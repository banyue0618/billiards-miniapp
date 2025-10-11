package org.dromara.billiards.service.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.billiards.common.constant.AggregateTypeEnum;
import org.dromara.billiards.domain.entity.BlsOrder;
import org.dromara.billiards.domain.entity.BlsPayRecord;
import org.dromara.billiards.domain.event.RefundRequestedEvent;
import org.dromara.billiards.service.IBlsRefundRecordService;
import org.dromara.billiards.service.IBlsPayRecordService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.dromara.billiards.common.constant.OutboxEventTypeEnum;
import org.dromara.billiards.service.support.OutboxHelper;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class RefundRequestedListener {

    private final IBlsRefundRecordService refundRecordService;
    private final IBlsPayRecordService payRecordService;
    private final OutboxHelper outboxHelper;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRefundRequested(RefundRequestedEvent event) {
        BlsOrder order = event.getOrder();
        try {
            // 幂等由 refundRecordService 内部保障（按订单ID查询未完成的退款记录/或唯一索引）
            BlsPayRecord lastPay = payRecordService.getById(event.getLastPayRecordId());
            refundRecordService.refund(order.getId(), lastPay, event.getRefundAmount());
            outboxHelper.markOutbox(AggregateTypeEnum.ORDER.name(), order.getId(), OutboxEventTypeEnum.REFUND_REQUESTED.name(), true, null);
        } catch (Exception e) {
            log.error("RefundRequested async failed orderId={}, err=", order.getId(), e);
            outboxHelper.markOutbox(AggregateTypeEnum.ORDER.name(), order.getId(), OutboxEventTypeEnum.REFUND_REQUESTED.name(), false, e.getMessage());
        }
    }
}


