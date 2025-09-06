package org.dromara.billiards.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 仪表盘概览数据VO
 */
@Data
@Schema(description = "仪表盘概览数据")
public class DashboardOverviewVO {

    @Schema(description = "今日营收")
    private BigDecimal todayRevenue;

    @Schema(description = "昨日营收")
    private BigDecimal yesterdayRevenue;

    @Schema(description = "今日订单数")
    private Integer todayOrderCount;

    @Schema(description = "昨日订单数")
    private Integer yesterdayOrderCount;

    @Schema(description = "今日台球桌使用率")
    private BigDecimal todayTableUsageRate;

    @Schema(description = "昨日台球桌使用率")
    private BigDecimal yesterdayTableUsageRate;

    @Schema(description = "今日新增会员数")
    private Integer todayNewMembers;

    @Schema(description = "昨日新增会员数")
    private Integer yesterdayNewMembers;

    @Schema(description = "总桌台")
    private Integer totalTables;

    @Schema(description = "使用中桌台")
    private Integer activeTables;

    @Schema(description = "平均使用时长")
    private Integer avgUsageTime;

    @Schema(description = "昨日平均使用时长")
    private Integer yesterdayAvgUsageTime;
}
