package org.dromara.billiards.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 附近门店视图对象
 */
@Data
@Schema(description = "附近门店视图对象，对齐前端StoreDetail")
public class NearbyStoreVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "门店ID")
    private String id;

    @Schema(description = "门店名称")
    private String name;

    @Schema(description = "门店封面图预览URL")
    private String coverImage;

    @Schema(description = "门店图片预览URL列表")
    private List<String> images;

    @Schema(description = "营业状态文字描述, e.g., '营业中', '休息中'")
    private String statusText;

    @Schema(description = "营业状态原码值, e.g., '0:营业中', '1:休息中', '2:已停业'")
    private String status;

    @Schema(description = "营业时间, e.g., '09:00-23:00' or '24小时营业'")
    private String businessHours;

    @Schema(description = "地址信息")
    private AddressVO address;

    @Schema(description = "桌台信息")
    private TablesInfoVO tables;

    @Schema(description = "最低价格/小时，单位：分")
    private Integer minPrice;

    @Schema(description = "联系电话")
    private String contactPhone;

    @Schema(description = "公告信息")
    private AnnouncementVO announcement;

    @Schema(description = "用户到门店的距离，单位：米")
    private Double distance;

    @Schema(description = "门店价格列表")
    private List<PriceVO> priceList;

    @Data
    @Schema(description = "地址详细信息")
    public static class AddressVO implements Serializable {
        private static final long serialVersionUID = 1L;
        @Schema(description = "省份")
        private String province;
        @Schema(description = "城市")
        private String city;
        @Schema(description = "区县")
        private String district;
        @Schema(description = "街道详细地址")
        private String street;
        @Schema(description = "纬度")
        private Double latitude;
        @Schema(description = "经度")
        private Double longitude;
    }

    @Data
    @Schema(description = "桌台统计信息")
    public static class TablesInfoVO implements Serializable {
        private static final long serialVersionUID = 1L;
        @Schema(description = "总桌台数")
        private Integer total;
        @Schema(description = "可用桌台数")
        private Integer available;
    }

    @Data
    @Schema(description = "公告详情")
    public static class AnnouncementVO implements Serializable {
        private static final long serialVersionUID = 1L;
        @Schema(description = "公告内容")
        private String content;
        @Schema(description = "公告更新时间, yyyy-MM-dd HH:mm:ss")
        private String updateTime;
    }

    @Data
    @Schema(description = "价格列表")
    public static class PriceVO implements Serializable {
        private static final long serialVersionUID = 1L;
        @Schema(description = "桌台类型")
        private String type;
        @Schema(description = "价格，单位：分")
        private BigDecimal price;
        @Schema(description = "会员折扣")
        private BigDecimal memberDiscount;
    }
}
