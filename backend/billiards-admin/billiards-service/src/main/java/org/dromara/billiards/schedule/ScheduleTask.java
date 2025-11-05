package org.dromara.billiards.schedule;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.dromara.billiards.service.OrderService;
import org.dromara.billiards.service.PaymentMonitorService;
import org.dromara.billiards.service.IBlsReservationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;

/**
 * @Description 系统定时任务,包括订单检测、支付监控、预约过期检查等，在应用启动时初始化并开始运行
 * 后期如有必要，增加定时任务管理界面，可动态配置和控制定时任务的启停和参数调整，同时记录任务执行日志
 * @Author zhangsip
 * @Version V1.0.0
 * @Since 1.0
 * @Date 2025/6/8
 */
@Component
@Slf4j
public class ScheduleTask implements CommandLineRunner {

    @Resource
    private OrderService orderService;

    @Resource
    private ScheduledExecutorService scheduledExecutorService;

    @Resource
    private PaymentMonitorService paymentMonitorService;

    @Resource
    private IBlsReservationService reservationService;

    @Resource
    private ScheduleTaskLogger taskLogger;

    @Value("${billiards.schedule.pay-timeout-interval-minutes:30}")
    private long payTimeoutIntervalMinutes;

    @Value("${billiards.schedule.pay-reconcile-interval-minutes:3}")
    private long payReconcileIntervalMinutes;

    @Value("${billiards.schedule.refund-failed-scan-interval-minutes:30}")
    private long refundFailedScanIntervalMinutes;

    @Value("${billiards.schedule.enabled:true}")
    private boolean enabled;

    @Value("${billiards.schedule.reservation-expire-interval-minutes:5}")
    private long reservationExpireIntervalMinutes;

    @Override
    public void run(String... args) throws Exception {
        if(!enabled){
            return;
        }
        // 检测系统中进行的订单，每五分钟检测一次
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            taskLogger.record("ORDER_DETECT", "订单检测任务", () -> {
                orderService.detectOrders();
            });
        }, 0, 5, java.util.concurrent.TimeUnit.MINUTES);

        log.info("订单检测任务已启动，每五分钟执行一次");

        // 支付超时检测任务（默认每10分钟，可配置）
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            taskLogger.record("PAYMENT_TIMEOUT", "支付超时检测任务", () -> {
                paymentMonitorService.detectPaymentTimeouts();
            });
        }, 1, payTimeoutIntervalMinutes, TimeUnit.MINUTES);

        // 支付中记录轮询任务（默认每3分钟，可配置）
        scheduledExecutorService.scheduleAtFixedRate(() -> {

            taskLogger.record("PAYMENT_RESULT", "支付结果检测任务", () -> {
                paymentMonitorService.reconcilePayingRecords();
            });
        }, 2, payReconcileIntervalMinutes, TimeUnit.MINUTES);

        // 退款失败扫描任务（默认每30分钟，可配置）
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            taskLogger.record("REFUND_DETECT", "退款失败检测任务", () -> {
                paymentMonitorService.scanRefundFailures();
            });
        }, 3, refundFailedScanIntervalMinutes, TimeUnit.MINUTES);

        log.info("支付/退款监控任务已启动：超时检测({}m)、支付中轮询({}m)、退款失败扫描({}m)", payTimeoutIntervalMinutes, payReconcileIntervalMinutes, refundFailedScanIntervalMinutes);

        // 预约过期检查任务（默认每5分钟，可配置，使用配置中的 autoCheckIntervalMinutes）
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            taskLogger.record("RESERVE_TIMEOUT", "预约过期检查任务", () -> {
                reservationService.checkAndExpireReservations();
            });
        }, 4, reservationExpireIntervalMinutes, TimeUnit.MINUTES);

        log.info("预约过期检查任务已启动，每 {} 分钟执行一次", reservationExpireIntervalMinutes);
    }
}
