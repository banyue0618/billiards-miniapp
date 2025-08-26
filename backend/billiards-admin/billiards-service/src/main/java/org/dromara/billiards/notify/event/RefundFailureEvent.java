package org.dromara.billiards.notify.event;

import lombok.Getter;
import org.dromara.billiards.domain.entity.BlsRefundRecord;
import org.dromara.billiards.domain.entity.Order;
import org.springframework.context.ApplicationEvent;

@Getter
public class RefundFailureEvent extends ApplicationEvent {

    private final BlsRefundRecord refundRecord;
    private final Order order;
    private final String refundStatus;

    public RefundFailureEvent(Object source, BlsRefundRecord refundRecord, Order order, String refundStatus) {
        super(source);
        this.refundRecord = refundRecord;
        this.order = order;
        this.refundStatus = refundStatus;
    }
}


