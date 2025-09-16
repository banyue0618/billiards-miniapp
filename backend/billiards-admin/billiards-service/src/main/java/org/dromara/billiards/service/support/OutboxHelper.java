package org.dromara.billiards.service.support;

import lombok.RequiredArgsConstructor;
import org.dromara.billiards.domain.bo.BlsEventOutboxBo;
import org.dromara.billiards.domain.vo.BlsEventOutboxVo;
import org.dromara.billiards.service.IBlsEventOutboxService;
import org.springframework.stereotype.Component;
import org.dromara.common.core.utils.StringUtils;

import java.time.LocalDateTime;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class OutboxHelper {

    private final IBlsEventOutboxService eventOutboxService;

    public void markOutbox(String aggregateType, String aggregateId, String eventType, boolean success, String error){
        BlsEventOutboxBo queryBo = new BlsEventOutboxBo();
        queryBo.setAggregateType(aggregateType);
        queryBo.setAggregateId(aggregateId);
        queryBo.setEventType(eventType);
        var list = eventOutboxService.queryList(queryBo);
        if (list != null && !list.isEmpty()) {
            BlsEventOutboxVo vo = list.get(0);
            BlsEventOutboxBo update = new BlsEventOutboxBo();
            update.setId(vo.getId());
            if (success) {
                update.setStatus(1L);
                update.setLastError(null);
                update.setNextRetryTime(null);
            } else {
                long retry = vo.getRetryCount() == null ? 0L : vo.getRetryCount();
                update.setStatus(2L);
                update.setRetryCount(retry + 1);
                String truncated = StringUtils.substring(error, 0, 255);
                update.setLastError(truncated);
                long backoffSec = Math.min(300L, (retry + 1) * 10L);
                update.setNextRetryTime(LocalDateTime.now().plusSeconds(backoffSec));
            }
            eventOutboxService.updateByBo(update);
        }
    }
}


