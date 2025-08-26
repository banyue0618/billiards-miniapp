package org.dromara.billiards.common.result;

/**
 * 响应码接口
 */
public interface IResultCode {

    /**
     * 获取响应码
     */
    Integer getCode();

    /**
     * 获取响应信息
     */
    String getMessage();
}
