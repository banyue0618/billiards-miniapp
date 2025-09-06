package org.dromara.billiards.notify.service.impl;

import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.billiards.notify.config.RefundNotifyProperties;
import org.dromara.billiards.notify.model.RefundFailureNotice;
import org.dromara.billiards.notify.service.RefundFailureNotifier;
import org.dromara.common.mail.utils.MailUtils;
import org.dromara.common.websocket.utils.WebSocketUtils;
import org.dromara.common.sse.utils.SseMessageUtils;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefundFailureNotifierImpl implements RefundFailureNotifier {

    private final RefundNotifyProperties properties;

    /**
     * 通过 SSE 群发退款失败消息
     */
    @Override
    public void notifyBySse(RefundFailureNotice notice) {
        if (!properties.getSse().isEnabled()) {
            return;
        }
        String message = properties.getSse().getMessageTemplate()
            .replace("${orderNo}", StrUtil.nullToDefault(notice.getOrderNo(), "-"))
            .replace("${amount}", notice.getAmount() == null ? "0" : notice.getAmount().setScale(2, RoundingMode.DOWN).toPlainString())
            .replace("${refundId}", StrUtil.nullToDefault(notice.getRefundRecordId(), "-"))
            .replace("${refundStatus}", StrUtil.nullToDefault(notice.getRefundStatus(), "-"));
        SseMessageUtils.publishAll(message);
    }

    /**
     * 通过 Email 发送退款失败通知
     */
    @Override
    public void notifyByEmail(RefundFailureNotice notice) {
        if (!properties.getEmail().isEnabled()) {
            return;
        }
        if (properties.getEmail().getToEmails() == null || properties.getEmail().getToEmails().isEmpty()) {
            log.warn("退款失败邮件通知未配置收件人");
            return;
        }
        String subject = properties.getEmail().getSubjectTemplate()
            .replace("${orderNo}", StrUtil.nullToDefault(notice.getOrderNo(), "-"));
        String content = properties.getEmail().getContentTemplate()
            .replace("${orderNo}", StrUtil.nullToDefault(notice.getOrderNo(), "-"))
            .replace("${refundId}", StrUtil.nullToDefault(notice.getRefundRecordId(), "-"))
            .replace("${amount}", notice.getAmount() == null ? "0" : notice.getAmount().setScale(2, RoundingMode.DOWN).toPlainString())
            .replace("${refundStatus}", StrUtil.nullToDefault(notice.getRefundStatus(), "-"))
            .replace("${reason}", StrUtil.nullToDefault(notice.getReason(), "-"))
            .replace("${dashboardUrl}", StrUtil.nullToDefault(notice.getDashboardUrl(), "-"));
        try {
            MailUtils.sendHtml(properties.getEmail().getToEmails(), subject, content);
        } catch (Exception e) {
            log.error("发送退款失败邮件异常", e);
        }
    }

    /**
     * 通过 WebSocket 群发退款失败消息
     */
    @Override
    public void notifyByWebSocket(RefundFailureNotice notice) {
        if (!properties.getWebsocket().isEnabled()) {
            return;
        }
        String message = properties.getWebsocket().getMessageTemplate()
            .replace("${orderNo}", StrUtil.nullToDefault(notice.getOrderNo(), "-"))
            .replace("${amount}", notice.getAmount() == null ? "0" : notice.getAmount().setScale(2, RoundingMode.DOWN).toPlainString());
        // 群发给在线管理员（可后续按角色筛选会话）
        WebSocketUtils.publishAll(message);
    }

    /**
     * 通过 SMS 发送退款失败通知（骨架），后续可接入具体网关
     */
    @Override
    public void notifyBySms(RefundFailureNotice notice) {
        if (!properties.getSms().isEnabled()) {
            return;
        }
        // 目前为骨架：根据配置打印日志，后续可接入 ruoyi-common-sms 具体发送实现
        String content = properties.getSms().getContentTemplate()
            .replace("${orderNo}", StrUtil.nullToDefault(notice.getOrderNo(), "-"))
            .replace("${amount}", notice.getAmount() == null ? "0" : notice.getAmount().setScale(2, RoundingMode.DOWN).toPlainString())
            .replace("${refundStatus}", StrUtil.nullToDefault(notice.getRefundStatus(), "-"));
        if (properties.getSms().getToPhones() == null || properties.getSms().getToPhones().isEmpty()) {
            log.warn("退款失败短信通知未配置收件手机号, content={}", content);
            return;
        }
        log.info("[SMS骨架] 向 {} 发送内容: {}", properties.getSms().getToPhones(), content);
    }
}


