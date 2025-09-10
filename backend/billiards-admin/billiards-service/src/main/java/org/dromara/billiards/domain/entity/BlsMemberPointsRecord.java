package org.dromara.billiards.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

import java.io.Serial;

/**
 * 会员积分记录对象 bls_member_points_record
 *
 * @author banyue
 * @date 2025-06-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("bls_member_points_record")
public class BlsMemberPointsRecord extends BilliardsBaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    @TableId(value = "id")
    private String id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 积分数量（正数表示获取，负数表示消耗）
     */
    private Long points;

    /**
     * 类型：1-获取 2-消耗
     */
    private Long type;

    /**
     * 场景，与积分规则表场景对应
     */
    private Long scene;

    /**
     * 对应的规则ID
     */
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
