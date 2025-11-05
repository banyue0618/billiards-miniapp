package org.dromara.billiards.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Description
 * @Author zhangsip
 * @Version V1.0.0
 * @Since 1.0
 * @Date 2025/11/5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("bls_schedule_task_log")
public class BlsScheduleTaskLog {

    private Long id;
    private String taskCode;
    private String taskName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long durationMs;
    private String status;
    private String errorMsg;
    private LocalDateTime createTime;
    private String remark;

}
