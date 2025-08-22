package org.dromara.billiards.service.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.github.binarywang.wxpay.bean.notify.WxPayRefundNotifyResult;
import com.github.binarywang.wxpay.bean.notify.WxPayRefundNotifyV3Result;
import com.github.binarywang.wxpay.bean.result.WxPayRefundQueryV3Result;
import com.github.binarywang.wxpay.constant.WxPayConstants;
import com.github.binarywang.wxpay.exception.WxPayException;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.dromara.billiards.common.constant.BilliardsConstants;
import org.dromara.billiards.common.constant.TransTypeEnum;
import org.dromara.billiards.common.exception.BilliardsException;
import org.dromara.billiards.common.result.ResultCode;
import org.dromara.billiards.domain.bo.BlsRefundRecordBo;
import org.dromara.billiards.domain.entity.BlsRefundRecord;
import org.dromara.billiards.domain.entity.Order;
import org.dromara.billiards.domain.entity.PayRecord;
import org.dromara.billiards.domain.vo.BlsRefundRecordVo;
import org.dromara.billiards.service.*;
import org.dromara.billiards.notify.event.RefundFailureEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.dromara.common.pay.service.PayService;
import org.dromara.common.satoken.utils.LoginHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.dromara.billiards.mapper.BlsRefundRecordMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Collection;

/**
 * 退款记录Service业务层处理
 *
 * @author banyue
 * @date 2025-06-08
 */
@RequiredArgsConstructor
@Service
@Slf4j
@DS(BilliardsConstants.DS_BILLIARDS_PLATFORM)
public class BlsRefundRecordServiceImpl implements IBlsRefundRecordService {

    private final BlsRefundRecordMapper baseMapper;
    private final PayService payService;
    private final IBlsWalletTransactionService walletTransactionService;
    private final IBlsWalletAccountService walletAccountService;

    @Resource
    @Lazy
    private OrderService orderService;

    @Resource
    private ApplicationEventPublisher eventPublisher;

    /**
     * 是否启用模拟支付（开发环境使用）
     */
    @Value("${billiards.payment.mock-enabled:true}")
    private boolean mockPaymentEnabled;

