package org.dromara.billiards.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.billiards.common.constant.AggregateTypeEnum;
import org.dromara.billiards.domain.entity.BlsOrder;
import org.dromara.billiards.listener.event.OrderCompletedEvent;
import org.dromara.billiards.service.IBlsMemberUserService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.dromara.billiards.common.constant.OutboxEventTypeEnum;
import org.dromara.billiards.support.OutboxHelper;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCompletedListener {

    private final IBlsMemberUserService memberUserService;
    private final OutboxHelper outboxHelper;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
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


