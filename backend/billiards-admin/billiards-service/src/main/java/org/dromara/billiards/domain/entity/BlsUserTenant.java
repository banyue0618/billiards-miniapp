package org.dromara.billiards.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;

import java.io.Serial;

/**
 * 平台用户-租户映射对象 bls_user_tenant
 *
 * @author banyue
 * @date 2025-08-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("bls_user_tenant")
public class BlsUserTenant extends BlsTenantMchEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 平台用户ID
     */
    private Long userId;

    /**
     * 租户内的用户名/昵称
     */
    private String tenantUserName;

    /**
     * 租户内绑定手机号
     */
    private String tenantPhone;

    /**
     * 角色(USER/MEMBER/ADMIN)
     */
    private String role;

    /**
     * 是否在该租户下是会员
     */
    private Long isMember;

    /**
     * 状态 0-正常 1-禁用 2-注销
     */
    private Long status;

    /**
     * 首次登录时间
     */
    private Date firstTime;

    /**
     * 最后登录时间
     */
    private Date lastTime;


}
