package org.dromara.billiards.domain.bo;

import org.dromara.billiards.domain.entity.BlsMemberPointsValidity;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;
import java.util.Date;

/**
 * 积分有效期业务对象 bls_member_points_validity
 *
 * @author banyue
 * @date 2025-06-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = BlsMemberPointsValidity.class, reverseConvertGenerate = false)
public class BlsMemberPointsValidityBo extends BaseEntity {

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
     * 积分数量
     */
    @NotNull(message = "积分数量不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long points;

    /**
     * 剩余积分数量
     */
    @NotNull(message = "剩余积分数量不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long remainingPoints;

    /**
     * 过期时间
     */
    @NotNull(message = "过期时间不能为空", groups = { AddGroup.class, EditGroup.class })
    private Date expireTime;


}
