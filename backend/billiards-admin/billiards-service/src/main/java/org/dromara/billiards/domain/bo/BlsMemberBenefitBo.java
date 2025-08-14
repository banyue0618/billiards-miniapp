package org.dromara.billiards.domain.bo;

import org.dromara.billiards.domain.entity.BlsMemberBenefit;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;
import java.util.Date;

/**
 * 会员权益业务对象 bls_member_benefit
 *
 * @author banyue
 * @date 2025-06-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = BlsMemberBenefit.class, reverseConvertGenerate = false)
public class BlsMemberBenefitBo extends BaseEntity {

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
     * 权益类型：1-折扣 2-赠送 3-积分 4-特权
     */
    @NotNull(message = "权益类型：1-折扣 2-赠送 3-积分 4-特权不能为空", groups = { AddGroup.class, EditGroup.class })
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
    private Date effectiveTime;

    /**
     * 失效时间
     */
    private Date expireTime;

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
     * 状态：0-启用 1-禁用
     */
    @NotNull(message = "状态：0-启用 1-禁用不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long status;

    /**
     * 排序号
     */
    @NotNull(message = "排序号不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long sortOrder;

    /**
     * 是否限时权益：0-永久 1-限时
     */
    @NotNull(message = "是否限时权益：0-永久 1-限时不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer isLimited;

    /**
     * 是否节日特权：0-否 1-是
     */
    @NotNull(message = "是否节日特权：0-否 1-是不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer isHoliday;

    /**
     * 权益标签，多个用逗号分隔
     */
    private String tags;


}
