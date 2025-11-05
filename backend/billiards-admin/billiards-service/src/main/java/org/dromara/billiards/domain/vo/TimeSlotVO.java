package org.dromara.billiards.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 预约桌台时间段视图对象
 */
@Data
@Schema(description = "预约桌台时间段")
public class TimeSlotVO {

    @Schema(description = "开始时间 HH:mm")
    private String startTime;

    @Schema(description = "结束时间 HH:mm")
    private String endTime;

    @Schema(description = "状态 available-可预约 blocked-已占用")
    private String status;

    @Schema(description = "标签")
    private String label;
}

