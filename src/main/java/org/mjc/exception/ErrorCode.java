package org.mjc.exception;

/**
 * 错误码枚举
 *
 * @author Online_Voting
 * @since 2026-07-24
 */
public enum ErrorCode {

    // ========== 通用错误 ==========
    SUCCESS(200, "操作成功"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),
    INTERNAL_ERROR(500, "服务器内部错误"),

    // ========== 账号相关 1001-1099 ==========
    USER_NOT_FOUND(1001, "用户不存在"),
    USERNAME_EXISTS(1002, "用户名已存在"),
    USERNAME_OR_PASSWORD_ERROR(1003, "用户名或密码错误"),
    PASSWORD_ERROR(1004, "密码错误"),
    ACCOUNT_DISABLED(1005, "账号已被禁用"),
    TOKEN_EXPIRED(1006, "Token已过期，请重新登录"),
    TOKEN_INVALID(1007, "Token无效"),

    // ========== 投票相关 2001-2099 ==========
    VOTE_NOT_FOUND(2001, "投票不存在"),
    VOTE_NOT_STARTED(2002, "投票尚未开始"),
    VOTE_ENDED(2003, "投票已截止"),
    VOTE_ALREADY_VOTED(2004, "您已经投过票了"),
    VOTE_OPTIONS_INSUFFICIENT(2005, "投票选项至少需要2个"),
    VOTE_TITLE_EMPTY(2006, "投票标题不能为空"),
    VOTE_CREATE_FAILED(2007, "创建投票失败"),
    VOTE_UPDATE_FAILED(2008, "修改投票失败"),
    VOTE_DELETE_FAILED(2009, "删除投票失败"),

    // ========== 选项相关 3001-3099 ==========
    OPTION_NOT_FOUND(3001, "选项不存在"),
    OPTION_CREATE_FAILED(3002, "创建选项失败"),
    OPTION_UPDATE_FAILED(3003, "修改选项失败"),
    OPTION_DELETE_FAILED(3004, "删除选项失败"),

    // ========== 验证码相关 4001-4099 ==========
    CAPTCHA_EXPIRED(4001, "验证码已过期"),
    CAPTCHA_ERROR(4002, "验证码错误"),
    CAPTCHA_GENERATE_FAILED(4003, "生成验证码失败"),

    // ========== 导出/分享相关 5001-5099 ==========
    EXPORT_FAILED(5001, "导出失败"),
    QRCODE_GENERATE_FAILED(5002, "生成二维码失败"),
    SHARE_LINK_GENERATE_FAILED(5003, "生成分享链接失败");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
