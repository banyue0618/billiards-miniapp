package org.dromara.billiards.common.result;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应结果类
 */
@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 消息内容
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 时间戳
     */
    private long timestamp = System.currentTimeMillis();

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 构造方法私有化，不允许外部实例化
     */
    private Result() {}

    /**
     * 构建响应对象
     */
    public static <T> Result<T> build(Integer code, String message, T data, boolean success) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(data);
        result.setSuccess(success);
        return result;
    }

    /**
     * 成功返回（无数据）
     */
    public static <T> Result<T> success() {
        return build(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), null, true);
    }

    /**
     * 成功返回（有数据）
     */
    public static <T> Result<T> success(T data) {
        return build(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data, true);
    }

    /**
     * 失败返回（自定义消息）
     */
    public static <T> Result<T> error(String message) {
        return build(ResultCode.ERROR.getCode(), message, null, false);
    }

    /**
     * 失败返回（使用预定义的错误码）
     */
    public static <T> Result<T> error(IResultCode resultCode) {
        return build(resultCode.getCode(), resultCode.getMessage(), null, false);
    }

    /**
     * 失败返回（自定义错误码和消息）
     */
    public static <T> Result<T> error(Integer code, String message) {
        return build(code, message, null, false);
    }

    /**
     * 判断是否成功
     */
    public boolean isSuccess() {
        return this.code.equals(ResultCode.SUCCESS.getCode());
    }
}
