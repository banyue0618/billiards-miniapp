package org.dromara.billiards.schedule.task;

import jakarta.annotation.Resource;
import org.dromara.billiards.schedule.annotation.ScheduledTask;
import org.dromara.billiards.service.PaymentMonitorService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 支付结果检测任务
 *
 * @author zhangsip
 * @version V1.0.0
 * @since 1.0
 * @date 2025/11/21
 */
@Component
@ScheduledTask(
    taskCode = "PAYMENT_RESULT",
    taskName = "支付结果检测任务",
    initialDelayMinutes = 2,
    intervalMinutes = 3,
    configProperty = "billiards.schedule.pay-reconcile-interval-minutes"
)
public class PaymentResultTaskRunner implements ScheduledTaskRunner {

    @Resource
    private PaymentMonitorService paymentMonitorService;

    @Override
    @Transactional
    public void execute() {
        paymentMonitorService.reconcilePayingRecords();
    }
}
