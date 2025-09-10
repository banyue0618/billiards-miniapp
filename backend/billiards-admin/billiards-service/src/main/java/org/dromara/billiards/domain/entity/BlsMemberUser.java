package org.dromara.billiards.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import java.io.Serial;

/**
 * 会员用户对象 bls_member_user
 *
 * @author banyue
 * @date 2025-06-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("bls_member_user")
public class BlsMemberUser extends BilliardsBaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 会员ID
     */
    @TableId(value = "id")
    private String id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 当前等级编码
     */
    private String levelCode;

    /**
     * 累计消费金额
     */
    private BigDecimal totalAmount;

    /**
     * 当前积分
     */
    private Long points;

    /**
     * 本月已使用免费时长（分钟）
     */
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
    private Long status;


}
