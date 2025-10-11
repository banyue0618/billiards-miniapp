package org.dromara.billiards.domain.bo;

import org.dromara.billiards.domain.entity.BlsMemberBenefit;
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
 * 会员权益业务对象 bls_member_benefit
 *
 * @author banyue
 * @date 2025-09-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = BlsMemberBenefit.class, reverseConvertGenerate = false)
public class BlsMemberBenefitBo extends BlsTenantMchEntity {

    /**
     * 权益ID
     */
    @NotBlank(message = "权益ID不能为空", groups = { EditGroup.class })
    private String id;

    /**
     * 权益名称
     */
    @NotBlank(message = "权益名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String name;

    /**
     * 权益类型
     */
    @NotNull(message = "权益类型不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long type;

    /**
     * 适用等级编码，多个用逗号分隔
     */
    @NotBlank(message = "适用等级编码，多个用逗号分隔不能为空", groups = { AddGroup.class, EditGroup.class })
    private String applicableLevels;

    /**
     * 权益值（如折扣率、赠送时长、积分倍率等）
     */
    @NotBlank(message = "权益值（如折扣率、赠送时长、积分倍率等）不能为空", groups = { AddGroup.class, EditGroup.class })
    private String benefitValue;

    /**
     * 权益规则（JSON格式，存储具体规则配置）
     */
    private String benefitRules;

    /**
     * 生效时间
     */
    private LocalDateTime effectiveTime;

    /**
     * 失效时间
     */
    private LocalDateTime expireTime;

    /**
     * 权益图标
     */
    private String icon;

    /**
     * 权益描述
     */
    private String description;

    /**
     * 使用说明
     */
    private String instructions;

    /**
     * 状态
     */
    @NotNull(message = "状态不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long status;

    /**
     * 排序号
     */
    private Long sortOrder;

    /**
     * 是否限时
     */
    @NotNull(message = "是否限时不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer isLimited;

    /**
     * 是否节日特权
     */
    @NotNull(message = "是否节日特权不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer isHoliday;

    /**
     * 权益标签
     */
    private String tags;


}
