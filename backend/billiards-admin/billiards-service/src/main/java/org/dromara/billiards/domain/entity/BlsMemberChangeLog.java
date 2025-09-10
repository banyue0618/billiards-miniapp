package org.dromara.billiards.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

import java.io.Serial;

/**
 * 会员变更记录对象 bls_member_change_log
 *
 * @author banyue
 * @date 2025-09-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("bls_member_change_log")
public class BlsMemberChangeLog extends BlsTenantMchEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    @TableId(value = "id")
    private String id;

    /**
     * 商家ID
     */
    private String merchantId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 变更类型 NEW/RENEWAL/UPGRADE/EXPIRED
     */
    private String changeType;

    /**
     * 变更前等级
     */
    private String beforeLevel;

    /**
     * 变更后等级
     */
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
