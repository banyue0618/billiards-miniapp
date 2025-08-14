package org.dromara.billiards.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.dromara.billiards.common.constant.BilliardsConstants;
import org.dromara.billiards.common.constant.TransTypeEnum;
import org.dromara.billiards.common.exception.BilliardsException;
import org.dromara.billiards.common.result.ResultCode;
import org.dromara.billiards.mapper.PayRecordMapper;
import org.dromara.billiards.domain.bo.PaymentRequest;
import org.dromara.billiards.domain.entity.PayRecord;
import org.dromara.billiards.domain.entity.User;
import org.dromara.billiards.service.*;
import org.dromara.common.pay.service.PayService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * 充值支付记录 Service 实现
 *
 * @author zhangsip
 * @date 2024-07-02
 */
@Slf4j
@Service
@DS(BilliardsConstants.DS_BILLIARDS_PLATFORM)
public class PayRecordServiceImpl extends ServiceImpl<PayRecordMapper, PayRecord> implements IPayRecordService {

    @Resource
    private PayService payService;

    @Resource
    private UserService userService;

    @Resource
    private IBlsWalletTransactionService walletTransactionService;

    @Resource
    private IBlsWalletAccountService walletAccountService;

    /**
     * 是否启用模拟支付（开发环境使用）
     */
    @Value("${payment.mock-enabled:true}")
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
        payRecord.setPaymentStatus(0); // 未支付

        // 保存支付记录
        boolean saved = this.save(payRecord);
        if (!saved) {
            throw BilliardsException.of("创建支付记录失败");
        }

        // 如果启用了模拟支付，则直接更新用户余额并返回成功
        if (mockPaymentEnabled) {
            log.info("模拟支付模式已启用，直接更新用户余额");
            boolean updated = userService.updateBalance(payRecord.getUserId(), payRecord.getAmount());
            if (updated) {
                // 更新支付记录状态为已支付
                payRecord.setPaymentStatus(1);
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
    @Transactional(rollbackFor = Exception.class)
    public boolean handlePayNotify(HttpServletRequest request, HttpServletResponse response) {
        try {
            String notifyData = payService.resolveNotifyData(request);
            if(notifyData == null){
                payService.responseError(response);
                return false;
            }
            // 解析回调数据
            JSONObject jsonObject = JSONUtil.parseObj(notifyData);
            JSONObject resource = jsonObject.getJSONObject("resource");
            String transactionId = resource.getStr("transaction_id"); // 微信支付交易号
            String outTradeNo = resource.getStr("out_trade_no"); // 商户订单号，即payNo
            String tradeState = resource.getStr("trade_state"); // 交易状态

            // 查询支付记录
            PayRecord payRecord = this.getOne(new LambdaQueryWrapper<PayRecord>()
                .eq(PayRecord::getId, outTradeNo));

            if (payRecord == null) {
                log.error("支付回调找不到对应的支付记录: {}", outTradeNo);
                payService.responseError(response);
                return false;
            }

            // 判断是否已处理过
            if (payRecord.getPaymentStatus() == 1) {
                payService.responseSuccess(response);
                log.info("支付回调重复处理: {}", outTradeNo);
                return true;
            }

            // 更新支付记录
            payRecord.setTransactionId(transactionId);
            payRecord.setNotifyTime(LocalDateTime.now());
            payRecord.setNotifyData(notifyData);

            // 判断支付状态
            if ("SUCCESS".equals(tradeState)) {
                // 支付成功，更新用户余额
                boolean updated = userService.updateBalance(payRecord.getUserId(), payRecord.getAmount());
                if (!updated) {
                    log.error("更新用户余额失败: userId={}, amount={}", payRecord.getUserId(), payRecord.getAmount());
                    return false;
                }
                // 新增钱包流水记录 BlsWalletTransaction
                walletTransactionService.addWalletTransaction(payRecord.getUserId(), payRecord.getAmount(), payRecord.getId(), transactionId, "支付成功", TransTypeEnum.RECHARGE);

                // 更新钱包 BlsWalletAccount
                walletAccountService.updateWalletBalance(payRecord.getUserId(), payRecord.getAmount(), "支付成功");

                payRecord.setPaymentStatus(1); // 已支付
                payRecord.setRemark("支付成功");
            } else {
                payRecord.setPaymentStatus(0); // 未支付
                payRecord.setRemark("支付失败: " + tradeState);
                log.error("支付回调状态异常: {}", tradeState);
                return false;
            }

            payRecord.setUpdateTime(LocalDateTime.now());
            this.updateById(payRecord);

            payService.responseSuccess(response);
            return true;
        } catch (Exception e) {
            log.error("处理支付回调异常", e);
            try {
                payService.responseError(response);
            } catch (IOException ioException) {
                log.error("返回错误响应异常", ioException);
            }
            return false;
        }
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

    /**
     * 生成支付流水号
     */
    private String generatePayNo() {
        return "PAY" + System.currentTimeMillis() + IdUtil.fastSimpleUUID().substring(0, 6).toUpperCase();
    }
}