    /**
     * 查询退款记录
     *
     * @param id 主键
     * @return 退款记录
     */
    @Override
    public BlsRefundRecordVo queryById(String id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询退款记录列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 退款记录分页列表
     */
    @Override
    public TableDataInfo<BlsRefundRecordVo> queryPageList(BlsRefundRecordBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BlsRefundRecord> lqw = buildQueryWrapper(bo);
        Page<BlsRefundRecordVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的退款记录列表
     *
     * @param bo 查询条件
     * @return 退款记录列表
     */
    @Override
    public List<BlsRefundRecordVo> queryList(BlsRefundRecordBo bo) {
        LambdaQueryWrapper<BlsRefundRecord> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<BlsRefundRecord> buildQueryWrapper(BlsRefundRecordBo bo) {
        LambdaQueryWrapper<BlsRefundRecord> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(BlsRefundRecord::getId);
        lqw.eq(StringUtils.isNotBlank(bo.getPayRecordId()), BlsRefundRecord::getPayRecordId, bo.getPayRecordId());
        lqw.eq(StringUtils.isNotBlank(bo.getOrderId()), BlsRefundRecord::getOrderId, bo.getOrderId());
        lqw.eq(bo.getUserId() != null, BlsRefundRecord::getUserId, bo.getUserId());
        lqw.eq(bo.getAmount() != null, BlsRefundRecord::getAmount, bo.getAmount());
        lqw.eq(bo.getRefundStatus() != null, BlsRefundRecord::getRefundStatus, bo.getRefundStatus());
        lqw.eq(bo.getNotifyTime() != null, BlsRefundRecord::getNotifyTime, bo.getNotifyTime());
        lqw.eq(StringUtils.isNotBlank(bo.getNotifyData()), BlsRefundRecord::getNotifyData, bo.getNotifyData());
        return lqw;
    }

    /**
     * 新增退款记录
     *
     * @param bo 退款记录
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(BlsRefundRecordBo bo) {
        BlsRefundRecord add = MapstructUtils.convert(bo, BlsRefundRecord.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改退款记录
     *
     * @param bo 退款记录
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(BlsRefundRecordBo bo) {
        BlsRefundRecord update = MapstructUtils.convert(bo, BlsRefundRecord.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(BlsRefundRecord entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除退款记录信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteByIds(ids) > 0;
    }

    @Override
    public BlsRefundRecord queryRecordByOrderId(String orderId) {
        if (StringUtils.isBlank(orderId)) {
            return null;
        }
        LambdaQueryWrapper<BlsRefundRecord> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(BlsRefundRecord::getOrderId, orderId);
        return baseMapper.selectOne(queryWrapper);
    }

    @Override
    public void refund(String orderId, PayRecord lastPayRecord, BigDecimal refundAmount) {
        BlsRefundRecord refundRecord = new BlsRefundRecord();
        refundRecord.setPayRecordId(lastPayRecord.getId());
        refundRecord.setOrderId(orderId);
        refundRecord.setUserId(LoginHelper.getUserId());
        refundRecord.setAmount(refundAmount);
        refundRecord.setRefundStatus(0); // 0 for refunding
        refundRecord.setTransactionId(lastPayRecord.getTransactionId());
        baseMapper.insert(refundRecord);
        if (mockPaymentEnabled) {
            log.info("模拟退款模式已启用，自动完成微信退款回调步骤！");
            refundRecord.setRemark("模拟退款成功！");
            refundSuccess(refundRecord);
            return;
        }
        // 发起退款动作
        try {
            payService.refund(lastPayRecord.getTransactionId(), lastPayRecord.getId(), refundAmount, refundRecord.getId());
        } catch (WxPayException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean handleRefundNotify(String notifyData, HttpServletRequest request) {
        try {
            WxPayRefundNotifyV3Result result = payService.parseRefundNotifyResult(notifyData, request);
            WxPayRefundNotifyV3Result.DecryptNotifyResult decryptRes = result.getResult();
            // 查询退款支付记录
            BlsRefundRecord refundRecord = baseMapper.selectById(decryptRes.getOutRefundNo());

            if (refundRecord == null) {
                log.error("退款回调找不到对应的退款记录: {}", decryptRes.getOutRefundNo());
                return false;
            }

            // 判断是否已处理过，判断状态是否是 0，如果不是，则表示已经处理过
            if(refundRecord.getRefundStatus() != 0){
                log.error("退款已处理: {}", decryptRes.getOutRefundNo());
                return true;
            }

            refundRecord.setNotifyTime(LocalDateTime.now());
            refundRecord.setNotifyData(notifyData);

            // 处理退款结果
            handleRefundResult(decryptRes.getRefundStatus(), refundRecord, false);
        } catch (Exception e) {
            log.error("处理支付回调异常", e);
            return false;
        }
        return true;
    }

    @Override
    public WxPayRefundQueryV3Result queryRefundResult(String outRefundNo) throws Exception {
        WxPayRefundQueryV3Result refundResult = payService.queryRefundResult(outRefundNo);
        if (refundResult == null) {
            log.error("查询退款状态失败，未找到订单: outRefundNo={}", outRefundNo);
            throw BilliardsException.of(ResultCode.ERROR, "查询退款状态失败");
        }
        return refundResult;
    }

    @Override
    public boolean handleRefundResult(String refundStatus, BlsRefundRecord refundRecord, boolean ifThrowException) {
        log.info("退款单号:{},处理退款结果: {}", refundRecord.getId(), refundStatus);

        // 退款中，无需处理，等待微信回调
        if(WxPayConstants.RefundStatus.PROCESSING.equals(refundStatus)){
            return true;
        }

        // 退款成功
        if(WxPayConstants.RefundStatus.SUCCESS.equals(refundStatus)){
            refundSuccess(refundRecord);
            return true;
        }
        // 退款异常，直接更新为退款失败
        if(WxPayConstants.RefundStatus.ABNORMAL.equals(refundStatus)) {
            notifyMchIfRefundFail(refundRecord, refundStatus, ifThrowException);
            return true;
        }
        // 退款关闭
        if(WxPayConstants.RefundStatus.CLOSED.equals(refundStatus)){
            notifyMchIfRefundFail(refundRecord, refundStatus, ifThrowException);
            return true;
        }
        return true;
    }

    /**
     * 退款成功处理：更新退款记录、完成订单、记钱包流水与余额
     */
    private void refundSuccess(BlsRefundRecord refundRecord) {
        // 退款成功 更新退款记录
        refundRecord.setRefundStatus(1); // 退款成功
        refundRecord.setRemark("退款成功");
        baseMapper.updateById(refundRecord);

        // 更新订单的付款状态
        orderService.completeOrder(refundRecord.getOrderId());

        // 新增钱包流水记录 BlsWalletTransaction
        walletTransactionService.addWalletTransaction(refundRecord.getUserId(), refundRecord.getAmount(), refundRecord.getId(), refundRecord.getTransactionId(), "退款成功", TransTypeEnum.REFUND);

        // 更新钱包 BlsWalletAccount
        walletAccountService.updateWalletBalance(refundRecord.getUserId(), refundRecord.getAmount().negate());
    }

    /**
     * 退款失败处理：仅更新记录，通知在外部事件机制完成
     */
    private void refundFail(BlsRefundRecord refundRecord) {
        // 更新退款记录为失败
        refundRecord.setRefundStatus(2); // 退款失败
        refundRecord.setRemark("退款失败");
        baseMapper.updateById(refundRecord);
        // todo 用户钱包得相关流水咋整呢？
    }

    /**
     * 退款失败/关闭等异常场景通知商户：
     * - 记录日志
     * - 可选抛出业务异常
     * - 发布退款失败事件，交由监听器按通道发送通知
     */
    private void notifyMchIfRefundFail(BlsRefundRecord refundRecord, String refundStatus, boolean ifThrowException) {

        Order order = orderService.getById(refundRecord.getOrderId());

        // 退款异常，提示管理员前往微信商户平台处理
        if(WxPayConstants.RefundStatus.ABNORMAL.equals(refundStatus)){
            log.error("订单退款异常，请前往微信商户平台处理，订单号：{}", order.getOrderNo());
            if(ifThrowException) {
                throw BilliardsException.of(ResultCode.REFUND_ABNORMAL);
            }
            // 发布退款失败事件（异常）
            eventPublisher.publishEvent(new RefundFailureEvent(this, refundRecord, order, refundStatus));
        }

        // 退款关闭，但是确实需要退款得话，提示商家操作
        if(WxPayConstants.RefundStatus.CLOSED.equals(refundStatus)){
            log.warn("订单退款已关闭，请商家手动处理，订单号：{}", order.getOrderNo());
            if(ifThrowException) {
                throw BilliardsException.of("订单退款已关闭，请商家手动处理还款，可能是商户账号上余额不足，请前往查看，然后再次操作退款");
            }
            // 发布退款失败事件（关闭）
            eventPublisher.publishEvent(new RefundFailureEvent(this, refundRecord, order, refundStatus));

        }

        // 退款中，直接更新为退款失败
        if(refundRecord.getRefundStatus() == 0){
            refundFail(refundRecord);
            // 发布退款失败事件（失败）
            eventPublisher.publishEvent(new RefundFailureEvent(this, refundRecord, order, refundStatus));
        }
    }
}
