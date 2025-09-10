package org.dromara.billiards.domain.bo;

import org.dromara.billiards.domain.entity.BlsUserTenant;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 平台用户-租户映射业务对象 bls_user_tenant
 *
 * @author banyue
 * @date 2025-08-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = BlsUserTenant.class, reverseConvertGenerate = false)
public class BlsUserTenantBo extends BaseEntity {

    /**
     *
     */
    @NotNull(message = "不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 平台用户ID
     */
    @NotNull(message = "平台用户ID不能为空", groups = { AddGroup.class, EditGroup.class })
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
    @NotNull(message = "状态 0-正常 1-禁用 2-注销不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long status;

    /**
     * 首次登录时间
     */
    @NotNull(message = "首次登录时间不能为空", groups = { AddGroup.class, EditGroup.class })
    private LocalDateTime firstTime;

    /**
     * 最后登录时间
     */
    @NotNull(message = "最后登录时间不能为空", groups = { AddGroup.class, EditGroup.class })
    private LocalDateTime lastTime;


}
