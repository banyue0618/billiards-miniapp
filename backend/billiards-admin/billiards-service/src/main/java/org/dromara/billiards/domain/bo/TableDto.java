package org.dromara.billiards.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

@Data
@Schema(description = "桌台请求对象 (用于新增和修改)")
public class TableDto {

    @Schema(description = "所属门店ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "门店ID不能为空 (仅在新增时严格校验，更新时如果提供则校验)")
    private String storeId;

    private String tableNumber;

    @Schema(description = "桌台编号前缀", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "桌台编号前缀不能为空")
    private String tablePrefix;

    @Schema(description = "桌台编号数字", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "桌台编号数字不能为空")
    @Min(value = 1, message = "桌台编号数字必须大于等于1")
    private Integer tableNumeric;

    @Schema(description = "桌台类型 1-普通 2-专业 3-大师", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "桌台类型不能为空 (仅在新增时严格校验，更新时如果提供则校验)")
    private Integer tableType;

    @Schema(description = "桌台描述")
    private String description;

    @Schema(description = "桌台图片URL")
    private String image;

    @Schema(description = "计费规则ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "计费规则ID不能为空 (仅在新增时严格校验，更新时如果提供则校验)")
    private String priceRuleId;

    // 注意：status 字段通常通过专门接口更新，或在Service层创建时设置默认值。
    // id 字段在新增时由系统生成，在更新时通过路径参数传递。
    // qrcodeUrl, createTime, updateTime 等字段由系统生成或管理。
}
