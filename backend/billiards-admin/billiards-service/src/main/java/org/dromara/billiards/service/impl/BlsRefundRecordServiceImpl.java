package org.dromara.billiards.service.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.dromara.billiards.common.constant.BilliardsConstants;
import org.dromara.billiards.common.constant.TransTypeEnum;
import org.dromara.billiards.domain.bo.BlsRefundRecordBo;
import org.dromara.billiards.domain.entity.BlsRefundRecord;
import org.dromara.billiards.domain.entity.PayRecord;
import org.dromara.billiards.domain.vo.BlsRefundRecordVo;
import org.dromara.billiards.service.*;
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
    private final UserService billiardsUservice;

    @Resource
    @Lazy
    private OrderService orderService;

    /**
     * 是否启用模拟支付（开发环境使用）
     */
    @Value("${payment.mock-enabled:true}")
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
    public void createRefund(String orderId, PayRecord lastPayRecord, BigDecimal deductBalance) {
        BlsRefundRecord refundRecord = new BlsRefundRecord();
        refundRecord.setPayRecordId(lastPayRecord.getId());
        refundRecord.setOrderId(orderId);
        refundRecord.setUserId(LoginHelper.getUserId());
        refundRecord.setAmount(deductBalance);
        refundRecord.setRefundStatus(0); // 0 for refunding
        baseMapper.insert(refundRecord);
        if (mockPaymentEnabled) {
            log.info("模拟退款模式已启用，自动完成微信退款回调步骤！");
            refundSuccess(refundRecord, "模拟退款", lastPayRecord.getTransactionId());
            return;
        }
        // 发起退款动作
        payService.refund(lastPayRecord.getTransactionId(), lastPayRecord.getId(), deductBalance, refundRecord.getId());
    }

    @Override
    public boolean handleRefundNotify(HttpServletRequest request, HttpServletResponse response) {
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
            String outRefundNo = resource.getStr("out_refund_no"); // 退款记录id，refund_id
            String outTradeNo = resource.getStr("out_trade_no"); // 原pay_record的id
            String state = resource.getStr("status"); // 交易状态

            // 查询支付记录
            BlsRefundRecord refundRecordVo = baseMapper.selectById(outRefundNo);

            if (refundRecordVo == null) {
                log.error("退款回调找不到对应的退款记录: {}", outRefundNo);
                payService.responseError(response);
                return false;
            }

            // 判断微信的支付状态
            if ("SUCCESS".equals(state)) {
                //  更新退款记录
                refundSuccess(refundRecordVo, notifyData, transactionId);

            } else {
                // 微信退款失败！可能要发送短信通知管理员！ todo
            }
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

    private void refundSuccess(BlsRefundRecord refundRecordVo, String notifyData, String transactionId) {
        BlsRefundRecord refundRecordBo = new BlsRefundRecord();
        refundRecordBo.setId(refundRecordVo.getId());
        refundRecordBo.setRefundStatus(1); // 退款成功
        refundRecordBo.setNotifyTime(LocalDateTime.now());
        refundRecordBo.setNotifyData(notifyData);
        refundRecordBo.setRemark("退款成功");
        baseMapper.updateById(refundRecordBo);

        // 更新用户余额
        billiardsUservice.deductBalance(refundRecordVo.getAmount(), refundRecordVo.getUserId());

        // 更新订单的付款状态
        orderService.completeOrder(refundRecordVo.getOrderId());

        // 新增钱包流水记录 BlsWalletTransaction
        walletTransactionService.addWalletTransaction(refundRecordVo.getUserId(), refundRecordVo.getAmount(), refundRecordVo.getId(), transactionId, "退款成功", TransTypeEnum.REFUND);

        // 更新钱包 BlsWalletAccount
        walletAccountService.updateWalletBalance(refundRecordVo.getUserId(), refundRecordVo.getAmount().negate(), "退款成功");
    }


}
