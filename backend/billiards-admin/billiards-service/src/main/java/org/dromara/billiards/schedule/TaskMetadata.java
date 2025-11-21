package org.dromara.billiards.schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 定时任务元数据
 * 封装任务的配置信息，包括任务代码、名称、执行间隔等
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
public class TaskMetadata {
    
    /**
     * 任务代码
     */
    private String taskCode;
    
    /**
     * 任务名称
     */
    private String taskName;
    
    /**
     * 初始延迟时间（分钟）
     */
    private long initialDelayMinutes;
    
    /**
     * 执行间隔时间（分钟）
     */
    private long intervalMinutes;
    
    /**
     * 是否启用
     */
    private boolean enabled;
}
