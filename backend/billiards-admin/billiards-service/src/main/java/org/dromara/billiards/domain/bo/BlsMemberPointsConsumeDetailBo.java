package org.dromara.billiards.domain.bo;

import org.dromara.billiards.domain.entity.BlsMemberPointsConsumeDetail;
import org.dromara.billiards.domain.entity.BlsTenantMchEntity;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 会员积分消费详情业务对象 bls_member_points_consume_detail
 *
 * @author banyue
 * @date 2025-09-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = BlsMemberPointsConsumeDetail.class, reverseConvertGenerate = false)
public class BlsMemberPointsConsumeDetailBo extends BlsTenantMchEntity {

    /**
     * 记录ID
     */
    @NotBlank(message = "记录ID不能为空", groups = { EditGroup.class })
    private String id;


    private Long userId;

    private String recordId;

    private String validityId;

    private Long points;
}
