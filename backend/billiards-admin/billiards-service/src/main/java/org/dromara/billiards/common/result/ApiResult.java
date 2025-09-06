package org.dromara.billiards.common.result;

import org.dromara.common.core.constant.HttpStatus;
import org.dromara.common.core.domain.R;

/**
 * API响应结果门面类
 * 内部使用RuoYi框架的R类，保持API兼容性
 */
public class ApiResult {

    /**
     * 成功返回（无数据）
     */
    public static <T> R<T> success() {
        return R.ok();
    }

    /**
     * 成功返回（有数据）
     */
    public static <T> R<T> success(T data) {
        return R.ok(data);
    }

    /**
     * 成功返回（自定义消息）
     */
    public static <T> R<T> success(String message) {
        return R.ok(message);
    }

    /**
     * 成功返回（自定义消息和数据）
     */
    public static <T> R<T> success(String message, T data) {
        return R.ok(message, data);
    }

    /**
     * 失败返回（自定义消息）
     */
    public static <T> R<T> error(String message) {
        return R.fail(message);
    }

    /**
     * 失败返回（使用预定义的错误码）
     */
    public static <T> R<T> error(IResultCode resultCode) {
        return R.fail(resultCode.getCode(), resultCode.getMessage());
    }

    /**
     * 失败返回（自定义错误码和消息）
     */
    public static <T> R<T> error(Integer code, String message) {
        return R.fail(code, message);
    }

    /**
     * 失败返回（无参数）
     */
    public static <T> R<T> error() {
        return R.fail();
    }

    /**
     * 未授权返回
     */
    public static <T> R<T> unauthorized(String message) {
        return R.fail(HttpStatus.UNAUTHORIZED, message);
    }

    /**
     * 禁止访问返回
     */
    public static <T> R<T> forbidden(String message) {
        return R.fail(HttpStatus.FORBIDDEN, message);
    }

    /**
     * 参数错误返回
     */
    public static <T> R<T> paramError(String message) {
        return R.fail(HttpStatus.BAD_REQUEST, message);
    }

    /**
     * 判断是否成功
     */
    public static <T> boolean isSuccess(R<T> r) {
        return R.isSuccess(r);
    }

    /**
     * 判断是否失败
     */
    public static <T> boolean isError(R<T> r) {
        return R.isError(r);
    }
}
