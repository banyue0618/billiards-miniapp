package org.dromara.billiards.notify.service;

import org.dromara.billiards.notify.model.RefundFailureNotice;

public interface RefundFailureNotifier {
    void notifyBySse(RefundFailureNotice notice);
    void notifyByEmail(RefundFailureNotice notice);
    // WebSocket 通道
    void notifyByWebSocket(RefundFailureNotice notice);
    // SMS 通道（骨架）
    void notifyBySms(RefundFailureNotice notice);
}


