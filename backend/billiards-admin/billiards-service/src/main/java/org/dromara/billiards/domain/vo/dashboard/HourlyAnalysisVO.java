package org.dromara.billiards.domain.vo.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 每日时段分析数据VO
 */
@Data
@Schema(description = "每日时段分析数据")
public class HourlyAnalysisVO {

    @Schema(description = "小时数(0-23)")
    private Integer hour;

    @Schema(description = "订单数")
    private Integer orderCount;

    @Schema(description = "营收额")
    private BigDecimal hourlyRevenue;

    @Schema(description = "小时列表")
    private List<Integer> hours;

    @Schema(description = "订单数列表")
    private List<Integer> orders;

    @Schema(description = "营收额列表")
    private List<BigDecimal> revenueList;
}
