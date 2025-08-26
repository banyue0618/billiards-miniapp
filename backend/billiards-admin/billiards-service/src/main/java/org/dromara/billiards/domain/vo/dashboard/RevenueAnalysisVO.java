package org.dromara.billiards.domain.vo.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 营收分析数据VO
 */
@Data
@Schema(description = "营收分析数据")
public class RevenueAnalysisVO {

    @Schema(description = "当期营收")
    private BigDecimal currentRevenue;

    @Schema(description = "同期营收")
    private BigDecimal previousRevenue;

    @Schema(description = "同比增长率")
    private BigDecimal yearOverYearGrowth;

    @Schema(description = "环比增长率")
    private BigDecimal monthOverMonthGrowth;

    @Schema(description = "预测下期营收")
    private BigDecimal forecastRevenue;

    @Schema(description = "预测增长率")
    private BigDecimal forecastGrowthRate;

    @Schema(description = "营收趋势评价")
    private String trendEvaluation;

    @Schema(description = "改进建议")
    private String improvements;
}
