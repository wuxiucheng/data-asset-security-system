package com.dataasset.security.common.result;

import lombok.Getter;

/**
 * 响应码枚举
 *
 * @author Data Asset Security Team
 * @since 2025-06-17
 */
@Getter
public enum ResultCode {

    /**
     * 成功
     */
    SUCCESS(200, "操作成功"),

    /**
     * 失败
     */
    ERROR(500, "操作失败"),

    /**
     * 参数错误
     */
    BAD_REQUEST(400, "请求参数错误"),

    /**
     * 未授权
     */
    UNAUTHORIZED(401, "未授权，请先登录"),

    /**
     * 禁止访问
     */
    FORBIDDEN(403, "禁止访问"),

    /**
     * 资源不存在
     */
    NOT_FOUND(404, "资源不存在"),

    /**
     * 方法不允许
     */
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),

    /**
     * 请求超时
     */
    REQUEST_TIMEOUT(408, "请求超时"),

    /**
     * 系统错误
     */
    INTERNAL_SERVER_ERROR(500, "系统内部错误"),

    /**
     * 服务不可用
     */
    SERVICE_UNAVAILABLE(503, "服务不可用"),

    /**
     * 网关超时
     */
    GATEWAY_TIMEOUT(504, "网关超时"),

    /**
     * 用户名或密码错误
     */
    USERNAME_OR_PASSWORD_ERROR(1001, "用户名或密码错误"),

    /**
     * 用户不存在
     */
    USER_NOT_EXIST(1002, "用户不存在"),

    /**
     * 用户已存在
     */
    USER_ALREADY_EXIST(1003, "用户已存在"),

    /**
     * 用户已禁用
     */
    USER_DISABLED(1004, "用户已被禁用"),

    /**
     * 用户已锁定
     */
    USER_LOCKED(1005, "用户已被锁定"),

    /**
     * 角色不存在
     */
    ROLE_NOT_EXIST(2001, "角色不存在"),

    /**
     * 权限不足
     */
    PERMISSION_DENIED(3001, "权限不足"),

    /**
     * 违反三权分立原则
     */
    SEPARATION_OF_DUTIES_VIOLATION(3002, "违反三权分立原则"),

    /**
     * 数据资产不存在
     */
    DATA_ASSET_NOT_EXIST(4001, "数据资产不存在"),

    /**
     * 数据资产已存在
     */
    DATA_ASSET_ALREADY_EXIST(4002, "数据资产已存在"),

    /**
     * 部门不存在
     */
    DEPARTMENT_NOT_EXIST(5001, "部门不存在"),

    /**
     * 责任人不存在
     */
    OWNER_NOT_EXIST(6001, "责任人不存在"),

    /**
     * 分类标准不存在
     */
    CLASSIFICATION_STANDARD_NOT_EXIST(7001, "分类标准不存在"),

    /**
     * 分类不存在
     */
    CLASSIFICATION_NOT_EXIST(7002, "分类不存在"),

    /**
     * 分级标准不存在
     */
    SECURITY_LEVEL_STANDARD_NOT_EXIST(8001, "分级标准不存在"),

    /**
     * 审批流程不存在
     */
    APPROVAL_PROCESS_NOT_EXIST(9001, "审批流程不存在"),

    /**
     * 审批流程已存在
     */
    APPROVAL_PROCESS_ALREADY_EXIST(9002, "审批流程已存在"),

    /**
     * 文件上传失败
     */
    FILE_UPLOAD_FAILED(10001, "文件上传失败"),

    /**
     * 文件下载失败
     */
    FILE_DOWNLOAD_FAILED(10002, "文件下载失败"),

    /**
     * 文件格式不支持
     */
    FILE_FORMAT_NOT_SUPPORTED(10003, "文件格式不支持"),

    /**
     * 数据导入失败
     */
    DATA_IMPORT_FAILED(11001, "数据导入失败"),

    /**
     * 数据导出失败
     */
    DATA_EXPORT_FAILED(11002, "数据导出失败");

    /**
     * 响应码
     */
    private final Integer code;

    /**
     * 响应消息
     */
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
