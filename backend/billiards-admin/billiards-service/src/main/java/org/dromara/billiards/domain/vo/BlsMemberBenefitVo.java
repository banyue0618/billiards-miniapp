package org.dromara.billiards.domain.vo;

import java.time.LocalDateTime;

import org.dromara.billiards.domain.entity.BlsMemberBenefit;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;


/**
 * 会员权益视图对象 bls_member_benefit
 *
 * @author banyue
 * @date 2025-06-17
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = BlsMemberBenefit.class)
public class BlsMemberBenefitVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 权益ID
     */
    @ExcelProperty(value = "权益ID")
    private String id;

    /**
     * 权益名称
     */
    @ExcelProperty(value = "权益名称")
    private String name;

    /**
     * 权益类型：1-折扣 2-赠送 3-积分 4-特权
     */
    @ExcelProperty(value = "权益类型：1-折扣 2-赠送 3-积分 4-特权")
    private Long type;

    /**
     * 适用等级编码，多个用逗号分隔
     */
    @ExcelProperty(value = "适用等级编码，多个用逗号分隔")
    private String applicableLevels;

    /**
     * 权益值（如折扣率、赠送时长、积分倍率等）
     */
    @ExcelProperty(value = "权益值", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "如=折扣率、赠送时长、积分倍率等")
    private String benefitValue;

    /**
     * 权益规则（JSON格式，存储具体规则配置）
     */
    @ExcelProperty(value = "权益规则", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "J=SON格式，存储具体规则配置")
    private String benefitRules;

    /**
     * 生效时间
     */
    @ExcelProperty(value = "生效时间")
    private LocalDateTime effectiveTime;

    /**
     * 失效时间
     */
    @ExcelProperty(value = "失效时间")
    private LocalDateTime expireTime;

    /**
     * 权益图标
     */
    @ExcelProperty(value = "权益图标")
    private String icon;

    /**
     * 权益描述
     */
    @ExcelProperty(value = "权益描述")
    private String description;

    /**
     * 使用说明
     */
    @ExcelProperty(value = "使用说明")
    private String instructions;

    /**
     * 状态：0-启用 1-禁用
     */
    @ExcelProperty(value = "状态：0-启用 1-禁用")
    private Long status;

    /**
     * 排序号
     */
    @ExcelProperty(value = "排序号")
    private Long sortOrder;

    /**
     * 是否限时权益：0-永久 1-限时
     */
    @ExcelProperty(value = "是否限时权益：0-永久 1-限时")
    private Integer isLimited;

    /**
     * 是否节日特权：0-否 1-是
     */
    @ExcelProperty(value = "是否节日特权：0-否 1-是")
    private Integer isHoliday;

    /**
     * 权益标签，多个用逗号分隔
     */
    @ExcelProperty(value = "权益标签，多个用逗号分隔")
    private String tags;


}
