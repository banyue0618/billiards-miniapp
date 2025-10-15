package org.dromara.billiards.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.billiards.listener.event.MemberLevelChangedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MemberLevelChangedListener {

    @Async
    @EventListener
    public void onMemberLevelChanged(MemberLevelChangedEvent event) {
        // TODO: 调用推送服务（小程序订阅消息/短信/站内通知）
        log.info("Member level changed, userId={}, merchantId={}, {} -> {}, orderId={}",
            event.getUserId(), event.getMerchantId(), event.getBeforeLevel(), event.getAfterLevel(), event.getOrderId());
    }
}


