package org.dromara.billiards.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.common.file.annotation.FilePreviewUrl;
import org.dromara.common.file.enums.ResourceType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "门店视图对象")
public class StoreVO {

    @Schema(description = "门店ID")
    private String id;

    @Schema(description = "门店名称")
    private String name;

    @Schema(description = "门店封面图URL")
    private String coverImage;

    @FilePreviewUrl(path = "coverImage", resourceType = ResourceType.STORE_COVER)
    private String coverImagePreviewUrl;

    @Schema(description = "省份")
    private String province;

    @Schema(description = "城市")
    private String city;

    @Schema(description = "区县")
    private String district;

    @Schema(description = "详细地址")
    private String address;

    @Schema(description = "纬度坐标")
    private BigDecimal latitude;

    @Schema(description = "经度坐标")
    private BigDecimal longitude;

    @Schema(description = "联系电话")
    private String contactPhone;

    @Schema(description = "营业时间")
    private String businessHours;

    @Schema(description = "公告")
    private String announcement;

    @Schema(description = "状态 0-正常 1-休息 2-停业")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "桌台数量")
    private Long tableCount;

    // 如果 Store 实体继承了 BaseEntity，且 BaseEntity 中有 createBy, updateBy 等字段，也可以在这里添加
    // @Schema(description = "创建者")
    // private String createBy;

    // @Schema(description = "更新者")
    // private String updateBy;
}
