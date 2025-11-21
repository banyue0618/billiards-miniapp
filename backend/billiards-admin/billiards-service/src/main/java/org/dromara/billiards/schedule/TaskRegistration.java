package org.dromara.billiards.schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dromara.billiards.schedule.task.ScheduledTaskRunner;

/**
 * 任务注册信息
 * 封装任务执行器和元数据
 * 
 * @author zhangsip
 * @version V1.0.0
 * @since 1.0
 * @date 2025/11/21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskRegistration {
    
    /**
     * 任务执行器
     */
    private ScheduledTaskRunner taskRunner;
    
    /**
     * 任务元数据
     */
    private TaskMetadata metadata;
}
