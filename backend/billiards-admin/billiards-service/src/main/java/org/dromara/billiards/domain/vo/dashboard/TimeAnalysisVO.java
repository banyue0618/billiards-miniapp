package org.dromara.billiards.domain.vo.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 时段分析数据VO
 */
@Data
@Schema(description = "时段分析数据")
public class TimeAnalysisVO {

    @Schema(description = "时段(格式:HH:mm-HH:mm)")
    private String timeSlot;

    @Schema(description = "客流量")
    private Integer customerFlow;

    @Schema(description = "营收额")
    private BigDecimal revenue;

    @Schema(description = "台球桌使用率")
    private BigDecimal tableUsageRate;

    @Schema(description = "平均停留时长(分钟)")
    private Integer averageStayDuration;

    @Schema(description = "高峰时段标记(1:是 0:否)")
    private Integer isPeakTime;

    @Schema(description = "同比变化率")
    private BigDecimal yearOverYearChange;

    @Schema(description = "环比变化率")
    private BigDecimal monthOverMonthChange;

    @Schema(description = "时段特征分析")
    private String timeSlotAnalysis;

    @Schema(description = "优化建议")
    private String optimizationSuggestions;
}
