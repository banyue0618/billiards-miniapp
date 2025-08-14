package org.dromara.billiards.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.common.file.annotation.FilePreviewUrl;
import org.dromara.common.file.enums.ResourceType;

import java.time.LocalDateTime;

@Data
@Schema(description = "桌台视图对象 (小程序端)")
public class TableVO {

    @Schema(description = "桌台ID")
    private String id;

    @Schema(description = "所属门店ID")
    private String storeId;

    @Schema(description = "桌台编号")
    private String tableNumber;

    @Schema(description = "桌台编号前缀")
    private String tablePrefix;

    @Schema(description = "桌台编号数字部分")
    private Integer tableNumeric;

    @Schema(description = "桌台类型 1-普通 2-专业 3-大师")
    private Integer tableType;

    @Schema(description = "桌台描述")
    private String description;

    @Schema(description = "桌台图片URL")
    private String image;

    @Schema(description = "二维码资源ID")
    private String qrcodeUrl;

    @Schema(description = "二维码预览URL")
    @FilePreviewUrl(path = "qrcodeUrl", resourceType = ResourceType.QRCODE)
    private String qrcodePreviewUrl;

    @Schema(description = "计费规则ID")
    private String priceRuleId;

    @Schema(description = "状态 0-空闲 1-使用中 2-维修中 3-锁定")
    private Integer status;

     @Schema(description = "创建时间")
     @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
     private LocalDateTime createTime;

     @Schema(description = "更新时间")
     @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
     private LocalDateTime updateTime;

     private PriceRuleVO priceRule;
}
