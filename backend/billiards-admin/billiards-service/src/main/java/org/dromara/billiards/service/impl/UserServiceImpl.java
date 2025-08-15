package org.dromara.billiards.service.impl;

import org.dromara.billiards.common.constant.BilliardsConstants;
import org.dromara.billiards.common.exception.BilliardsException;
import org.dromara.billiards.common.result.ResultCode;
import org.dromara.billiards.domain.entity.BlsWalletAccount;
import org.dromara.billiards.mapper.UserMapper;
import org.dromara.billiards.domain.entity.User;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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

    private final IBlsWalletAccountService walletAccountService;

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
        BlsWalletAccount walletAccount = walletAccountService.getWalletAccountByUserId(LoginHelper.getUserId());

        // 检查余额是否大于0
        return walletAccount.getBalance().compareTo(BigDecimal.ZERO) > 0;
    }
}
