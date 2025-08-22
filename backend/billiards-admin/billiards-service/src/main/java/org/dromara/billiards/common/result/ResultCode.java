package org.dromara.billiards.common.result;

import org.dromara.common.core.constant.HttpStatus;

/**
 * 结果状态码枚举
 * 使用RuoYi框架的HttpStatus常量
 */
public enum ResultCode implements IResultCode {

    /**
     * 操作成功
     */
    SUCCESS(HttpStatus.SUCCESS, "操作成功"),

    /**
     * 操作失败
     */
    ERROR(HttpStatus.ERROR, "操作失败"),

    /**
     * 请求未授权
     */
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "请求未授权"),

    /**
     * 权限不足
     */
    FORBIDDEN(HttpStatus.FORBIDDEN, "权限不足"),

    /**
     * 请求参数错误
     */
    PARAM_ERROR(HttpStatus.BAD_REQUEST, "请求参数错误"),

    /**
     * 资源不存在
     */
    NOT_FOUND(HttpStatus.NOT_FOUND, "请求资源不存在"),

    /**
     * 服务器内部错误
     */
    INTERNAL_SERVER_ERROR(HttpStatus.ERROR, "服务器内部错误"),

    /**
     * 数据库操作失败
     */
    DATABASE_ERROR(1001, "数据库操作失败"),

    /**
     * 用户名或密码错误
     */
    INVALID_CREDENTIAL(1002, "用户名或密码错误"),

    /**
     * 账号已锁定
     */
    ACCOUNT_LOCKED(1003, "账号已锁定"),

    /**
     * 账号不存在
     */
    ACCOUNT_NOT_FOUND(1004, "账号不存在"),

    /**
     * 重复提交
     */
    DUPLICATE_SUBMISSION(1005, "请勿重复提交"),

    /**
     * 验证码错误
     */
    CAPTCHA_ERROR(1006, "验证码错误或已过期"),

    /**
     * 手机号已绑定
     */
    PHONE_ALREADY_BOUND(1007, "手机号已被其他账号绑定"),

    /**
     * 订单已过期
     */
    ORDER_EXPIRED(2001, "订单已过期"),

    /**
     * 订单状态错误
     */
    ORDER_STATUS_ERROR(2002, "订单状态错误"),

    /**
     * 桌台不可用
     */
    TABLE_UNAVAILABLE(2003, "桌台不可用"),

    /**
     * 余额不足
     */
    INSUFFICIENT_BALANCE(2004, "余额不足"),

    /**
     * 支付失败
     */
    PAYMENT_FAILED(2005, "支付失败"),

    /**
     * 桌台不存在
     */
    TABLE_NOT_EXIST(2006, "桌台不存在"),

    /**
     * 桌台已被占用
     */
    TABLE_OCCUPIED(2007, "桌台已被占用"),

    /**
     * 计费规则不存在
     */
    PRICE_RULE_NOT_EXIST(2008, "计费规则不存在"),

    /**
     * 有进行中的订单
     */
    ORDER_IN_PROGRESS(2009, "您已经有正在进行中的订单了"),

    /**
     * 订单不存在
     */
    ORDER_NOT_EXIST(2010, "订单不存在"),

    /**
     * 订单已结束
     */
    ORDER_ALREADY_ENDED(2011, "订单已结束"),

    /**
     * 文件上传失败
     */
    FILE_UPLOAD_ERROR(3001, "文件上传失败"),

    INVALID_CHANNEL(4031, "非法渠道"),

    INVALID_TIME_RANGE(5031, "非法时间区间"), ORDER_NOT_REFUNDING(2012, "订单未处于可退款状态"), REFUND_ABNORMAL(2013, "订单退款异常，请前往微信商户平台处理");

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 消息内容
     */
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public Integer getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
