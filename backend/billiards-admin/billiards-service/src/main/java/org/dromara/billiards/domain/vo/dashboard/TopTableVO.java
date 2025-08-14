package org.dromara.billiards.domain.vo.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 热门桌台数据VO
 */
@Data
@Schema(description = "热门桌台数据")
public class TopTableVO {

    @Schema(description = "桌台ID")
    private Long tableId;

    @Schema(description = "桌台编号")
    private String tableNumber;

    @Schema(description = "门店名称")
    private String storeName;

    @Schema(description = "使用次数")
    private Integer usageCount;

    @Schema(description = "使用时长(分钟)")
    private Integer usageTime;

    @Schema(description = "营收金额")
    private BigDecimal revenue;
}
