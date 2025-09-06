package org.dromara.billiards.domain.vo.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 营收趋势图 VO：x 轴为时间
 * - orderSeries：订单数（柱状）
 * - revenueSeries：营收金额（折线）
 */
@Data
@Schema(description = "营收趋势图数据")
public class RevenueTrendVO {

    @Schema(description = "X轴时间标签")
    private List<String> xAxis;

    @Schema(description = "订单数序列（柱状）")
    private List<Integer> orderSeries;

    @Schema(description = "营收金额序列（折线）")
    private List<BigDecimal> revenueSeries;
}


