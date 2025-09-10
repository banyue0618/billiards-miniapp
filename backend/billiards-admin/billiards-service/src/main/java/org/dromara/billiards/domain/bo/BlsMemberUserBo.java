package org.dromara.billiards.domain.bo;

import org.dromara.billiards.domain.entity.BlsMemberUser;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 会员用户业务对象 bls_member_user
 *
 * @author banyue
 * @date 2025-06-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = BlsMemberUser.class, reverseConvertGenerate = false)
public class BlsMemberUserBo extends BaseEntity {

    /**
     * 会员ID
     */
    @NotBlank(message = "会员ID不能为空", groups = { EditGroup.class })
    private String id;

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long userId;

    /**
     * 当前等级编码
     */
    @NotNull(message = "当前等级编码不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long levelCode;

    /**
     * 累计消费金额
     */
    @NotNull(message = "累计消费金额不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal totalAmount;

    /**
     * 当前积分
     */
    @NotNull(message = "当前积分不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long points;

    /**
     * 本月已使用免费时长（分钟）
     */
    @NotNull(message = "本月已使用免费时长（分钟）不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long monthlyUsedMinutes;

    /**
     * 等级有效期
     */
    private LocalDateTime levelExpireTime;

    /**
     * 最近消费时间
     */
    private LocalDateTime lastConsumeTime;

    /**
     * 状态：0-正常 1-禁用
     */
    @NotNull(message = "状态：0-正常 1-禁用不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long status;


}
