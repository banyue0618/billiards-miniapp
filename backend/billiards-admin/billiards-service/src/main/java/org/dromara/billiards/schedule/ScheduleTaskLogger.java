package org.dromara.billiards.schedule;

import com.baomidou.dynamic.datasource.annotation.DS;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.billiards.common.constant.BilliardsConstants;
import org.dromara.billiards.domain.entity.BlsScheduleTaskLog;
import org.dromara.billiards.mapper.BlsScheduleTaskLogMapper;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * @Description
 * @Author zhangsip
 * @Version V1.0.0
 * @Since 1.0
 * @Date 2025/11/5
 */
@Component
@RequiredArgsConstructor
@Slf4j
@DS(BilliardsConstants.DS_BILLIARDS_PLATFORM)
public class ScheduleTaskLogger {

    private final BlsScheduleTaskLogMapper taskLogMapper;

    public void record(String taskCode, String taskName, Runnable taskLogic) {
        LocalDateTime start = LocalDateTime.now();
        BlsScheduleTaskLog logEntity = new BlsScheduleTaskLog();
        logEntity.setTaskCode(taskCode);
        logEntity.setTaskName(taskName);
        logEntity.setStartTime(start);

        try {
            taskLogic.run();
            logEntity.setStatus("SUCCESS");
        } catch (Exception e) {
            logEntity.setStatus("FAIL");
            logEntity.setErrorMsg(e.getMessage());
            log.error("【{}】任务执行异常: {}", taskName, e.getMessage(), e);
        } finally {
            LocalDateTime end = LocalDateTime.now();
            logEntity.setEndTime(end);
            logEntity.setDurationMs(Duration.between(start, end).toMillis());
            logEntity.setCreateTime(LocalDateTime.now());
            taskLogMapper.insert(logEntity);
            log.info("【{}】任务执行结束，状态={}, 耗时={}ms", taskName, logEntity.getStatus(), logEntity.getDurationMs());
        }
    }

}
