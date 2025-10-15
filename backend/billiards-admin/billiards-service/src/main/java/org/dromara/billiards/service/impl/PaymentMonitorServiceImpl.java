package org.dromara.billiards.service.impl;

import com.github.binarywang.wxpay.bean.result.WxPayOrderQueryV3Result;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.dromara.billiards.common.constant.PaymentStatus;
import org.dromara.billiards.domain.bo.BlsRefundRecordBo;
import org.dromara.billiards.domain.entity.BlsPayRecord;
import org.dromara.billiards.domain.vo.BlsRefundRecordVo;
import org.dromara.billiards.service.IBlsPayRecordService;
import org.dromara.billiards.service.IBlsRefundRecordService;
import org.dromara.billiards.service.IBlsWalletAccountService;
import org.dromara.billiards.service.PaymentMonitorService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class PaymentMonitorServiceImpl implements PaymentMonitorService {
    @Resource
    private IBlsRefundRecordService refundRecordService;

    @Resource
    private IBlsPayRecordService payRecordService;

    @Resource
    private IBlsWalletAccountService walletAccountService;

    @Value("${billiards.payment.timeout-minutes:30}")
    private long payTimeoutMinutes;

    @Override
    public void detectPaymentTimeouts() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(payTimeoutMinutes);
        List<BlsPayRecord> timeoutList = payRecordService.queryPayingTimeoutList(threshold);
        if (timeoutList == null || timeoutList.isEmpty()) {
            return;
        }
        for (BlsPayRecord record : timeoutList) {
            record.setPaymentStatus(PaymentStatus.PAY_TIME.getCode());
            record.setRemark("支付超时(>" + payTimeoutMinutes + "min)");
            record.setUpdateTime(LocalDateTime.now());
            payRecordService.updateById(record);
        }
        log.info("检测支付超时完成，处理条数: {}", timeoutList.size());
    }

    @Override
    public void reconcilePayingRecords() {
        List<BlsPayRecord> payingList = payRecordService.queryListWithStatus(PaymentStatus.PAYING);
        if (payingList == null || payingList.isEmpty()) {
            return;
        }
        for (BlsPayRecord record : payingList) {
            try {
                WxPayOrderQueryV3Result payStatus = payRecordService.queryPayStatus(record.getTransactionId(), record.getPayNo());
                String tradeState = payStatus.getTradeState();
                if (tradeState == null) {
                    continue;
                }
                switch (tradeState) {
                    case "SUCCESS" -> {
                        record.setPaymentStatus(PaymentStatus.PAID.getCode());
                        record.setRemark("轮询确认支付成功");
                        record.setNotifyTime(LocalDateTime.now());
                        record.setTransactionId(payStatus.getTransactionId());
                        payRecordService.updateById(record);
                        walletAccountService.updateWalletBalanceAndWalletTransaction(record);
                    }
                    case "CLOSED", "PAYERROR" -> {
                        record.setPaymentStatus(PaymentStatus.PAY_FAIL.getCode());
                        record.setRemark("轮询确认失败:" + tradeState);
                        record.setUpdateTime(LocalDateTime.now());
                        payRecordService.updateById(record);
                    }
                    default -> {
                        // 仍为支付中，按需更新最后查询时间
                        record.setLastQueryTime(LocalDateTime.now());
                        payRecordService.updateById(record);
                    }
                }
            } catch (Exception e) {
                log.error("查询支付状态异常, payNo={}", record.getPayNo(), e);
            }
        }
        log.info("对支付中记录轮询完成，处理条数: {}", payingList.size());
    }

    @Override
    public void scanRefundFailures() {
        // 退款失败数据来源都是微信回调，返回失败。
        List<BlsRefundRecordVo> failed = refundRecordService.queryRefundFailiureList();
        if (failed == null || failed.isEmpty()) {
            return;
        }
        for (BlsRefundRecordVo record : failed) {
            // 目前仅记录日志，后续对接消息通知模块
            log.warn("检测到退款失败记录需要通知：admin_notify_needed, id={}, orderId={}, amount={}", record.getId(), record.getOrderId(), record.getAmount());
        }
    }
}


