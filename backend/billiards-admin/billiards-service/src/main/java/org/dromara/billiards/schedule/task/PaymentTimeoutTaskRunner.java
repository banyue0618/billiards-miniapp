package org.dromara.billiards.schedule.task;

import jakarta.annotation.Resource;
import org.dromara.billiards.schedule.annotation.ScheduledTask;
import org.dromara.billiards.service.PaymentMonitorService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 支付超时检测任务
 *
 * @author zhangsip
 * @version V1.0.0
 * @since 1.0
 * @date 2025/11/21
 */
@Component
@ScheduledTask(
    taskCode = "PAYMENT_TIMEOUT",
    taskName = "支付超时检测任务",
    initialDelayMinutes = 1,
    intervalMinutes = 10,
    configProperty = "billiards.schedule.pay-timeout-interval-minutes"
)
public class PaymentTimeoutTaskRunner implements ScheduledTaskRunner {

    @Resource
    private PaymentMonitorService paymentMonitorService;

    @Override
    @Transactional
    public void execute() {
        paymentMonitorService.detectPaymentTimeouts();
    }
}
