package org.dromara.billiards.notify.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 退款失败通知载荷
 *
 * 用于事件监听器在各通知通道（SSE/Email/WebSocket/SMS）之间传递模板变量。
 * 下游通道实现可据此渲染标题/正文/消息体。
 */
@Data
@Builder
public class RefundFailureNotice {
    /** 退款记录ID（bls_refund_record.id） */
    private String refundRecordId;
    /** 订单ID（bls_order.id） */
    private String orderId;
    /** 订单编号（便于管理员定位） */
    private String orderNo;
    /** 退款金额 */
    private BigDecimal amount;
    /** 退款状态（如 ABNORMAL/CLOSED/FAIL 等人类可读状态） */
    private String refundStatus;
    /** 退款失败原因描述（来自记录 remark 或支付侧返回） */
    private String reason;
    /** 用户ID（便于关联用户画像/通知定向） */
    private Long userId;
    /** 微信支付交易号（可辅助排查） */
    private String transactionId;
    /** 后台控制台详情页地址（供邮件/IM 链接跳转） */
    private String dashboardUrl;
}


