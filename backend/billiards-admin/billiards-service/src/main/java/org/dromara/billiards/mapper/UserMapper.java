package org.dromara.billiards.mapper;

import org.dromara.billiards.domain.entity.BlsUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper extends BaseMapper<BlsUser> {

    /**
     * 根据手机号查询用户
     */
    BlsUser selectByPhone(@Param("phone") String phone);

    /**
     * 根据OpenID查询用户
     */
    BlsUser selectByOpenid(@Param("openid") String openid);
}
