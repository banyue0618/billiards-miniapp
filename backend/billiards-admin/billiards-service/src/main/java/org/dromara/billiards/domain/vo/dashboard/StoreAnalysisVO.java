package org.dromara.billiards.domain.vo.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 门店分析数据VO
 */
@Data
@Schema(description = "门店分析数据")
public class StoreAnalysisVO {

    @Schema(description = "门店ID")
    private Long storeId;

    @Schema(description = "门店名称")
    private String storeName;

    @Schema(description = "当期营收")
    private BigDecimal currentRevenue;

    @Schema(description = "营收排名")
    private Integer revenueRank;

    @Schema(description = "同比增长率")
    private BigDecimal yearOverYearGrowth;

    @Schema(description = "环比增长率")
    private BigDecimal monthOverMonthGrowth;

    @Schema(description = "客流量")
    private Integer customerFlow;

    @Schema(description = "客流量排名")
    private Integer customerFlowRank;

    @Schema(description = "平均消费")
    private BigDecimal averageConsumption;

    @Schema(description = "运营评分")
    private Integer operationScore;

    @Schema(description = "改进建议")
    private String improvements;
}
