package org.dromara.billiards.common.exception;

import org.dromara.billiards.common.result.IResultCode;
import org.dromara.common.core.exception.ServiceException;

/**
 * 台球厅业务异常类
 * 使用RuoYi框架的ServiceException
 */
public class BilliardsException {

    /**
     * 创建业务异常
     * @param message 错误消息
     * @return ServiceException 异常对象
     */
    public static ServiceException of(String message) {
        return new ServiceException(message);
    }

    /**
     * 创建业务异常
     * @param code 错误码
     * @param message 错误消息
     * @return ServiceException 异常对象
     */
    public static ServiceException of(Integer code, String message) {
        return new ServiceException(message, code);
    }

    /**
     * 创建业务异常
     * @param resultCode 结果码接口
     * @return ServiceException 异常对象
     */
    public static ServiceException of(IResultCode resultCode) {
        return new ServiceException(resultCode.getMessage(), resultCode.getCode());
    }

    /**
     * 创建业务异常
     * @param resultCode 结果码接口
     * @param detailMessage 详细错误信息
     * @return ServiceException 异常对象
     */
    public static ServiceException of(IResultCode resultCode, String detailMessage) {
        return new ServiceException(resultCode.getMessage(), resultCode.getCode())
                .setDetailMessage(detailMessage);
    }

    /**
     * 私有构造函数，防止实例化
     */
    private BilliardsException() {
    }
}
