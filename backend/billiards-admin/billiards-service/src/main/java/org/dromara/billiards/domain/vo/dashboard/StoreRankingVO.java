package org.dromara.billiards.domain.vo.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 门店营收排行数据VO
 */
@Data
@Schema(description = "门店营收排行数据")
public class StoreRankingVO {

    @Schema(description = "门店ID")
    private Long storeId;

    @Schema(description = "门店名称")
    private String storeName;

    @Schema(description = "营收金额")
    private BigDecimal revenue;

    @Schema(description = "同比增长率(%)")
    private BigDecimal growth;

    @Schema(description = "订单数")
    private Integer orderCount;
}
