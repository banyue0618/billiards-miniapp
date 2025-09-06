package org.dromara.billiards.notify.event;

import lombok.Getter;
import org.dromara.billiards.domain.entity.BlsOrder;
import org.dromara.billiards.domain.entity.BlsRefundRecord;
import org.springframework.context.ApplicationEvent;

@Getter
public class RefundFailureEvent extends ApplicationEvent {

    private final BlsRefundRecord refundRecord;
    private final BlsOrder blsOrder;
    private final String refundStatus;

    public RefundFailureEvent(Object source, BlsRefundRecord refundRecord, BlsOrder blsOrder, String refundStatus) {
        super(source);
        this.refundRecord = refundRecord;
        this.blsOrder = blsOrder;
        this.refundStatus = refundStatus;
    }
}


