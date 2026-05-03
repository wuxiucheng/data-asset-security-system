package com.dataasset.security.service;

import com.dataasset.security.dto.MfaSetupDTO;
import com.dataasset.security.dto.MfaVerifyDTO;

/**
 * 多因素认证服务接口
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
public interface MfaService {

    /**
     * 生成MFA设置信息（包含密钥和二维码）
     *
     * @param userId 用户ID
     * @return MFA设置信息
     */
    MfaSetupDTO generateMfaSetup(Long userId);

    /**
     * 启用MFA
     *
     * @param userId 用户ID
     * @param mfaType MFA类型
     * @param secret 密钥
     * @param verificationCode 验证码
     * @return 是否启用成功
     */
    boolean enableMfa(Long userId, String mfaType, String secret, String verificationCode);

    /**
     * 验证MFA验证码
     *
     * @param userId 用户ID
     * @param code 验证码
     * @return 是否验证通过
     */
    boolean verifyMfaCode(Long userId, String code);

    /**
     * 禁用MFA
     *
     * @param userId 用户ID
     * @param password 密码（用于验证身份）
     * @return 是否禁用成功
     */
    boolean disableMfa(Long userId, String password);

    /**
     * 检查用户是否启用了MFA
     *
     * @param userId 用户ID
     * @return 是否启用MFA
     */
    boolean isMfaEnabled(Long userId);

    /**
     * 获取用户的MFA配置
     *
     * @param userId 用户ID
     * @return MFA配置信息
     */
    MfaSetupDTO getMfaConfig(Long userId);

    /**
     * 生成备用码
     *
     * @param userId 用户ID
     * @return 备用码列表
     */
    String[] generateBackupCodes(Long userId);

    /**
     * 验证备用码
     *
     * @param userId 用户ID
     * @param backupCode 备用码
     * @return 是否验证通过
     */
    boolean verifyBackupCode(Long userId, String backupCode);
}