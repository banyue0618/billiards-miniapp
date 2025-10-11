package org.dromara.billiards.service.support;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.billiards.domain.bo.BlsEventOutboxBo;
import org.dromara.billiards.domain.entity.BlsEventOutbox;
import org.dromara.billiards.domain.vo.BlsEventOutboxVo;
import org.dromara.billiards.service.IBlsEventOutboxService;
import org.springframework.stereotype.Component;
import org.dromara.common.core.utils.StringUtils;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxHelper {

    private final IBlsEventOutboxService eventOutboxService;

    public void markOutbox(String aggregateType, String aggregateId, String eventType, boolean success, String error){
        BlsEventOutboxBo queryBo = new BlsEventOutboxBo();
        // 考虑下面两个字段增加唯一索引
        queryBo.setAggregateType(aggregateType);
        queryBo.setAggregateId(aggregateId);
        queryBo.setEventType(eventType);
        var list = eventOutboxService.queryList(queryBo);
        if (list != null && !list.isEmpty()) {
            BlsEventOutboxVo vo = list.get(0);

            // 使用 CAS 更新，防止并发问题
            // 支持两种场景：
            // 1. 直接发布的事件，状态为 0（NEW）
            // 2. 定时任务重试的事件，状态为 3（PROCESSING）

            if (success) {
                // 更新为成功状态
                boolean affected = eventOutboxService.update(
                    Wrappers.<BlsEventOutbox>lambdaUpdate()
                        .set(BlsEventOutbox::getStatus, 1L)
                        .set(BlsEventOutbox::getLastError, null)
                        .set(BlsEventOutbox::getNextRetryTime, null)
                        .eq(BlsEventOutbox::getId, vo.getId())
                        .in(BlsEventOutbox::getStatus, 0L, 3L) // 允许从 NEW 或 PROCESSING 状态更新
                );
                if (!affected) {
                    log.warn("Failed to mark outbox success, id={}, current status={}, may already processed",
                        vo.getId(), vo.getStatus());
                }
            } else {
                // 更新为失败状态
                long retry = vo.getRetryCount() == null ? 0L : vo.getRetryCount();
                String truncated = StringUtils.substring(error, 0, 255);
                long backoffSec = Math.min(300L, (retry + 1) * 10L);

                boolean affected = eventOutboxService.update(
                    Wrappers.<BlsEventOutbox>lambdaUpdate()
                        .set(BlsEventOutbox::getStatus, 2L)
                        .set(BlsEventOutbox::getRetryCount, retry + 1)
                        .set(BlsEventOutbox::getLastError, truncated)
                        .set(BlsEventOutbox::getNextRetryTime, LocalDateTime.now().plusSeconds(backoffSec))
                        .eq(BlsEventOutbox::getId, vo.getId())
                        .in(BlsEventOutbox::getStatus, 0L, 3L) // 允许从 NEW 或 PROCESSING 状态更新
                );
                if (!affected) {
                    log.warn("Failed to mark outbox failure, id={}, current status={}, may already processed",
                        vo.getId(), vo.getStatus());
                }
            }
        }
    }
}


