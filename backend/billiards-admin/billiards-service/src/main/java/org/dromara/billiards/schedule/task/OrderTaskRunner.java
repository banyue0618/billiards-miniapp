package org.dromara.billiards.schedule.task;

import jakarta.annotation.Resource;
import org.dromara.billiards.schedule.annotation.ScheduledTask;
import org.dromara.billiards.service.OrderService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 订单检测任务
 *
 * @author zhangsip
 * @version V1.0.0
 * @since 1.0
 * @date 2025/11/21
 */
@Component
@ScheduledTask(
    taskCode = "ORDER_DETECT",
    taskName = "订单检测任务",
    initialDelayMinutes = 0,
    intervalMinutes = 5
)
public class OrderTaskRunner implements ScheduledTaskRunner {

    @Resource
    private OrderService orderService;

    @Override
    @Transactional
    public void execute() {
        orderService.detectOrders();
    }
}
