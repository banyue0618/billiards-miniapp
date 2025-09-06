package org.dromara.billiards.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

import java.io.Serial;

/**
 * 桌台使用记录对象 bls_table_usage
 *
 * @author banyue
 * @date 2025-09-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("bls_table_usage")
public class BlsTableUsage extends BlsTenantMchEntity {

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
     * 门店ID
     */
    private String storeId;

    /**
     * 桌台ID
     */
    private String tableId;

    /**
     * 订单ID
     */
    private String orderId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 开始时间
     */
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
