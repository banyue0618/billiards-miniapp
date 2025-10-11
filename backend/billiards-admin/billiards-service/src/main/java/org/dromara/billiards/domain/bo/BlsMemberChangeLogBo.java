package org.dromara.billiards.domain.bo;

import org.dromara.billiards.domain.entity.BlsMemberChangeLog;
import org.dromara.billiards.domain.entity.BlsTenantMchEntity;
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
 * 会员变更记录业务对象 bls_member_change_log
 *
 * @author banyue
 * @date 2025-09-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = BlsMemberChangeLog.class, reverseConvertGenerate = false)
public class BlsMemberChangeLogBo extends BlsTenantMchEntity {

    /**
     * 记录ID
     */
    @NotBlank(message = "记录ID不能为空", groups = { EditGroup.class })
    private String id;

    /**
     * 商家ID
     */
    private String merchantId;

    /**
     * 用户ID
     */
    @NotBlank(message = "用户ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long userId;

    /**
     * 变更类型 NEW/RENEWAL/UPGRADE/EXPIRED
     */
    @NotBlank(message = "变更类型 NEW/RENEWAL/UPGRADE/EXPIRED不能为空", groups = { AddGroup.class, EditGroup.class })
    private String changeType;

    /**
     * 变更前等级
     */
    private String beforeLevel;

    /**
     * 变更后等级
     */
    @NotBlank(message = "变更后等级不能为空", groups = { AddGroup.class, EditGroup.class })
    private String afterLevel;

    /**
     * 变更前过期时间
     */
    private LocalDateTime beforeExpire;

    /**
     * 变更后过期时间
     */
    private LocalDateTime afterExpire;

    /**
     * 关联订单ID
     */
    private String orderId;

    /**
     * 备注
     */
    private String remark;


}
