package org.dromara.billiards.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyV3Result;
import com.github.binarywang.wxpay.bean.result.WxPayOrderQueryV3Result;
import com.github.binarywang.wxpay.constant.WxPayConstants;
import com.github.binarywang.wxpay.exception.WxPayException;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.dromara.billiards.common.constant.BilliardsConstants;
import org.dromara.billiards.common.constant.PaymentStatus;
import org.dromara.billiards.common.exception.BilliardsException;
import org.dromara.billiards.common.result.ResultCode;
import org.dromara.billiards.domain.entity.BlsWalletAccount;
import org.dromara.billiards.mapper.PayRecordMapper;
import org.dromara.billiards.domain.bo.PaymentRequest;
import org.dromara.billiards.domain.entity.PayRecord;
import org.dromara.billiards.domain.entity.User;
import org.dromara.billiards.service.*;
import org.dromara.common.pay.service.PayService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 充值支付记录 Service 实现
 *
 * @author zhangsip
 * @date 2024-07-02
 */
@Slf4j
@Service
@DS(BilliardsConstants.DS_BILLIARDS_PLATFORM)
public class BlsPayRecordServiceImpl extends ServiceImpl<PayRecordMapper, PayRecord> implements IBlsPayRecordService {

    @Resource
    private PayService payService;

    @Resource
    @Lazy
    private UserService userService;

    @Resource
    private IBlsWalletAccountService walletAccountService;

    /**
     * 是否启用模拟支付（开发环境使用）
     */
    @Value("${billiards.payment.mock-enabled:false}")
    private boolean mockPaymentEnabled;

    @Override
    public String createPayment(PaymentRequest request, String channel) {
        User user = userService.getUserInfoById();
        log.info("创建支付订单: userId={}, openid={}, amount={}", user.getId(), user.getOpenid(), request.getAmount());

        // 创建支付记录
        PayRecord payRecord = new PayRecord();
        payRecord.setId(IdUtil.fastSimpleUUID());
        payRecord.setOpenid(user.getOpenid());
        payRecord.setPayNo(generatePayNo());
        payRecord.setUserId(user.getId());
        payRecord.setAmount(request.getAmount());
        payRecord.setChannel(channel);
        payRecord.setPaymentStatus(PaymentStatus.UNPAID.getCode()); // 未支付

        // 保存支付记录
        boolean saved = this.save(payRecord);
        if (!saved) {
            throw BilliardsException.of("创建支付记录失败");
        }

        // 如果启用了模拟支付，则直接更新用户余额并返回成功
        if (mockPaymentEnabled) {
            log.info("模拟支付模式已启用，直接更新用户余额");
            BlsWalletAccount walletAccount = walletAccountService.updateWalletBalance(user.getId(), request.getAmount());
            if (walletAccount.getBalance().compareTo(BigDecimal.ZERO) > 0) {
                // 更新支付记录状态为支付成功
                payRecord.setPaymentStatus(PaymentStatus.PAID.getCode());
                payRecord.setRemark("模拟支付成功");
                payRecord.setUpdateTime(LocalDateTime.now());
                this.updateById(payRecord);

                // 返回特殊标记，前端可据此判断是模拟支付
                return "{\"mock\":true,\"message\":\"模拟支付成功\"}";
            }
        }

        // 调用微信支付接口创建预支付订单
        try {
            // 调用微信支付服务创建jsapi预支付订单，返回支付参数
            String payParams = payService.jsApiPay(user.getOpenid(), payRecord.getAmount(), payRecord.getId());
            log.info("创建预支付订单成功，支付参数: {}", payParams);
            return payParams;
        } catch (Exception e) {
            log.error("创建预支付订单失败", e);
            throw BilliardsException.of(ResultCode.ERROR, "创建预支付订单失败: " + e.getMessage());
        }
    }

    @Override
    public WxPayOrderQueryV3Result queryPayStatus(String transactionId, String outTradeNo) throws WxPayException {
        WxPayOrderQueryV3Result queryV3Result = payService.queryPayResult(transactionId, outTradeNo);
        if (queryV3Result == null) {
            log.error("查询支付状态失败，未找到订单: transactionId={}, outTradeNo={}", transactionId, outTradeNo);
            throw BilliardsException.of(ResultCode.ERROR, "查询支付状态失败");
        }
        return queryV3Result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handlePayNotify(String notifyData, HttpServletRequest request) {
        try {
            // 解析回调数据
            WxPayNotifyV3Result result = payService.parseOrderNotifyResult(notifyData, request);
            WxPayNotifyV3Result.DecryptNotifyResult decryptRes = result.getResult();

            final String outTradeNo = decryptRes.getOutTradeNo(); // 商户订单号

            // 查询支付记录
            PayRecord payRecord = this.getOne(new LambdaQueryWrapper<PayRecord>()
                .eq(PayRecord::getId, outTradeNo));

            if (payRecord == null) {
                log.error("支付回调找不到对应的支付记录: {}", outTradeNo);
                return false;
            }

            // 判断是否已处理过，判断状态是否是 0或者1，如果不是，则表示已经处理过
            if(!(payRecord.getPaymentStatus() >= 0 && (payRecord.getPaymentStatus() & (payRecord.getPaymentStatus() - 1)) == 0)){
                log.info("支付回调重复处理: {}", outTradeNo);
                return true;
            }

            // 更新支付记录
            payRecord.setTransactionId(decryptRes.getTransactionId());
            payRecord.setNotifyTime(LocalDateTime.now());
            payRecord.setNotifyData(notifyData);
            payRecord.setRemark(decryptRes.getTradeStateDesc());
            // 判断支付状态
            if (WxPayConstants.WxpayTradeStatus.SUCCESS.equals(decryptRes.getTradeState())) {
                payRecord.setPaymentStatus(PaymentStatus.PAID.getCode()); // 支付成功
                log.info("支付成功: {}", outTradeNo);
                walletAccountService.updateWalletBalanceAndWalletTransaction(payRecord);
            } else {
                payRecord.setPaymentStatus(PaymentStatus.PAY_FAIL.getCode()); // 支付失败
                // todo 付款失败
            }
            payRecord.setUpdateTime(LocalDateTime.now());
            this.updateById(payRecord);
            return true;
        } catch (Exception e) {
            log.error("处理支付回调异常", e);
            BilliardsException.of(ResultCode.ERROR, "处理支付回调异常: " + e.getMessage());
        }
        return false;
    }

    @Override
    public PayRecord getLastPayRecord(Long userId) {
        // 根据用户查询最近的一条已支付记录
        return this.getOne(new LambdaQueryWrapper<PayRecord>()
            .eq(PayRecord::getUserId, userId)
            .eq(PayRecord::getPaymentStatus, 1)
            .orderByDesc(PayRecord::getCreateTime)
            .last("limit 1"));
    }

    @Override
    public List<PayRecord> queryListWithStatus(PaymentStatus paymentStatus) {
        LambdaQueryWrapper<PayRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(PayRecord::getPaymentStatus, paymentStatus.getCode());
        return baseMapper.selectList(lqw);
    }

    @Override
    public List<PayRecord> queryPayingTimeoutList(LocalDateTime threshold) {
        LambdaQueryWrapper<PayRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(PayRecord::getPaymentStatus, PaymentStatus.UNPAID.getCode())
            .lt(PayRecord::getCreateTime, threshold);
        return baseMapper.selectList(lqw);
    }

    /**
     * 生成支付流水号
     */
    private String generatePayNo() {
        return "PAY" + System.currentTimeMillis() + IdUtil.fastSimpleUUID().substring(0, 6).toUpperCase();
    }
}
