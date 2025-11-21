package org.dromara.billiards.schedule.task;

import jakarta.annotation.Resource;
import org.dromara.billiards.schedule.annotation.ScheduledTask;
import org.dromara.billiards.service.IBlsReservationService;
import org.springframework.stereotype.Component;

/**
 * 预约过期检查任务
 *
 * @author zhangsip
 * @version V1.0.0
 * @since 1.0
 * @date 2025/11/21
 */
@Component
@ScheduledTask(
    taskCode = "RESERVE_TIMEOUT",
    taskName = "预约过期检查任务",
    initialDelayMinutes = 4,
    intervalMinutes = 5,
    configProperty = "billiards.schedule.reservation-expire-interval-minutes"
)
public class ReservationCheckTaskRunner implements ScheduledTaskRunner {

    @Resource
    private IBlsReservationService reservationService;

    @Override
    public void execute() {
        reservationService.checkAndExpireReservations();
    }
}
