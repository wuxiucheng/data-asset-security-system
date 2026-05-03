package com.dataasset.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.dataasset.security.dto.MfaEnableDTO;
import com.dataasset.security.dto.MfaSetupDTO;
import com.dataasset.security.dto.MfaVerifyDTO;
import com.dataasset.security.entity.AuthMfaConfig;
import com.dataasset.security.entity.SysUser;
import com.dataasset.security.mapper.AuthMfaConfigMapper;
import com.dataasset.security.mapper.SysUserMapper;
import com.dataasset.security.security.CustomUserDetails;
import com.dataasset.security.service.MfaService;
import com.dataasset.security.service.SysUserService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 多因素认证服务实现类
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MfaServiceImpl implements MfaService {

    private final AuthMfaConfigMapper authMfaConfigMapper;
    private final SysUserMapper sysUserMapper;
    private final SysUserService sysUserService;
    private final PasswordEncoder passwordEncoder;
    private final GoogleAuthenticator gAuth = new GoogleAuthenticator();

    private static final String ISSUER = "DataAssetSecurity";
    private static final int BACKUP_CODES_COUNT = 10;
    private static final int BACKUP_CODE_LENGTH = 8;

    @Override
    public MfaSetupDTO generateMfaSetup(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 生成TOTP密钥
        GoogleAuthenticatorKey key = gAuth.createCredentials();

        // 生成二维码URL
        String qrCodeUrl = GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL(
                ISSUER,
                user.getUsername(),
                key
        );

        // 生成二维码图片（Base64）
        String qrCodeImage = generateQrCodeImage(qrCodeUrl);

        // 构建返回结果
        MfaSetupDTO setupDTO = new MfaSetupDTO();
        setupDTO.setUserId(userId);
        setupDTO.setUsername(user.getUsername());
        setupDTO.setMfaType("TOTP");
        setupDTO.setSecret(key.getKey());
        setupDTO.setQrCodeUrl(qrCodeUrl);
        setupDTO.setQrCodeImage(qrCodeImage);
        setupDTO.setEnabled(false);

        // 检查是否已启用MFA
        setupDTO.setEnabled(isMfaEnabled(userId));

        log.info("为用户{}生成MFA设置信息", user.getUsername());
        return setupDTO;
    }

    @Override
    @Transactional
    public boolean enableMfa(Long userId, String mfaType, String secret, String verificationCode) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 验证验证码
        if (!verifyCode(secret, verificationCode)) {
            log.warn("MFA验证码验证失败：userId={}, 输入验证码={}", userId, verificationCode);
            throw new RuntimeException("验证码错误，请确认验证码是否正确且未过期（验证码每30秒刷新一次）");
        }

        // 检查是否已启用MFA
        AuthMfaConfig existingConfig = authMfaConfigMapper.selectOne(
                new LambdaQueryWrapper<AuthMfaConfig>()
                        .eq(AuthMfaConfig::getUserId, userId)
                        .eq(AuthMfaConfig::getStatus, "ACTIVE")
        );

        if (existingConfig != null) {
            // 更新现有配置
            existingConfig.setMfaType(mfaType);
            existingConfig.setSecretKey(secret);
            existingConfig.setLastVerifiedTime(LocalDateTime.now());
            authMfaConfigMapper.updateById(existingConfig);
        } else {
            // 创建新配置
            AuthMfaConfig config = new AuthMfaConfig();
            config.setUserId(userId);
            config.setMfaType(mfaType);
            config.setSecretKey(secret);
            config.setStatus("ACTIVE");
            config.setEnabledTime(LocalDateTime.now());
            config.setLastVerifiedTime(LocalDateTime.now());

            // 生成备用码
            String[] backupCodes = generateBackupCodes(userId);
            config.setBackupCodes(String.join(",", backupCodes));

            authMfaConfigMapper.insert(config);
        }

        log.info("用户{}启用MFA成功", user.getUsername());
        return true;
    }

    @Override
    public boolean verifyMfaCode(Long userId, String code) {
        AuthMfaConfig config = authMfaConfigMapper.selectOne(
                new LambdaQueryWrapper<AuthMfaConfig>()
                        .eq(AuthMfaConfig::getUserId, userId)
                        .eq(AuthMfaConfig::getStatus, "ACTIVE")
        );

        if (config == null) {
            log.warn("用户{}未启用MFA", userId);
            return false;
        }

        // 验证TOTP验证码
        boolean isValid = verifyCode(config.getSecretKey(), code);

        if (isValid) {
            // 更新最后验证时间
            config.setLastVerifiedTime(LocalDateTime.now());
            authMfaConfigMapper.updateById(config);
            log.info("用户{}MFA验证成功", userId);
        } else {
            log.warn("用户{}MFA验证失败", userId);
        }

        return isValid;
    }

    @Override
    @Transactional
    public boolean disableMfa(Long userId, String password) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        // 删除或禁用MFA配置
        int updated = authMfaConfigMapper.update(null,
                new LambdaUpdateWrapper<AuthMfaConfig>()
                        .eq(AuthMfaConfig::getUserId, userId)
                        .set(AuthMfaConfig::getStatus, "INACTIVE")
        );

        log.info("用户{}禁用MFA成功", user.getUsername());
        return updated > 0;
    }

    @Override
    public boolean isMfaEnabled(Long userId) {
        Long count = authMfaConfigMapper.selectCount(
                new LambdaQueryWrapper<AuthMfaConfig>()
                        .eq(AuthMfaConfig::getUserId, userId)
                        .eq(AuthMfaConfig::getStatus, "ACTIVE")
        );
        return count > 0;
    }

    @Override
    public MfaSetupDTO getMfaConfig(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        AuthMfaConfig config = authMfaConfigMapper.selectOne(
                new LambdaQueryWrapper<AuthMfaConfig>()
                        .eq(AuthMfaConfig::getUserId, userId)
                        .eq(AuthMfaConfig::getStatus, "ACTIVE")
        );

        MfaSetupDTO setupDTO = new MfaSetupDTO();
        setupDTO.setUserId(userId);
        setupDTO.setUsername(user.getUsername());
        setupDTO.setEnabled(config != null);

        if (config != null) {
            setupDTO.setMfaType(config.getMfaType());
            setupDTO.setSecret(config.getSecretKey());
            // 不返回密钥和备用码，只显示是否启用
        }

        return setupDTO;
    }

    @Override
    public String[] generateBackupCodes(Long userId) {
        String[] backupCodes = new String[BACKUP_CODES_COUNT];
        Random random = new Random();

        for (int i = 0; i < BACKUP_CODES_COUNT; i++) {
            StringBuilder code = new StringBuilder();
            for (int j = 0; j < BACKUP_CODE_LENGTH; j++) {
                code.append(random.nextInt(10));
            }
            backupCodes[i] = code.toString();
        }

        log.info("为用户{}生成备用码", userId);
        return backupCodes;
    }

    @Override
    @Transactional
    public boolean verifyBackupCode(Long userId, String backupCode) {
        AuthMfaConfig config = authMfaConfigMapper.selectOne(
                new LambdaQueryWrapper<AuthMfaConfig>()
                        .eq(AuthMfaConfig::getUserId, userId)
                        .eq(AuthMfaConfig::getStatus, "ACTIVE")
        );

        if (config == null || !StringUtils.hasText(config.getBackupCodes())) {
            log.warn("用户{}备用码验证失败：未找到配置", userId);
            return false;
        }

        String[] backupCodes = config.getBackupCodes().split(",");
        boolean isValid = false;

        for (int i = 0; i < backupCodes.length; i++) {
            if (backupCode.equals(backupCodes[i])) {
                isValid = true;
                // 删除已使用的备用码
                backupCodes[i] = "USED";
                break;
            }
        }

        if (isValid) {
            // 更新备用码
            config.setBackupCodes(String.join(",", backupCodes));
            config.setLastVerifiedTime(LocalDateTime.now());
            authMfaConfigMapper.updateById(config);
            log.info("用户{}备用码验证成功", userId);
        } else {
            log.warn("用户{}备用码验证失败", userId);
        }

        return isValid;
    }

    /**
     * 验证TOTP验证码（允许前后各1个时间窗口的容错）
     */
    private boolean verifyCode(String secret, String code) {
        try {
            int codeInt = Integer.parseInt(code);
            // 先验证当前窗口
            if (gAuth.authorize(secret, codeInt)) {
                return true;
            }
            // 容错：验证前一个窗口和后一个窗口（允许时钟偏移）
            return gAuth.authorize(secret, codeInt, 1);
        } catch (NumberFormatException e) {
            log.error("验证码格式错误：{}", code);
            return false;
        }
    }

    /**
     * 生成二维码图片（Base64）
     */
    private String generateQrCodeImage(String qrCodeUrl) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 1);

            BitMatrix bitMatrix = qrCodeWriter.encode(qrCodeUrl, BarcodeFormat.QR_CODE, 200, 200, hints);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(MatrixToImageWriter.toBufferedImage(bitMatrix), "PNG", outputStream);

            return "data:image/png;base64," + Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (Exception e) {
            log.error("生成二维码失败：{}", e.getMessage());
            throw new RuntimeException("生成二维码失败", e);
        }
    }

    /**
     * 获取当前登录用户ID
     */
    private Long getCurrentUserId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                return userDetails.getUserId();
            }
        } catch (Exception e) {
            log.error("获取当前用户ID失败：{}", e.getMessage());
        }
        return null;
    }
}
