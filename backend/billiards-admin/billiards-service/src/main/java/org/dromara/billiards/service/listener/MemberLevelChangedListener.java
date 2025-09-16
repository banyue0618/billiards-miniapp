package org.dromara.billiards.service.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.billiards.notify.event.MemberLevelChangedEvent;
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
        log.info("Member level changed, userId={}, {} -> {}, orderId={}",
            event.getUserId(), event.getBeforeLevel(), event.getAfterLevel(), event.getOrderId());
    }
}


