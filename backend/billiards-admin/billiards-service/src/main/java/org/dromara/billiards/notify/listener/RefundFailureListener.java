package org.dromara.billiards.notify.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.billiards.notify.config.RefundNotifyProperties;
import org.dromara.billiards.notify.event.RefundFailureEvent;
import org.dromara.billiards.notify.model.RefundFailureNotice;
import org.dromara.billiards.notify.service.RefundFailureNotifier;
import org.dromara.common.redis.utils.RedisUtils;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class RefundFailureListener {

    private final RefundNotifyProperties properties;
    private final RefundFailureNotifier notifier;

    // 异步消费退款失败事件，避免阻塞业务主流程
    // 标注为 @Async 以并发发送不同通道的通知
    @Async
    @EventListener
    public void onRefundFailure(RefundFailureEvent event) {
        if (!properties.isEnabled()) {
            return;
        }

        // 利用 Redis 做幂等去重，避免同一退款单重复通知
        String dedupKey = "refund_notify:" + event.getRefundRecord().getId();
        if (Boolean.FALSE.equals(RedisUtils.setObjectIfAbsent(dedupKey, 1, Duration.ofMinutes(properties.getDedupTtlMinutes())))) {
            // 已通知过
            return;
        }

        RefundFailureNotice notice = RefundFailureNotice.builder()
            .refundRecordId(event.getRefundRecord().getId())
            .orderId(event.getOrder().getId())
            .orderNo(event.getOrder().getOrderNo())
            .amount(event.getRefundRecord().getAmount())
            .refundStatus(event.getRefundStatus())
            .reason(event.getRefundRecord().getRemark())
            .userId(event.getRefundRecord().getUserId())
            .transactionId(event.getRefundRecord().getTransactionId())
            .dashboardUrl("/admin/refund/" + event.getRefundRecord().getId())
            .build();

        // 优先级：默认 SSE -> Email -> WebSocket -> SMS（可通过配置覆盖顺序）
        if (properties.getChannels() == null || properties.getChannels().isEmpty()) {
            notifier.notifyBySse(notice);
            notifier.notifyByEmail(notice);
            notifier.notifyByWebSocket(notice);
            notifier.notifyBySms(notice);
            return;
        }
        properties.getChannels().forEach(ch -> {
            switch (ch.toUpperCase()) {
                case "SSE" -> notifier.notifyBySse(notice);
                case "EMAIL" -> notifier.notifyByEmail(notice);
                case "WEBSOCKET" -> notifier.notifyByWebSocket(notice);
                case "SMS" -> notifier.notifyBySms(notice);
                default -> log.warn("未知通知通道: {}", ch);
            }
        });
    }
}


