package org.dromara.billiards.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;

import java.io.Serial;

/**
 * 积分有效期对象 bls_member_points_validity
 *
 * @author banyue
 * @date 2025-06-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("bls_member_points_validity")
public class BlsMemberPointsValidity extends BilliardsBaseEntity {

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
     * 积分数量
     */
    private Long points;

    /**
     * 剩余积分数量
     */
    private Long remainingPoints;

    /**
     * 过期时间
     */
    private Date expireTime;


}
