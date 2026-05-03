package com.dataasset.security.controller;

import com.dataasset.security.common.annotation.AuditLog;
import com.dataasset.security.common.enums.ObjectTypeEnum;
import com.dataasset.security.common.enums.OperationTypeEnum;
import com.dataasset.security.common.result.Result;
import com.dataasset.security.dto.MfaEnableDTO;
import com.dataasset.security.dto.MfaSetupDTO;
import com.dataasset.security.dto.MfaVerifyDTO;
import com.dataasset.security.security.CustomUserDetails;
import com.dataasset.security.service.MfaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 多因素认证控制器
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Slf4j
@RestController
@RequestMapping("/mfa")
@RequiredArgsConstructor
@Tag(name = "多因素认证", description = "多因素认证相关接口")
public class MfaController {

    private final MfaService mfaService;

    /**
     * 生成MFA设置信息
     */
    @GetMapping("/setup")
    @Operation(summary = "生成MFA设置信息", description = "为当前用户生成MFA设置信息，包含密钥和二维码")
    public Result<MfaSetupDTO> generateMfaSetup() {
        log.info("生成MFA设置信息请求");
        try {
            Long userId = getCurrentUserId();
            if (userId == null) {
                return Result.error("未登录或会话已过期");
            }

            MfaSetupDTO setupDTO = mfaService.generateMfaSetup(userId);
            return Result.success(setupDTO);
        } catch (Exception e) {
            log.error("生成MFA设置信息失败：{}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 启用MFA
     */
    @PostMapping("/enable")
    @Operation(summary = "启用MFA", description = "启用多因素认证")
    @AuditLog(operationType = OperationTypeEnum.UPDATE, objectType = ObjectTypeEnum.USER, description = "启用MFA")
    public Result<Void> enableMfa(@Valid @RequestBody MfaEnableDTO enableDTO) {
        log.info("启用MFA请求");
        try {
            Long userId = getCurrentUserId();
            if (userId == null) {
                return Result.error("未登录或会话已过期");
            }

            boolean success = mfaService.enableMfa(userId, enableDTO.getMfaType(),
                    enableDTO.getSecret(), enableDTO.getVerificationCode());
            if (success) {
                return Result.success("MFA启用成功");
            } else {
                return Result.error("MFA启用失败");
            }
        } catch (Exception e) {
            log.error("启用MFA失败：{}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 验证MFA验证码
     */
    @PostMapping("/verify")
    @Operation(summary = "验证MFA验证码", description = "验证多因素认证验证码")
    public Result<Boolean> verifyMfaCode(@Valid @RequestBody MfaVerifyDTO verifyDTO) {
        log.info("验证MFA验证码请求");
        try {
            Long userId = verifyDTO.getUserId();
            if (userId == null) {
                userId = getCurrentUserId();
                if (userId == null) {
                    return Result.error("未登录或会话已过期");
                }
            }

            boolean isValid;
            if (Boolean.TRUE.equals(verifyDTO.getUseBackupCode())) {
                // 使用备用码验证
                isValid = mfaService.verifyBackupCode(userId, verifyDTO.getCode());
            } else {
                // 使用TOTP验证码验证
                isValid = mfaService.verifyMfaCode(userId, verifyDTO.getCode());
            }

            return Result.success(isValid);
        } catch (Exception e) {
            log.error("验证MFA验证码失败：{}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 禁用MFA
     */
    @PostMapping("/disable")
    @Operation(summary = "禁用MFA", description = "禁用多因素认证")
    @AuditLog(operationType = OperationTypeEnum.UPDATE, objectType = ObjectTypeEnum.USER, description = "禁用MFA")
    public Result<Void> disableMfa(@RequestParam String password) {
        log.info("禁用MFA请求");
        try {
            Long userId = getCurrentUserId();
            if (userId == null) {
                return Result.error("未登录或会话已过期");
            }

            boolean success = mfaService.disableMfa(userId, password);
            if (success) {
                return Result.success("MFA禁用成功");
            } else {
                return Result.error("MFA禁用失败");
            }
        } catch (Exception e) {
            log.error("禁用MFA失败：{}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 检查是否启用了MFA
     */
    @GetMapping("/status")
    @Operation(summary = "检查MFA状态", description = "检查当前用户是否启用了MFA")
    public Result<Boolean> checkMfaStatus() {
        log.info("检查MFA状态请求");
        try {
            Long userId = getCurrentUserId();
            if (userId == null) {
                return Result.error("未登录或会话已过期");
            }

            boolean enabled = mfaService.isMfaEnabled(userId);
            return Result.success(enabled);
        } catch (Exception e) {
            log.error("检查MFA状态失败：{}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取MFA配置
     */
    @GetMapping("/config")
    @Operation(summary = "获取MFA配置", description = "获取当前用户的MFA配置信息")
    public Result<MfaSetupDTO> getMfaConfig() {
        log.info("获取MFA配置请求");
        try {
            Long userId = getCurrentUserId();
            if (userId == null) {
                return Result.error("未登录或会话已过期");
            }

            MfaSetupDTO config = mfaService.getMfaConfig(userId);
            return Result.success(config);
        } catch (Exception e) {
            log.error("获取MFA配置失败：{}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 生成备用码
     */
    @PostMapping("/backup-codes")
    @Operation(summary = "生成备用码", description = "为当前用户生成新的备用码")
    @AuditLog(operationType = OperationTypeEnum.UPDATE, objectType = ObjectTypeEnum.USER, description = "生成MFA备用码")
    public Result<String[]> generateBackupCodes() {
        log.info("生成备用码请求");
        try {
            Long userId = getCurrentUserId();
            if (userId == null) {
                return Result.error("未登录或会话已过期");
            }

            String[] backupCodes = mfaService.generateBackupCodes(userId);
            return Result.success(backupCodes);
        } catch (Exception e) {
            log.error("生成备用码失败：{}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取当前登录用户ID
     */
    private Long getCurrentUserId() {
        try {
            CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                    .getAuthentication().getPrincipal();
            return userDetails.getUserId();
        } catch (Exception e) {
            log.error("获取当前用户ID失败：{}", e.getMessage());
            return null;
        }
    }
}
