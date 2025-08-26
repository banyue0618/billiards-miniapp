package org.dromara.billiards.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("bls_user")
public class User extends BilliardsBaseEntity {

    /**
     * 用户ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 微信OpenID
     */
    private String openid;

    /**
     * 微信UnionID
     */
    private String unionid;

    /**
     * 用户昵称（来自于微信）
     */
    private String nickname;

    /**
     * 用户名，在本系统设置的用户名
     */
    private String userName;

    /**
     * 用户头像URL
     */
    private String avatarUrl;

    /**
     * 性别 0-未知 1-男 2-女
     */
    private Integer gender;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 是否是会员 0-否 1-是
     */
    private Integer isMember;

    /**
     * 会员等级
     */
    private Integer memberLevel;

    /**
     * 会员过期时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime memberExpire;

    /**
     * 积分
     */
    private Integer points;

    /**
     * 余额
     */
    private BigDecimal balance;

    /**
     * 最后登录时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLoginTime;

    /**
     * 状态 0-正常 1-禁用
     */
    private Integer status;
}
