package org.dromara.billiards.service.impl;

import com.github.binarywang.wxpay.bean.result.WxPayOrderQueryV3Result;
import com.github.binarywang.wxpay.constant.WxPayConstants;
import com.github.binarywang.wxpay.exception.WxPayException;
import org.dromara.billiards.common.constant.BilliardsConstants;
import org.dromara.billiards.common.constant.PaymentStatus;
import org.dromara.billiards.common.exception.BilliardsException;
import org.dromara.billiards.common.result.ResultCode;
import org.dromara.billiards.domain.entity.BlsUser;
import org.dromara.billiards.domain.entity.BlsWalletAccount;
import org.dromara.billiards.domain.entity.BlsPayRecord;
import org.dromara.billiards.domain.entity.BlsRefundRecord;
import org.dromara.billiards.domain.vo.OrderVO;
import org.dromara.billiards.domain.vo.RechargeRecordVO;
import org.dromara.billiards.domain.vo.UserVO;
import org.dromara.billiards.mapper.UserMapper;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.dromara.billiards.service.*;
import org.dromara.billiards.domain.bo.UserUpdateDto;
import org.dromara.billiards.convert.UserConvert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dromara.common.satoken.utils.LoginHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
@DS(BilliardsConstants.DS_BILLIARDS_PLATFORM)
public class UserServiceImpl extends ServiceImpl<UserMapper, BlsUser> implements UserService {

    private final IBlsWalletAccountService walletAccountService;

    private final IBlsPayRecordService payRecordService;

    private final IBlsRefundRecordService refundRecordService;

    private final OrderService orderService;

    private final UserConvert userConvert = UserConvert.INSTANCE;

    /**
     * 根据手机号获取用户
     * @param phone 手机号
     * @return 用户对象
     */
    @Override
    public BlsUser getByPhone(String phone) {
        return baseMapper.selectByPhone(phone);
    }

    /**
     * 根据OpenID获取用户
     * @param openid 微信OpenID
     * @return 用户对象
     */
    @Override
    public BlsUser getByOpenid(String openid) {
        return baseMapper.selectByOpenid(openid);
    }

    /**
     * 更新当前登录用户的基本信息
     * @param dto 包含要更新信息的数据传输对象
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateCurrentUserInfo(UserUpdateDto dto) {
        BlsUser existBlsUser = this.getUserInfoById();
        // 使用 MapStruct 更新允许修改的字段
        userConvert.updateUserFromDto(dto, existBlsUser);

        return this.updateById(existBlsUser);
    }

    /**
     * 绑定用户手机号
     * @param phone 手机号
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean bindUserPhone(String phone) {
        if (StringUtils.isBlank(phone)) {
            throw BilliardsException.of(ResultCode.PARAM_ERROR, "手机号不能为空");
        }

        Long userId = LoginHelper.getUserId();
        BlsUser blsUser = this.getUserInfoById();
        // 检查手机号是否已绑定其他用户
        BlsUser existPhone = this.getByPhone(phone);
        if (existPhone != null && !existPhone.getId().equals(userId)) {
            throw BilliardsException.of(ResultCode.PHONE_ALREADY_BOUND);
        }

        // 更新手机号
        blsUser.setPhone(phone);
        return this.updateById(blsUser);
    }

    /**
     * 获取用户详细信息，若不存在则抛出异常
     * @return 用户实体
     */
    @Override
    public BlsUser getUserInfoById() {
        return this.getById(LoginHelper.getUserId());
    }

    @Override
    public BlsUser getUserInfoById(Long userId) {
        return this.getById(userId);
    }

