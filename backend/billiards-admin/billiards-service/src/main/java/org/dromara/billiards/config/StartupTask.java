package org.dromara.billiards.config;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.dromara.billiards.service.OrderService;
import org.dromara.billiards.service.PaymentMonitorService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;

/**
 * @Description
 * @Author zhangsip
 * @Version V1.0.0
 * @Since 1.0
 * @Date 2025/6/8
 */
@Component
@Slf4j
public class StartupTask implements CommandLineRunner {

    @Resource
    private OrderService orderService;

    @Resource
    private ScheduledExecutorService scheduledExecutorService;

    @Resource
    private PaymentMonitorService paymentMonitorService;

    @Value("${billiards.schedule.pay-timeout-interval-minutes:30}")
    private long payTimeoutIntervalMinutes;

    @Value("${billiards.schedule.pay-reconcile-interval-minutes:3}")
    private long payReconcileIntervalMinutes;

    @Value("${billiards.schedule.refund-failed-scan-interval-minutes:30}")
    private long refundFailedScanIntervalMinutes;

    @Override
    public void run(String... args) throws Exception {

        // 检测系统中进行的订单，每五分钟检测一次
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            try {
                orderService.detectOrders();
            } catch (Exception e) {
                // 记录异常日志
                log.error("订单检测任务执行异常: {}", e.getMessage(), e);
            }
        }, 0, 5, java.util.concurrent.TimeUnit.MINUTES);

        log.info("订单检测任务已启动，每五分钟执行一次");

        // 支付超时检测任务（默认每10分钟，可配置）
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            try {
                paymentMonitorService.detectPaymentTimeouts();
            } catch (Exception e) {
                log.error("支付超时检测任务执行异常: {}", e.getMessage(), e);
            }
        }, 1, payTimeoutIntervalMinutes, TimeUnit.MINUTES);

        // 支付中记录轮询任务（默认每3分钟，可配置）
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            try {
                paymentMonitorService.reconcilePayingRecords();
            } catch (Exception e) {
                log.error("支付中状态轮询任务执行异常: {}", e.getMessage(), e);
            }
        }, 2, payReconcileIntervalMinutes, TimeUnit.MINUTES);

        // 退款失败扫描任务（默认每30分钟，可配置）
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            try {
                paymentMonitorService.scanRefundFailures();
            } catch (Exception e) {
                log.error("退款失败扫描任务执行异常: {}", e.getMessage(), e);
            }
        }, 3, refundFailedScanIntervalMinutes, TimeUnit.MINUTES);

        log.info("支付/退款监控任务已启动：超时检测({}m)、支付中轮询({}m)、退款失败扫描({}m)", payTimeoutIntervalMinutes, payReconcileIntervalMinutes, refundFailedScanIntervalMinutes);
    }
}
