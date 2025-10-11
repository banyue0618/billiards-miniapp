package org.dromara.billiards.domain.bo;

import org.dromara.billiards.domain.entity.BlsTableUsage;
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
 * 桌台使用记录业务对象 bls_table_usage
 *
 * @author banyue
 * @date 2025-09-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = BlsTableUsage.class, reverseConvertGenerate = false)
public class BlsTableUsageBo extends BlsTenantMchEntity {

    /**
     * 记录ID
     */
    @NotBlank(message = "记录ID不能为空", groups = { EditGroup.class })
    private String id;

    /**
     * 商家ID
     */
    @NotBlank(message = "商家ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private String merchantId;

    /**
     * 门店ID
     */
    @NotBlank(message = "门店ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private String storeId;

    /**
     * 桌台ID
     */
    @NotBlank(message = "桌台ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private String tableId;

    /**
     * 订单ID
     */
    @NotBlank(message = "订单ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private String orderId;

    /**
     * 用户ID
     */
    @NotBlank(message = "用户ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long userId;

    /**
     * 开始时间
     */
    @NotNull(message = "开始时间不能为空", groups = { AddGroup.class, EditGroup.class })
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 使用时长(分钟)
     */
    private Integer duration;

}
