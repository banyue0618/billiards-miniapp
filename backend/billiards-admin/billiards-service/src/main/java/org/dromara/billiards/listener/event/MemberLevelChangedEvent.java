package org.dromara.billiards.listener.event;

import org.springframework.context.ApplicationEvent;

public class MemberLevelChangedEvent extends ApplicationEvent {

    private final Long userId;
    private final String merchantId;
    private final String beforeLevel;
    private final String afterLevel;
    private final String orderId;

    public MemberLevelChangedEvent(Object source, Long userId, String merchantId, String beforeLevel, String afterLevel, String orderId) {
        super(source);
        this.userId = userId;
        this.merchantId = merchantId;
        this.beforeLevel = beforeLevel;
        this.afterLevel = afterLevel;
        this.orderId = orderId;
    }

    public Long getUserId() { return userId; }
    public String getMerchantId() { return merchantId; }
    public String getBeforeLevel() { return beforeLevel; }
    public String getAfterLevel() { return afterLevel; }
    public String getOrderId() { return orderId; }
}


