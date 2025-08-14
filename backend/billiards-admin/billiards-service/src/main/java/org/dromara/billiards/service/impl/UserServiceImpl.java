package org.dromara.billiards.service.impl;

import org.dromara.billiards.common.constant.BilliardsConstants;
import org.dromara.billiards.common.exception.BilliardsException;
import org.dromara.billiards.common.result.ResultCode;
import org.dromara.billiards.mapper.UserMapper;
import org.dromara.billiards.domain.entity.User;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.dromara.billiards.service.UserService;
import org.dromara.billiards.domain.bo.UserUpdateDto;
import org.dromara.billiards.convert.UserConvert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dromara.common.satoken.utils.LoginHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
@DS(BilliardsConstants.DS_BILLIARDS_PLATFORM)
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserConvert userConvert = UserConvert.INSTANCE;

    /**
     * 根据手机号获取用户
     * @param phone 手机号
     * @return 用户对象
     */
    @Override
    public User getByPhone(String phone) {
        return baseMapper.selectByPhone(phone);
    }

    /**
     * 根据OpenID获取用户
     * @param openid 微信OpenID
     * @return 用户对象
     */
    @Override
    public User getByOpenid(String openid) {
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
        User existUser = this.getUserInfoById();
        // 使用 MapStruct 更新允许修改的字段
        userConvert.updateUserFromDto(dto, existUser);

        return this.updateById(existUser);
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
        User user = this.getUserInfoById();
        // 检查手机号是否已绑定其他用户
        User existPhone = this.getByPhone(phone);
        if (existPhone != null && !existPhone.getId().equals(userId)) {
            throw BilliardsException.of(ResultCode.PHONE_ALREADY_BOUND);
        }

        // 更新手机号
        user.setPhone(phone);
        return this.updateById(user);
    }

    /**
     * 获取用户详细信息，若不存在则抛出异常
     * @return 用户实体
     */
    @Override
    public User getUserInfoById() {
        return this.getById(LoginHelper.getUserId());
    }

    @Override
    public User getUserInfoById(Long userId) {
        return this.getById(userId);
    }

    /**
     * 检查用户余额是否足够,足够返回true,否则返回false
     * @return 是否足够
     */
    @Override
    public boolean scanTableEnableCheck() {
        // 检查用户是否有足够的余额进行扫码开台
        User user = this.getUserInfoById();
        // 1. 如果是会员，直接返回true
//        if (user.getIsMember() != null && user.getIsMember() == 1) {
//            return true;
//        }
        // 2. 检查余额是否大于0
        if (user.getBalance() != null && user.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            return true;
        }
        return false;
    }

    /**
     * 更新用户余额
     *
     * @param userId 用户ID
     * @param amount 变动金额（正数为增加，负数为减少）
     * @return 更新结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateBalance(Long userId, BigDecimal amount) {
        if (userId == null || amount == null) {
            log.error("更新用户余额参数错误: userId={}, amount={}", userId, amount);
            return false;
        }

        // 查询用户
        User user = this.getById(userId);
        if (user == null) {
            log.error("用户不存在: userId={}", userId);
            return false;
        }

        // 计算新余额
        BigDecimal currentBalance = user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO;
        BigDecimal newBalance = currentBalance.add(amount);

        // 如果是扣减余额，需要检查余额是否足够
        if (amount.compareTo(BigDecimal.ZERO) < 0 && newBalance.compareTo(BigDecimal.ZERO) < 0) {
            log.error("用户余额不足: userId={}, currentBalance={}, amount={}", userId, currentBalance, amount);
            return false;
        }

        // 更新用户余额
        user.setBalance(newBalance);
        user.setUpdateTime(LocalDateTime.now());

        boolean result = this.updateById(user);
        log.info("更新用户余额: userId={}, 原余额={}, 变动金额={}, 新余额={}, 结果={}",
                 userId, currentBalance, amount, newBalance, result);

        return result;
    }

    @Override
    public BigDecimal deductBalance(BigDecimal amount) {
        return deductBalance(amount, LoginHelper.getUserId());
    }

    @Override
    public BigDecimal deductBalance(BigDecimal amount, Long userId) {
        User user = this.getById(userId);
        BigDecimal currentBalance = user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO;
        BigDecimal newBalance = currentBalance.subtract(amount);
        user.setBalance(newBalance);
        this.updateById(user);
        log.info("更新用户余额: userId={}, 原余额={}, 变动金额={}, 新余额={}, 结果={}",
            user.getId(), currentBalance, amount, newBalance);
        return newBalance;
    }
}
