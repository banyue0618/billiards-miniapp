package org.dromara.billiards.schedule;

import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.billiards.schedule.task.ScheduledTaskRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 系统定时任务初始化器
 * 自动发现并注册所有实现了 ScheduledTaskRunner 接口的任务，在应用启动时初始化并开始运行
 * 
 * 优化说明：
 * 1. 使用策略模式，所有任务实现统一的 ScheduledTaskRunner 接口
 * 2. 使用注解驱动，通过 @ScheduledTask 注解配置任务元数据
 * 3. 自动发现机制，无需手动注入每个 TaskRunner
 * 4. 配置驱动，支持从配置文件读取任务执行间隔
 * 
 * 后期如有必要，增加定时任务管理界面，可动态配置和控制定时任务的启停和参数调整，同时记录任务执行日志
 * 
 * @author zhangsip
 * @version V1.0.0
 * @since 1.0
 * @date 2025/6/8
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ScheduledTaskInitializer implements CommandLineRunner {

    private final ScheduledExecutorService scheduledExecutorService;
    private final TaskRegistry taskRegistry;
    private final ScheduleTaskLogger taskLogger;

    @Override
    public void run(String... args) throws Exception {
        // 获取所有已注册的任务
        var registrations = taskRegistry.getRegistrations();
        
        if (registrations.isEmpty()) {
            log.warn("未发现任何定时任务，跳过初始化");
            return;
        }
        
        log.info("开始初始化 {} 个定时任务", registrations.size());
        
        // 遍历所有任务并注册到调度器
        for (TaskRegistration registration : registrations) {
            ScheduledTaskRunner taskRunner = registration.getTaskRunner();
            TaskMetadata metadata = registration.getMetadata();
            
            // 如果任务未启用，跳过注册
            if (!metadata.isEnabled()) {
                log.info("任务 {} ({}) 已禁用，跳过注册", 
                        metadata.getTaskName(), metadata.getTaskCode());
                continue;
            }
            
            // 注册任务到调度器
            scheduledExecutorService.scheduleAtFixedRate(
                    () -> {
                        taskLogger.record(
                                metadata.getTaskCode(),
                                metadata.getTaskName(),
                                taskRunner::execute
                        );
                    },
                    metadata.getInitialDelayMinutes(),
                    metadata.getIntervalMinutes(),
                    TimeUnit.MINUTES
            );
            
            log.info("定时任务已启动: {} ({}) - 初始延迟: {}分钟, 执行间隔: {}分钟",
                    metadata.getTaskName(),
                    metadata.getTaskCode(),
                    metadata.getInitialDelayMinutes(),
                    metadata.getIntervalMinutes());
        }
        
        log.info("定时任务初始化完成，共启动 {} 个任务", registrations.size());
    }
}
