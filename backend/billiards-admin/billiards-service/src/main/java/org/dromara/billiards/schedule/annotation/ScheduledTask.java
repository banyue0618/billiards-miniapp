package org.dromara.billiards.schedule.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 定时任务配置注解
 * 用于标注定时任务的元数据信息，包括任务代码、名称、初始延迟和执行间隔
 * 
 * @author zhangsip
 * @version V1.0.0
 * @since 1.0
 * @date 2025/11/21
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ScheduledTask {
    
    /**
     * 任务代码，用于日志记录和任务标识
     */
    String taskCode();
    
    /**
     * 任务名称，用于日志记录和显示
     */
    String taskName();
    
    /**
     * 初始延迟时间（分钟），任务启动后多久开始第一次执行
     * 默认值为 0，表示立即执行
     */
    long initialDelayMinutes() default 0;
    
    /**
     * 执行间隔时间（分钟），两次任务执行之间的间隔
     * 默认值为 5 分钟
     */
    long intervalMinutes() default 5;
    
    /**
     * 是否启用该任务
     * 默认值为 true
     */
    boolean enabled() default true;
    
    /**
     * 配置属性前缀，用于从配置文件中读取执行间隔
     * 例如：如果设置为 "billiards.schedule.order-interval-minutes"，
     * 系统会尝试从配置文件中读取该属性值来覆盖 intervalMinutes
     * 如果配置文件中不存在该属性，则使用 intervalMinutes 的默认值
     */
    String configProperty() default "";
}
