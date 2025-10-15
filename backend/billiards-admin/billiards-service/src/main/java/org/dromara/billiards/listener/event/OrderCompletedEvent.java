package org.dromara.billiards.listener.event;

import org.dromara.billiards.domain.entity.BlsOrder;
import org.springframework.context.ApplicationEvent;
import java.math.BigDecimal;

public class OrderCompletedEvent extends ApplicationEvent {
    private final BlsOrder order;

    public OrderCompletedEvent(Object source, BlsOrder order) {
        super(source);
        this.order = order;
    }

    public BlsOrder getOrder() {
        return order;
    }
}


