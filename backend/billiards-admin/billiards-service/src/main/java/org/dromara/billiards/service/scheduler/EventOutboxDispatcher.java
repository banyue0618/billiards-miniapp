package org.dromara.billiards.service.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.billiards.domain.entity.BlsEventOutbox;
import org.dromara.billiards.domain.entity.BlsOrder;
import org.dromara.billiards.domain.event.OrderCompletedEvent;
import org.dromara.billiards.domain.event.RefundRequestedEvent;
import org.dromara.billiards.common.constant.OutboxEventTypeEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dromara.billiards.service.IBlsEventOutboxService;
import org.dromara.billiards.service.OrderService;
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
    private final OrderService orderService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // 每30秒扫描一次待发送/失败且到达重试时间的消息
    @Scheduled(fixedDelay = 30000L, initialDelay = 15000L)
    public void dispatch() {
        List<BlsEventOutbox> list = outboxService.queryPendingMessages();

        for (BlsEventOutbox msg : list) {
            try {
                // 使用乐观锁 CAS 更新状态，防止多实例重复处理
                // 更新条件：id 匹配且 status 仍为待处理(0)或失败(2)
                boolean locked = outboxService.tryLockMessage(msg.getId(), msg.getStatus());
                if (!locked) {
                    // 已被其他实例处理，跳过
                    log.debug("Message {} already locked by another instance", msg.getId());
                    continue;
                }

                // 重新发布事件
                String type = msg.getEventType();
                if (OutboxEventTypeEnum.ORDER_COMPLETED.name().equals(type)) {
                    OrderCompletedPayload payload = objectMapper.readValue(msg.getPayload(), OrderCompletedPayload.class);
                    BlsOrder order = orderService.getById(payload.getOrderId()); // 确保订单存在
                    if(order == null) {
                        log.error("Order not found for outbox event, orderId={}", payload.getOrderId());
                        // 标记为失败，避免重复调度
                        msg.setStatus(2L);
                        msg.setLastError("Order not found");
                        outboxService.updateById(msg);
                        continue;
                    }
                    order.setId(payload.getOrderId());
                    order.setUserId(payload.getUserId());
                    order.setMerchantId(payload.getMerchantId());
                    order.setActualAmount(payload.getActualAmount());
                    publisher.publishEvent(new OrderCompletedEvent(this, order));
                } else if (OutboxEventTypeEnum.REFUND_REQUESTED.name().equals(type)) {
                    RefundRequestedPayload payload = objectMapper.readValue(msg.getPayload(), RefundRequestedPayload.class);
                    BlsOrder order = orderService.getById(payload.getOrderId()); // 确保订单存在
                    if(order == null) {
                        log.error("Order not found for outbox event, orderId={}, type={}", payload.getOrderId(), OutboxEventTypeEnum.REFUND_REQUESTED.name());
                        // 标记为失败，避免重复调度
                        msg.setStatus(2L);
                        msg.setLastError("Order not found");
                        outboxService.updateById(msg);
                        continue;
                    }
                    publisher.publishEvent(new RefundRequestedEvent(this, order, payload.getRefundAmount(), payload.getLastPayRecordId()));
                }

                // 标记为处理中，防止监听器执行时间过长导致重复调度
                // 实际的成功/失败状态由 OutboxHelper 在监听器中更新
            } catch (Exception e) {
                log.error("Dispatch outbox failed id={}, err=", msg.getId(), e);
                // 如果发布事件本身失败（如 JSON 解析错误），直接标记失败
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


