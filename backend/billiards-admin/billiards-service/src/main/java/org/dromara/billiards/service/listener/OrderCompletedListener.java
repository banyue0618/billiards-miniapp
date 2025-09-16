package org.dromara.billiards.service.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.billiards.common.constant.AggregateTypeEnum;
import org.dromara.billiards.domain.entity.BlsOrder;
import org.dromara.billiards.domain.event.OrderCompletedEvent;
import org.dromara.billiards.service.IBlsMemberUserService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.dromara.billiards.common.constant.OutboxEventTypeEnum;
import org.dromara.billiards.service.IBlsEventOutboxService;
import org.dromara.billiards.service.support.OutboxHelper;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCompletedListener {

    private final IBlsMemberUserService memberUserService;
    private final IBlsEventOutboxService eventOutboxService;
    private final OutboxHelper outboxHelper;

    @Async
    @EventListener
    public void onOrderCompleted(OrderCompletedEvent event) {
        BlsOrder order = event.getOrder();
        try {
            memberUserService.accrueOnPaidOrder(order);
            outboxHelper.markOutbox(AggregateTypeEnum.ORDER.name(), order.getId(), OutboxEventTypeEnum.ORDER_COMPLETED.name(), true, null);
        } catch (Exception e) {
            log.error("accrueOnPaidOrder async failed orderId={}, err=", order.getId(), e);
            outboxHelper.markOutbox(AggregateTypeEnum.ORDER.name(), order.getId(), OutboxEventTypeEnum.ORDER_COMPLETED.name(), false, e.getMessage());
        }
    }
}


