package org.dromara.billiards.notify.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 退款失败通知配置
 *
 * prefix: billiards.notify.refund-fail
 * - enabled: 是否开启
 * - channels: 通道优先级顺序（SSE/EMAIL/WEBSOCKET/SMS）
 * - dedupTtlMinutes: 同一退款单的通知去重 TTL（分钟）
 * - retry*: 重试相关参数（留作扩展）
 * - email/sse/sms/websocket: 各通道的子配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "billiards.notify.refund-fail")
public class RefundNotifyProperties {
    /** 是否开启退款失败通知 */
    private boolean enabled = true;
    // 通道顺序即优先级，支持: SSE, EMAIL, WEBSOCKET, SMS
    private List<String> channels; // e.g. [SSE, EMAIL]
    /** 同一退款单去重 TTL（分钟） */
    private int dedupTtlMinutes = 60 * 24;
    /** 最大重试次数（预留，当前监听器未启用主动重试） */
    private int retryMaxTimes = 3;
    /** 重试退避秒数（预留） */
    private int retryBackoffSeconds = 30;

    private Email email = new Email();
    private Sse sse = new Sse();
    private Sms sms = new Sms();
    private WebSocket websocket = new WebSocket();

    @Data
    public static class Email {
        /** 是否启用邮件通道 */
        private boolean enabled = true;
        /** 收件人邮箱列表 */
        private List<String> toEmails;
        /** 邮件标题模板，变量如 ${orderNo} */
        private String subjectTemplate = "[退款失败告警] 订单:${orderNo}";
        /** 邮件正文模板，支持占位符变量 */
        private String contentTemplate = "退款单:${refundId}, 金额:${amount}, 状态:${refundStatus}, 原因:${reason}";
    }

    @Data
    public static class Sse {
        /** 是否启用 SSE 通道（站内实时提醒） */
        private boolean enabled = true;
        /** SSE 消息模板 */
        private String messageTemplate = "退款失败: 订单:${orderNo}, 金额:${amount}";
    }

    @Data
    public static class Sms {
        /** 是否启用短信通道（骨架） */
        private boolean enabled = false;
        /** 收件手机号列表 */
        private List<String> toPhones;
        /** 短信内容模板（长度需受限，尽量精简） */
        private String contentTemplate = "退款失败: 订单:${orderNo}, 金额:${amount}, 状态:${refundStatus}";
        /** 每小时频控阈值（后续接入网关时使用） */
        private int rateLimitPerHour = 5;
    }

    @Data
    public static class WebSocket {
        /** 是否启用 WebSocket 通道 */
        private boolean enabled = true;
        /** WebSocket 消息模板 */
        private String messageTemplate = "退款失败(WS): 订单:${orderNo}, 金额:${amount}";
    }
}


