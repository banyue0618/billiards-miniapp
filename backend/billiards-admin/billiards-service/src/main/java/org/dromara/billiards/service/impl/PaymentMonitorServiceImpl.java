package org.dromara.billiards.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.binarywang.wxpay.bean.result.WxPayOrderQueryV3Result;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.dromara.billiards.common.constant.PaymentStatus;
import org.dromara.billiards.domain.entity.BlsRefundRecord;
import org.dromara.billiards.domain.entity.PayRecord;
import org.dromara.billiards.mapper.BlsRefundRecordMapper;
import org.dromara.billiards.mapper.PayRecordMapper;
import org.dromara.billiards.service.IBlsPayRecordService;
import org.dromara.billiards.service.IBlsWalletAccountService;
import org.dromara.billiards.service.PaymentMonitorService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class PaymentMonitorServiceImpl implements PaymentMonitorService {

    @Resource
    private PayRecordMapper payRecordMapper;

    @Resource
    private BlsRefundRecordMapper refundRecordMapper;

    @Resource
    private IBlsPayRecordService payRecordService;

    @Resource
    private IBlsWalletAccountService walletAccountService;

    @Value("${billiards.payment.timeout-minutes:30}")
    private long payTimeoutMinutes;

    @Override
    public void detectPaymentTimeouts() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(payTimeoutMinutes);
        LambdaQueryWrapper<PayRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(PayRecord::getPaymentStatus, PaymentStatus.UNPAID.getCode())
            .lt(PayRecord::getCreateTime, threshold);
        List<PayRecord> timeoutList = payRecordMapper.selectList(lqw);
        if (timeoutList == null || timeoutList.isEmpty()) {
            return;
        }
        for (PayRecord record : timeoutList) {
            record.setPaymentStatus(PaymentStatus.PAY_FAIL.getCode());
            record.setRemark("支付超时(>" + payTimeoutMinutes + "min)");
            record.setUpdateTime(LocalDateTime.now());
            payRecordMapper.updateById(record);
        }
        log.info("检测支付超时完成，处理条数: {}", timeoutList.size());
    }

    @Override
    public void reconcilePayingRecords() {
        LambdaQueryWrapper<PayRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(PayRecord::getPaymentStatus, PaymentStatus.PAYING.getCode());
        List<PayRecord> payingList = payRecordMapper.selectList(lqw);
        if (payingList == null || payingList.isEmpty()) {
            return;
        }
        for (PayRecord record : payingList) {
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
                        payRecordMapper.updateById(record);
                        walletAccountService.updateWalletBalanceAndWalletTransaction(record);
                    }
                    case "CLOSED", "PAYERROR" -> {
                        record.setPaymentStatus(PaymentStatus.PAY_FAIL.getCode());
                        record.setRemark("轮询确认失败:" + tradeState);
                        record.setUpdateTime(LocalDateTime.now());
                        payRecordMapper.updateById(record);
                    }
                    default -> {
                        // 仍为支付中，按需更新最后查询时间
                        record.setLastQueryTime(LocalDateTime.now());
                        payRecordMapper.updateById(record);
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
        LambdaQueryWrapper<BlsRefundRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(BlsRefundRecord::getRefundStatus, 2); // 2=退款失败
        List<BlsRefundRecord> failed = refundRecordMapper.selectList(lqw);
        if (failed == null || failed.isEmpty()) {
            return;
        }
        for (BlsRefundRecord record : failed) {
            // 目前仅记录日志，后续对接消息通知模块
            log.warn("检测到退款失败记录需要通知：admin_notify_needed, id={}, orderId={}, amount={}", record.getId(), record.getOrderId(), record.getAmount());
        }
    }
}


