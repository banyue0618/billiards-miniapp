package org.dromara.billiards.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 计费规则实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("bls_price_rule")
public class BlsPriceRule extends BilliardsBaseEntity {

    /**
     * 规则ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 所属商家ID
     */
    private String merchantId;

    /**
     * 规则名称
     */
    private String name;

    /**
     * 规则类型 1-标准计费 2-阶梯计费
     */
    private Integer ruleType;

    /**
     * 单价(元/分钟)
     */
    private BigDecimal priceUnit;

    /**
     * 会员价格(元/分钟)
     */
    private BigDecimal memberPrice;

    /**
     * 最低消费时长(分钟)
     */
    private Integer minMinutes;

    /**
     * 封顶价格
     */
    private BigDecimal maxPrice;

    /**
     * 阶梯计费规则JSON
     */
    private String ladderRules;

    /**
     * 会员折扣
     */
    private BigDecimal memberDiscount;

    /**
     * 状态 0-启用 1-停用
     */
    private Integer status;
}
