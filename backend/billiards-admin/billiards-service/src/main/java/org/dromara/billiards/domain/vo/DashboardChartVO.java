package org.dromara.billiards.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 仪表盘图表数据VO
 */
@Data
@Schema(description = "仪表盘图表数据")
public class DashboardChartVO {

    @Schema(description = "时间维度标签列表")
    private List<String> timeLabels;

    @Schema(description = "营收数据列表")
    private List<BigDecimal> revenueData;

    @Schema(description = "订单数据列表")
    private List<Integer> orderData;

    @Schema(description = "台球桌使用率数据列表")
    private List<BigDecimal> tableUsageData;

    @Schema(description = "新增会员数据列表")
    private List<Integer> newMemberData;
}
