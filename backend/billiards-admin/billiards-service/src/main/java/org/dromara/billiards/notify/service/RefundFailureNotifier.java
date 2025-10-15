package org.dromara.billiards.notify.service;

import org.dromara.billiards.notify.model.RefundFailureNotice;

public interface RefundFailureNotifier {

    // Server-Sent Events (SSE) 通道
    void notifyBySse(RefundFailureNotice notice);

    // Email 通道
    void notifyByEmail(RefundFailureNotice notice);

    // WebSocket 通道
    void notifyByWebSocket(RefundFailureNotice notice);

    // SMS 通道（骨架）
    void notifyBySms(RefundFailureNotice notice);
}


