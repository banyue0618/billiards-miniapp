package org.dromara.billiards.domain.bo;

import org.dromara.billiards.domain.entity.BlsPointsRule;
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
 * 积分规则业务对象 bls_points_rule
 *
 * @author banyue
 * @date 2025-09-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = BlsPointsRule.class, reverseConvertGenerate = false)
public class BlsPointsRuleBo extends BaseEntity {

    /**
     * 规则ID
     */
    @NotBlank(message = "规则ID不能为空", groups = { EditGroup.class })
    private String id;

    /**
     * 规则名称
     */
    @NotBlank(message = "规则名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String name;

    /**
     * 规则类型
     */
    @NotNull(message = "规则类型不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long type;

    /**
     * 积分场景
     */
    @NotNull(message = "积分场景不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long scene;

    /**
     * 积分值类型
     */
    @NotNull(message = "积分值类型不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long valueType;

    /**
     * 积分值
     */
    @NotNull(message = "积分值不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long pointsValue;

    /**
     * 封顶积分值（0表示不封顶）
     */
    @NotNull(message = "封顶积分值（0表示不封顶）不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long maxPoints;

    /**
     * 规则配置（JSON格式，存储具体规则）
     */
    private String ruleConfig;

    /**
     * 等级加成配置（JSON格式，存储各等级的加成比例）
     */
    private String levelBonus;

    /**
     * 时段加成配置（JSON格式，存储特殊时段的加成比例）
     */
    private String timeBonus;

    /**
     * 生效时间
     */
    private LocalDateTime effectiveTime;

    /**
     * 失效时间
     */
    private LocalDateTime expireTime;

    /**
     * 规则描述
     */
    private String description;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 是否参与活动加成
     */
    private Integer enableActivityBonus;

    /**
     * 积分有效期（天）：0表示永久有效
     */
    private Integer validityDays;


}
