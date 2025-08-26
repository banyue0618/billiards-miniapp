package org.dromara.billiards.service;

import org.dromara.billiards.domain.bo.UserUpdateDto;
import org.dromara.billiards.domain.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {

    /**
     * 根据手机号获取用户
     * @param phone 手机号
     * @return 用户对象
     */
    User getByPhone(String phone);

    /**
     * 根据OpenID获取用户
     * @param openid 微信OpenID
     * @return 用户对象
     */
    User getByOpenid(String openid);

    /**
     * 更新当前登录用户的基本信息 (如昵称、头像、性别)
     * @param dto 包含要更新信息的数据传输对象
     * @return 是否成功
     */
    boolean updateCurrentUserInfo(UserUpdateDto dto);

    /**
     * 绑定用户手机号
     * @param phone 手机号
     * @return 是否成功
     */
    boolean bindUserPhone(String phone);

    /**
     * 获取用户详细信息，若不存在则抛出异常
     * @return 用户实体
     */
    User getUserInfoById();

    /**
     * 获取用户详细信息，若不存在则抛出异常
     * @return 用户实体
     */
    User getUserInfoById(Long userId);

    /**
     * 检查用户余额是否足够,足够返回true,否则返回false
     * @return
     */
    boolean scanTableEnableCheck();

}
