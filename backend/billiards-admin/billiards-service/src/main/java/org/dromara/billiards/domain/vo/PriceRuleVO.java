package org.dromara.billiards.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "计费规则视图对象")
public class PriceRuleVO {

    @Schema(description = "规则ID")
    private String id;

    @Schema(description = "所属门店ID")
    private String storeId;

    @Schema(description = "规则名称")
    private String name;

    @Schema(description = "规则类型 1-标准计费 2-阶梯计费")
    private Integer ruleType;

    @Schema(description = "单价(元/分钟)")
    private BigDecimal priceUnit;

    @Schema(description = "会员价格(元/分钟)")
    private BigDecimal memberPrice;

    @Schema(description = "会员折扣")
    private BigDecimal memberDiscount;

    @Schema(description = "最低消费时长(分钟)")
    private Integer minMinutes;

    @Schema(description = "最低价格")
    private BigDecimal minPrice;

    @Schema(description = "封顶价格")
    private BigDecimal maxPrice;

    @Schema(description = "阶梯计费规则JSON")
    private String ladderRules;

    @Schema(description = "状态 0-启用 1-停用")
    private Integer status;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
