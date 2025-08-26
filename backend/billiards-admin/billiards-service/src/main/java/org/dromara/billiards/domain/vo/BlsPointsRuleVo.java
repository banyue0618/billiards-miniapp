package org.dromara.billiards.domain.vo;

import java.util.Date;

import org.dromara.billiards.domain.entity.BlsPointsRule;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;


/**
 * 积分规则视图对象 bls_points_rule
 *
 * @author banyue
 * @date 2025-06-17
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = BlsPointsRule.class)
public class BlsPointsRuleVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 规则ID
     */
    @ExcelProperty(value = "规则ID")
    private String id;

    /**
     * 规则名称
     */
    @ExcelProperty(value = "规则名称")
    private String name;

    /**
     * 规则类型：1-获取 2-消耗
     */
    @ExcelProperty(value = "规则类型：1-获取 2-消耗")
    private Long type;

    /**
     * 积分场景：获取场景：1-消费 2-签到 3-活动 4-评价 5-首次绑定 6-邀请好友；消耗场景：1-抵扣 2-兑换商品 3-兑换优惠券
     */
    @ExcelProperty(value = "积分场景：获取场景：1-消费 2-签到 3-活动 4-评价 5-首次绑定 6-邀请好友；消耗场景：1-抵扣 2-兑换商品 3-兑换优惠券")
    private Long scene;

    /**
     * 积分值类型：1-固定值 2-比例值
     */
    @ExcelProperty(value = "积分值类型：1-固定值 2-比例值")
    private Long valueType;

    /**
     * 积分值
     */
    @ExcelProperty(value = "积分值")
    private Long pointsValue;

    /**
     * 封顶积分值（0表示不封顶）
     */
    @ExcelProperty(value = "封顶积分值", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "0=表示不封顶")
    private Long maxPoints;

    /**
     * 规则配置（JSON格式，存储具体规则）
     */
    @ExcelProperty(value = "规则配置", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "J=SON格式，存储具体规则")
    private String ruleConfig;

    /**
     * 等级加成配置（JSON格式，存储各等级的加成比例）
     */
    @ExcelProperty(value = "等级加成配置", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "J=SON格式，存储各等级的加成比例")
    private String levelBonus;

    /**
     * 时段加成配置（JSON格式，存储特殊时段的加成比例）
     */
    @ExcelProperty(value = "时段加成配置", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "J=SON格式，存储特殊时段的加成比例")
    private String timeBonus;

    /**
     * 生效时间
     */
    @ExcelProperty(value = "生效时间")
    private Date effectiveTime;

    /**
     * 失效时间
     */
    @ExcelProperty(value = "失效时间")
    private Date expireTime;

    /**
     * 规则描述
     */
    @ExcelProperty(value = "规则描述")
    private String description;

    /**
     * 状态：0-启用 1-禁用
     */
    @ExcelProperty(value = "状态：0-启用 1-禁用")
    private Integer status;

    /**
     * 是否参与活动加成：0-否 1-是
     */
    @ExcelProperty(value = "是否参与活动加成：0-否 1-是")
    private Integer enableActivityBonus;

    /**
     * 积分有效期（天）：0表示永久有效
     */
    @ExcelProperty(value = "积分有效期", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "天=")
    private Integer validityDays;


}
