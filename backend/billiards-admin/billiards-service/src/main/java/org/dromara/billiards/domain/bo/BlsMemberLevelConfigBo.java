package org.dromara.billiards.domain.bo;

import org.dromara.billiards.domain.entity.BlsMemberLevelConfig;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * 会员等级配置业务对象 bls_member_level_config
 *
 * @author banyue
 * @date 2025-06-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = BlsMemberLevelConfig.class, reverseConvertGenerate = false)
public class BlsMemberLevelConfigBo extends BaseEntity {

    /**
     * 配置ID
     */
    @NotBlank(message = "配置ID不能为空", groups = { EditGroup.class })
    private String id;

    /**
     * 等级编码
     */
    @NotNull(message = "等级编码不能为空", groups = { AddGroup.class, EditGroup.class })
    private String levelCode;

    /**
     * 等级名称
     */
    private String levelName;

    /**
     * 所需累计消费金额
     */
    @NotNull(message = "所需累计消费金额不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal requiredAmount;

    /**
     * 折扣率
     */
    @NotNull(message = "折扣率不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal discount;

    /**
     * 每月赠送时长（分钟）
     */
    @NotNull(message = "每月赠送时长（分钟）不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long monthlyFreeMinutes;

    /**
     * 积分获取倍率
     */
    @NotNull(message = "积分获取倍率不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal pointsMultiplier;

    /**
     * 生日特权折扣率
     */
    private BigDecimal birthdayDiscount;

    /**
     * 可带朋友享受会员价的人数
     */
    private Long friendPrivilegeCount;

    /**
     * 专属客服服务 0-否 1-是
     */
//    @NotNull(message = "专属客服服务 0-否 1-是不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long vipService;

    /**
     * 预约特权 0-否 1-是
     */
//    @NotNull(message = "预约特权 0-否 1-是不能为空", groups = { AddGroup.class, EditGroup.class })
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
