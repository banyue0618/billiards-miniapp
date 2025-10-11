package org.dromara.billiards.domain.bo;

import org.dromara.billiards.domain.entity.BlsMemberPointsRecord;
import org.dromara.billiards.domain.entity.BlsTenantMchEntity;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * 会员积分记录业务对象 bls_member_points_record
 *
 * @author banyue
 * @date 2025-06-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = BlsMemberPointsRecord.class, reverseConvertGenerate = false)
public class BlsMemberPointsRecordBo extends BlsTenantMchEntity {

    /**
     * 记录ID
     */
    @NotBlank(message = "记录ID不能为空", groups = { EditGroup.class })
    private String id;

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long userId;

    /**
     * 积分数量（正数表示获取，负数表示消耗）
     */
    @NotNull(message = "积分数量（正数表示获取，负数表示消耗）不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long points;

    /**
     * 类型：1-获取 2-消耗
     */
    @NotNull(message = "类型不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long type;

    /**
     * 场景，与积分规则表场景对应
     */
    @NotNull(message = "场景，与积分规则表场景对应不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long scene;

    /**
     * 对应的规则ID
     */
    @NotBlank(message = "对应的规则ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private String ruleId;

    /**
     * 关联业务ID（如订单ID、活动ID等）
     */
    private String businessId;

    /**
     * 积分描述
     */
    private String description;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;


}
