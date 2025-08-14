package org.dromara.billiards.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

/**
 * 仪表盘查询请求DTO
 */
@Data
@Schema(description = "仪表盘查询请求")
public class DashboardQueryRequest {

    @Schema(description = "开始日期")
    @NotNull(message = "开始日期不能为空")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Schema(description = "结束日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Schema(description = "门店ID列表")
    private List<Long> storeIds;

    @Schema(description = "图表数据类型：day-日, week-周, month-月")
    private String chartType = "day";
}
