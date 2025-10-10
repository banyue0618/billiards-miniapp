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
import org.dromara.billiards.domain.entity.BlsPayRecord;
import org.dromara.billiards.domain.entity.BlsUser;
import org.dromara.billiards.service.*;
import org.dromara.common.core.utils.StringUtils;
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
public class BlsPayRecordServiceImpl extends ServiceImpl<PayRecordMapper, BlsPayRecord> implements IBlsPayRecordService {

    @Resource
    private PayService payService;

    @Resource
    @Lazy
    private UserService userService;

    @Resource
    private IBlsWalletAccountService walletAccountService;

    @Resource
    private IBlsUserTenantService userTenantService;

    @Resource
    private IBlsPayChannelConfigService payChannelConfigService;

    /**
     * 是否启用模拟支付（开发环境使用）
     */
    @Value("${billiards.payment.mock-enabled:false}")
    private boolean mockPaymentEnabled;

    @Value("${billiards.wechat.appid}")
    private String appid;

    @Override
    public String createPayment(PaymentRequest request, String channel) {
        BlsUser blsUser = userService.getUserInfoById();
        log.info("创建支付订单: userId={}, openid={}, amount={}", blsUser.getId(), blsUser.getOpenid(), request.getAmount());

        // 创建支付记录
        BlsPayRecord blsPayRecord = new BlsPayRecord();
        blsPayRecord.setId(IdUtil.fastSimpleUUID());
        blsPayRecord.setOpenid(blsUser.getOpenid());
        blsPayRecord.setPayNo(generatePayNo());
        blsPayRecord.setUserId(blsUser.getId());
        blsPayRecord.setAmount(request.getAmount());
        blsPayRecord.setChannel(channel);
        blsPayRecord.setPaymentStatus(PaymentStatus.UNPAID.getCode()); // 未支付

        // 保存支付记录
        boolean saved = this.save(blsPayRecord);
        if (!saved) {
            throw BilliardsException.of("创建支付记录失败");
        }

        // 保存用户所属租户记录
        userTenantService.saveUserTenantRecord(blsUser);

        // 如果启用了模拟支付，则直接更新用户余额并返回成功
        if (mockPaymentEnabled) {
            log.info("模拟支付模式已启用，直接更新用户余额");
            walletAccountService.updateWalletBalance(blsUser.getId(), request.getAmount());
            // 更新支付记录状态为支付成功
            blsPayRecord.setPaymentStatus(PaymentStatus.PAID.getCode());
            blsPayRecord.setRemark("模拟支付成功");
            blsPayRecord.setUpdateTime(LocalDateTime.now());
            this.updateById(blsPayRecord);

            // 返回特殊标记，前端可据此判断是模拟支付
            return "{\"mock\":true,\"message\":\"模拟支付成功\"}";
        }

        // 调用微信支付接口创建预支付订单
        try {
            // 查询商户id
            String merchantId = payChannelConfigService.selectMerchantIdByStoreId(request.getStoreId());
            if(StringUtils.isEmpty(merchantId)){
                throw BilliardsException.of(ResultCode.ERROR, "未配置有效的支付渠道");
            }
            // 调用微信支付服务创建jsapi预支付订单，返回支付参数 prepay_id
            String payParams = payService.jsApiPay(appid, blsUser.getOpenid(), blsPayRecord.getAmount(), blsPayRecord.getId(), merchantId);
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
            BlsPayRecord blsPayRecord = this.getOne(new LambdaQueryWrapper<BlsPayRecord>()
                .eq(BlsPayRecord::getId, outTradeNo));

            if (blsPayRecord == null) {
                log.error("支付回调找不到对应的支付记录: {}", outTradeNo);
                return false;
            }

            // 判断是否已处理过，判断状态是否是 0或者1，如果不是，则表示已经处理过
            if(!(blsPayRecord.getPaymentStatus() >= 0 && (blsPayRecord.getPaymentStatus() & (blsPayRecord.getPaymentStatus() - 1)) == 0)){
                log.info("支付回调重复处理: {}", outTradeNo);
                return true;
            }

            // 更新支付记录
            blsPayRecord.setTransactionId(decryptRes.getTransactionId());
            blsPayRecord.setNotifyTime(LocalDateTime.now());
            blsPayRecord.setNotifyData(notifyData);
            blsPayRecord.setRemark(decryptRes.getTradeStateDesc());
            // 判断支付状态
            if (WxPayConstants.WxpayTradeStatus.SUCCESS.equals(decryptRes.getTradeState())) {
                blsPayRecord.setPaymentStatus(PaymentStatus.PAID.getCode()); // 支付成功
                log.info("支付成功: {}", outTradeNo);
                walletAccountService.updateWalletBalanceAndWalletTransaction(blsPayRecord);
            } else {
                blsPayRecord.setPaymentStatus(PaymentStatus.PAY_FAIL.getCode()); // 支付失败
                // todo 付款失败
            }
            blsPayRecord.setUpdateTime(LocalDateTime.now());
            this.updateById(blsPayRecord);
            return true;
        } catch (Exception e) {
            log.error("处理支付回调异常", e);
            BilliardsException.of(ResultCode.ERROR, "处理支付回调异常: " + e.getMessage());
        }
        return false;
    }

    @Override
    public BlsPayRecord getLastPayRecord(Long userId) {
        // 根据用户查询最近的一条已支付记录
        return this.getOne(new LambdaQueryWrapper<BlsPayRecord>()
            .eq(BlsPayRecord::getUserId, userId)
            .eq(BlsPayRecord::getPaymentStatus, PaymentStatus.PAID.getCode())
            .orderByDesc(BlsPayRecord::getCreateTime)
            .last("limit 1"));
    }

    @Override
    public List<BlsPayRecord> queryListWithStatus(PaymentStatus paymentStatus) {
        LambdaQueryWrapper<BlsPayRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(BlsPayRecord::getPaymentStatus, paymentStatus.getCode());
        return baseMapper.selectList(lqw);
    }

    @Override
    public List<BlsPayRecord> queryPayingTimeoutList(LocalDateTime threshold) {
        LambdaQueryWrapper<BlsPayRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(BlsPayRecord::getPaymentStatus, PaymentStatus.UNPAID.getCode())
            .lt(BlsPayRecord::getCreateTime, threshold);
        return baseMapper.selectList(lqw);
    }

    /**
     * 生成支付流水号
     */
    private String generatePayNo() {
        return "PAY" + System.currentTimeMillis() + IdUtil.fastSimpleUUID().substring(0, 6).toUpperCase();
    }
}
