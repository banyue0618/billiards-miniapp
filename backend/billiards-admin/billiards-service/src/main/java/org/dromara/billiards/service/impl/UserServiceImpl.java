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
import org.dromara.billiards.mapper.UserMapper;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.dromara.billiards.service.IBlsPayRecordService;
import org.dromara.billiards.service.IBlsWalletAccountService;
import org.dromara.billiards.service.UserService;
import org.dromara.billiards.domain.bo.UserUpdateDto;
import org.dromara.billiards.convert.UserConvert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dromara.common.satoken.utils.LoginHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

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
}
