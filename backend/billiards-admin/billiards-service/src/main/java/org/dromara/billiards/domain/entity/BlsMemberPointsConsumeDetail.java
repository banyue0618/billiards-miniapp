package org.dromara.billiards.domain.entity;

import org.dromara.common.tenant.core.TenantEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 会员积分消费详情对象 bls_member_points_consume_detail
 *
 * @author banyue
 * @date 2025-09-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("bls_member_points_consume_detail")
public class BlsMemberPointsConsumeDetail extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    @TableId(value = "id")
    private String id;

    /**
     * 商家ID
     */
    private String merchantId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 指向消费类 bls_member_points_record.id
     */
    private String recordId;

    /**
     * 指向被扣的批次 bls_member_points_validity.id
     */
    private String validityId;

    /**
     * 本行扣减的积分（正数表示扣减量）
     */
    private Long points;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    private Long isDelete;


}