    /**
     * 检查用户是否满足开台条件，满足返回true,否则返回false
     * @return 是否足够
     */
    @Override
    public boolean scanTableEnableCheck() {
        // 检查用户是否有足够的余额进行扫码开台
        BlsWalletAccount walletAccount = walletAccountService.getWalletAccountByUserId(LoginHelper.getUserId());

        // 检查余额是否大于0
        if(walletAccount.getBalance().compareTo(BigDecimal.ZERO) > 0){
            return true;
        }

        // 余额未更新则检查是否有充值记录,查询最近一次的支付记录
        BlsPayRecord lastBlsPayRecord = payRecordService.getLastPayRecord(LoginHelper.getUserId());
        if (lastBlsPayRecord != null) {
            if(PaymentStatus.UNPAID.getCode() == lastBlsPayRecord.getPaymentStatus()){
                // 如果最近的支付记录状态为未支付，表示用户还未支付，不能开台
                return false;
            }
            if(PaymentStatus.PAYING.getCode() == lastBlsPayRecord.getPaymentStatus()){
                // 如果最近的支付记录状态为支付中，调用微信支付接口查询支付结果
                try {
                    WxPayOrderQueryV3Result payStatus = payRecordService.queryPayStatus(lastBlsPayRecord.getTransactionId(), lastBlsPayRecord.getPayNo());
                    if(WxPayConstants.ResultCode.SUCCESS.equals(payStatus.getTradeState())){
                        // 如果查询到支付成功，则认为可以开台
                        return true;
                    }
                    // 表示支付失败
                } catch (WxPayException e) {
                    log.error("查询支付状态失败", e);
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * 获取当前用户的充值记录列表
     * @return 充值记录列表
     */
    @Override
    public List<RechargeRecordVO> getRechargeList() {
        Long userId = LoginHelper.getUserId();

        // 查询用户的充值记录
        LambdaQueryWrapper<BlsPayRecord> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(BlsPayRecord::getUserId, userId)
                   .orderByDesc(BlsPayRecord::getCreateTime);

        List<BlsPayRecord> payRecords = payRecordService.list(queryWrapper);

        // 转换为VO对象
        return payRecords.stream().map(this::convertToRechargeRecordVO).collect(Collectors.toList());
    }

    /**
     * 申请退款
     * @param payRecordId 充值记录ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void applyRefund(String payRecordId) {
        if (StringUtils.isBlank(payRecordId)) {
            throw BilliardsException.of(ResultCode.PARAM_ERROR, "充值记录ID不能为空");
        }

        Long userId = LoginHelper.getUserId();

        // 先检查当前用户是否处于开台中
        List<OrderVO> currentOrder = orderService.getCurrentOrder(userId);
        if (currentOrder != null && !currentOrder.isEmpty()) {
            throw BilliardsException.of(ResultCode.ERROR, "当前有未结账订单，无法申请退款");
        }

        // 查询充值记录
        BlsPayRecord payRecord = payRecordService.getById(payRecordId);
        if (payRecord == null) {
            throw BilliardsException.of(ResultCode.NOT_FOUND, "充值记录不存在");
        }

        // 验证是否为当前用户的记录
        if (!payRecord.getUserId().equals(userId)) {
            throw BilliardsException.of(ResultCode.FORBIDDEN, "无权限操作此记录");
        }

        // 检查支付状态
        if (payRecord.getPaymentStatus() != PaymentStatus.PAID.getCode()) {
            throw BilliardsException.of(ResultCode.ERROR, "只有已支付的充值记录才能申请退款");
        }

        // 检查是否已有退款记录
        BlsRefundRecord existingRefund = refundRecordService.queryRecordByPayRecordId(payRecordId);
        if (existingRefund != null) {
            throw BilliardsException.of(ResultCode.ERROR, "该充值记录已申请过退款");
        }

        // 检查当前用户余额，余额必须大于0才支持退款
        BlsWalletAccount walletAccount = walletAccountService.getWalletAccountByUserId(userId);
        if (walletAccount == null || walletAccount.getBalance().compareTo(BigDecimal.ZERO) <= 0) {
            throw BilliardsException.of(ResultCode.ERROR, "用户余额不足，无法申请退款");
        }

        // 调用退款服务
        refundRecordService.refund(payRecordId, payRecord, payRecord.getAmount());

        log.info("用户{}申请退款成功，充值记录ID：{}", userId, payRecordId);
    }

    @Override
    public UserVO getUserInfo() {
        BlsUser info = getUserInfoById();
        // 查询用户余额
        BlsWalletAccount walletAccount = walletAccountService.getWalletAccountByUserId(info.getId());
        UserVO userVO = userConvert.toVo(info);
        if (walletAccount != null) {
            userVO.setBalance(walletAccount.getBalance());
        }
        return userVO;
    }

    /**
     * 转换充值记录为VO对象
     */
    private RechargeRecordVO convertToRechargeRecordVO(BlsPayRecord payRecord) {
        RechargeRecordVO vo = new RechargeRecordVO();
        vo.setId(payRecord.getId());
        vo.setPayNo(payRecord.getPayNo());
        vo.setAmount(payRecord.getAmount());
        vo.setChannel(payRecord.getChannel());
        vo.setPaymentStatus(payRecord.getPaymentStatus());
        vo.setTransactionId(payRecord.getTransactionId());
        vo.setCreateTime(payRecord.getCreateTime());
        vo.setRemark(payRecord.getRemark());

        // 查询退款记录
        BlsRefundRecord refundRecord = refundRecordService.queryRecordByPayRecordId(payRecord.getId());
        if (refundRecord != null) {
            vo.setRefundStatus(refundRecord.getRefundStatus());
            vo.setRefundAmount(refundRecord.getAmount());
        }else{
            // 无需退款
            vo.setRefundStatus(-1);
        }


        return vo;
    }
}
