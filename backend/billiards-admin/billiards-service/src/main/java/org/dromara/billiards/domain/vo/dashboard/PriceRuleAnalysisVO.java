package org.dromara.billiards.domain.vo.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 计费规则分析数据VO
 */
@Data
@Schema(description = "计费规则分析数据")
public class PriceRuleAnalysisVO {

    @Schema(description = "规则ID")
    private Long ruleId;

    @Schema(description = "规则名称")
    private String ruleName;

    @Schema(description = "使用次数")
    private Integer usageCount;

    @Schema(description = "营收金额")
    private BigDecimal revenue;

    @Schema(description = "营收占比(%)")
    private BigDecimal revenueRatio;
}
