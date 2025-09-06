package org.dromara.billiards.service;

/**
 * 支付/退款监控服务
 */
public interface PaymentMonitorService {

    /**
     * 检测支付超时并标记为失败（备注超时）
     */
    void detectPaymentTimeouts();

    /**
     * 对支付中记录进行状态轮询并更新
     */
    void reconcilePayingRecords();

    /**
     * 扫描退款失败记录并记录待通知
     */
    void scanRefundFailures();
}


