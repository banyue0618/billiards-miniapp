package org.dromara.billiards.schedule.task;

/**
 * 定时任务执行器接口，所有定时任务实现类都需要实现此接口
 * 
 * @author zhangsip
 * @version V1.0.0
 * @since 1.0
 * @date 2025/11/21
 */
public interface ScheduledTaskRunner {
    
    /**
     * 执行任务逻辑
     */
    void execute();
}
