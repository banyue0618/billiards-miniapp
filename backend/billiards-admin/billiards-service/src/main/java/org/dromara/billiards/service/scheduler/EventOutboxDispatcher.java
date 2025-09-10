package org.dromara.billiards.service.scheduler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.billiards.domain.entity.BlsEventOutbox;
import org.dromara.billiards.domain.entity.BlsOrder;
import org.dromara.billiards.domain.event.OrderCompletedEvent;
import org.dromara.billiards.domain.event.RefundRequestedEvent;
import org.dromara.billiards.common.constant.OutboxEventTypeEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dromara.billiards.mapper.BlsEventOutboxMapper;
import org.dromara.billiards.service.IBlsEventOutboxService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventOutboxDispatcher {

    private final IBlsEventOutboxService outboxService;
    private final ApplicationEventPublisher publisher;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 每30秒扫描一次待发送/失败且到达重试时间的消息
    @Scheduled(fixedDelay = 30000L, initialDelay = 15000L)
    public void dispatch() {
        List<BlsEventOutbox> list = outboxService.queryPendingMessages();

        for (BlsEventOutbox msg : list) {
            try {
                String type = msg.getEventType();
                if (OutboxEventTypeEnum.ORDER_COMPLETED.name().equals(type)) {
                    OrderCompletedPayload payload = objectMapper.readValue(msg.getPayload(), OrderCompletedPayload.class);
                    BlsOrder order = new BlsOrder();
                    order.setId(payload.getOrderId());
                    order.setUserId(payload.getUserId());
                    order.setMerchantId(payload.getMerchantId());
                    order.setActualAmount(payload.getActualAmount());
                    publisher.publishEvent(new OrderCompletedEvent(this, order));
                } else if (OutboxEventTypeEnum.REFUND_REQUESTED.name().equals(type)) {
                    RefundRequestedPayload payload = objectMapper.readValue(msg.getPayload(), RefundRequestedPayload.class);
                    BlsOrder order = new BlsOrder();
                    order.setId(payload.getOrderId());
                    publisher.publishEvent(new RefundRequestedEvent(this, order, payload.getRefundAmount(), payload.getLastPayRecordId()));
                }
                msg.setStatus(1L);
                msg.setLastError(null);
                outboxService.updateById(msg);
            } catch (Exception e) {
                log.error("Dispatch outbox failed id={}, err=", msg.getId(), e);
                long retry = msg.getRetryCount() == null ? 0L : msg.getRetryCount();
                msg.setRetryCount(retry + 1);
                msg.setStatus(2L);
                // 简化：指数回退，也可用更精细的策略
                msg.setNextRetryTime(LocalDateTime.now().plusSeconds(Math.min(300, (retry + 1) * 10)));
                msg.setLastError(e.getMessage());
                outboxService.updateById(msg);
            }
        }
    }

    // Payload DTOs
    public static class OrderCompletedPayload {
        private String orderId;
        private Long userId;
        private String merchantId;
        private java.math.BigDecimal actualAmount;

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getMerchantId() {
            return merchantId;
        }

        public void setMerchantId(String merchantId) {
            this.merchantId = merchantId;
        }

        public java.math.BigDecimal getActualAmount() {
            return actualAmount;
        }

        public void setActualAmount(java.math.BigDecimal actualAmount) {
            this.actualAmount = actualAmount;
        }
    }

    public static class RefundRequestedPayload {
        private String orderId;
        private String lastPayRecordId;
        private java.math.BigDecimal refundAmount;

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public java.math.BigDecimal getRefundAmount() {
            return refundAmount;
        }

        public void setRefundAmount(java.math.BigDecimal refundAmount) {
            this.refundAmount = refundAmount;
        }

        public String getLastPayRecordId() {
            return lastPayRecordId;
        }

        public void setLastPayRecordId(String lastPayRecordId) {
            this.lastPayRecordId = lastPayRecordId;
        }
    }
}


