package org.dromara.billiards.schedule.task;

import jakarta.annotation.Resource;
import org.dromara.billiards.schedule.annotation.ScheduledTask;
import org.dromara.billiards.service.PaymentMonitorService;
import org.springframework.stereotype.Component;

/**
 * 退款失败检测任务
 *
 * @author zhangsip
 * @version V1.0.0
 * @since 1.0
 * @date 2025/11/21
 */
@Component
@ScheduledTask(
    taskCode = "REFUND_DETECT",
    taskName = "退款失败检测任务",
    initialDelayMinutes = 3,
    intervalMinutes = 30,
    configProperty = "billiards.schedule.refund-failed-scan-interval-minutes"
)
public class RefundFailureTaskRunner implements ScheduledTaskRunner {

    @Resource
    private PaymentMonitorService paymentMonitorService;

    @Override
    public void execute() {
        paymentMonitorService.scanRefundFailures();
    }
}
