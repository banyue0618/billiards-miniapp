package org.dromara.billiards.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.billiards.schedule.task.ScheduledTaskRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 定时任务注册器
 * 自动发现并注册所有实现了 ScheduledTask 接口的任务
 * 
 * @author zhangsip
 * @version V1.0.0
 * @since 1.0
 * @date 2025/11/21
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TaskRegistry {
    
    private final ApplicationContext applicationContext;
    
    @Value("${billiards.schedule.enabled:true}")
    private boolean scheduleEnabled;
    
    private final List<TaskRegistration> registrations = new ArrayList<>();
    
    @PostConstruct
    public void init() {
        if (!scheduleEnabled) {
            log.info("定时任务功能已禁用，跳过任务注册");
            return;
        }
        
        // 自动发现所有实现了 ScheduledTaskRunner 接口的 Bean
        Map<String, ScheduledTaskRunner> taskBeans = applicationContext.getBeansOfType(ScheduledTaskRunner.class);
        
        log.info("发现 {} 个定时任务实现类", taskBeans.size());
        
        for (Map.Entry<String, ScheduledTaskRunner> entry : taskBeans.entrySet()) {
            ScheduledTaskRunner taskRunner = entry.getValue();
            Class<?> taskClass = taskRunner.getClass();
            
            // 获取 @ScheduledTask 注解
            org.dromara.billiards.schedule.annotation.ScheduledTask annotation = 
                    AnnotationUtils.findAnnotation(taskClass, org.dromara.billiards.schedule.annotation.ScheduledTask.class);
            
            if (annotation == null) {
                log.warn("任务类 {} 未标注 @ScheduledTask 注解，跳过注册", taskClass.getName());
                continue;
            }
            
            // 构建任务元数据
            TaskMetadata metadata = buildMetadata(annotation);
            
            // 创建任务注册信息
            TaskRegistration registration = TaskRegistration.builder()
                    .taskRunner(taskRunner)
                    .metadata(metadata)
                    .build();
            
            registrations.add(registration);
            
            log.info("注册定时任务: {} ({}) - 初始延迟: {}分钟, 执行间隔: {}分钟, 启用: {}", 
                    metadata.getTaskName(), 
                    metadata.getTaskCode(),
                    metadata.getInitialDelayMinutes(),
                    metadata.getIntervalMinutes(),
                    metadata.isEnabled());
        }
        
        log.info("定时任务注册完成，共注册 {} 个任务", registrations.size());
    }
    
    /**
     * 构建任务元数据
     * 优先从配置文件读取，如果配置不存在则使用注解中的默认值
     */
    private TaskMetadata buildMetadata(org.dromara.billiards.schedule.annotation.ScheduledTask annotation) {
        long intervalMinutes = annotation.intervalMinutes();
        
        // 如果配置了 configProperty，尝试从配置文件中读取
        if (!annotation.configProperty().isEmpty()) {
            try {
                String propertyValue = applicationContext.getEnvironment()
                        .getProperty(annotation.configProperty());
                if (propertyValue != null && !propertyValue.isEmpty()) {
                    intervalMinutes = Long.parseLong(propertyValue);
                    log.debug("从配置文件读取任务间隔: {} = {} 分钟", 
                            annotation.configProperty(), intervalMinutes);
                }
            } catch (Exception e) {
                log.warn("读取配置属性 {} 失败，使用注解默认值: {}", 
                        annotation.configProperty(), intervalMinutes, e);
            }
        }
        
        return TaskMetadata.builder()
                .taskCode(annotation.taskCode())
                .taskName(annotation.taskName())
                .initialDelayMinutes(annotation.initialDelayMinutes())
                .intervalMinutes(intervalMinutes)
                .enabled(annotation.enabled())
                .build();
    }
    
    /**
     * 获取所有已注册的任务
     */
    public List<TaskRegistration> getRegistrations() {
        return new ArrayList<>(registrations);
    }
}
