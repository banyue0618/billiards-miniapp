package org.dromara.billiards.listener.event;

import org.dromara.billiards.domain.entity.BlsOrder;
import org.springframework.context.ApplicationEvent;
import java.math.BigDecimal;

public class RefundRequestedEvent extends ApplicationEvent {
    private final BlsOrder order;
    private final BigDecimal refundAmount;
    private final String lastPayRecordId;

    public RefundRequestedEvent(Object source, BlsOrder order, BigDecimal refundAmount, String lastPayRecordId) {
        super(source);
        this.order = order;
        this.refundAmount = refundAmount;
        this.lastPayRecordId = lastPayRecordId;
    }

    public BlsOrder getOrder() { return order; }
    public BigDecimal getRefundAmount() { return refundAmount; }
    public String getLastPayRecordId() { return lastPayRecordId; }
}


