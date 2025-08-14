package org.dromara.billiards.config;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.dromara.billiards.service.OrderService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledExecutorService;

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
    }
}
