package org.dromara.billiards.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 预约桌台列表视图对象
 */
@Data
@Schema(description = "预约桌台列表项")
public class ReservationTableVO {

    @Schema(description = "桌台ID")
    private String id;

    @Schema(description = "桌台名称")
    private String name;

    @Schema(description = "状态文本")
    private String statusText;

    @Schema(description = "桌台描述")
    private String description;

    @Schema(description = "桌台图片URL")
    private String image;

    @Schema(description = "标签列表")
    private List<TableTagVO> tags;

    @Schema(description = "时间段列表")
    private List<TimeSlotVO> slots;
}

