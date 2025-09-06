package org.dromara.billiards.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

import java.io.Serial;

/**
 * 会员等级配置对象 bls_member_level_config
 *
 * @author banyue
 * @date 2025-06-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("bls_member_level_config")
public class BlsMemberLevelConfig extends BlsTenantMchEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 配置ID
     */
    @TableId(value = "id")
    private String id;

    /**
     * 等级编码
     */
    private Long levelCode;

    /**
     * 等级名称
     */
    private String levelName;

    /**
     * 所需累计消费金额
     */
    private BigDecimal requiredAmount;

    /**
     * 折扣率
     */
    private Long discount;

    /**
     * 每月赠送时长（分钟）
     */
    private Long monthlyFreeMinutes;

    /**
     * 积分获取倍率
     */
    private Long pointsMultiplier;

    /**
     * 生日特权折扣率
     */
    private Long birthdayDiscount;

    /**
     * 可带朋友享受会员价的人数
     */
    private Long friendPrivilegeCount;

    /**
     * 专属客服服务 0-否 1-是
     */
    private Long vipService;

    /**
     * 预约特权 0-否 1-是
     */
    private Long reservationPrivilege;

    /**
     * 等级图标
     */
    private String levelIcon;

    /**
     * 等级背景图
     */
    private String levelBackground;

    /**
     * 等级描述
     */
    private String description;

    /**
     * 状态 0-启用 1-禁用
     */
    private Integer status;
}
