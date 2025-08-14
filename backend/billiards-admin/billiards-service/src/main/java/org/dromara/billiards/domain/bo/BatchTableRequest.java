package org.dromara.billiards.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@Schema(description = "批量创建桌台请求对象")
public class BatchTableRequest {

    @Schema(description = "所属门店ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "门店ID不能为空")
    private String storeId;

    @Schema(description = "桌台编号前缀")
    private String tablePrefix;

    @Schema(description = "起始编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "起始编号不能为空")
    @Min(value = 1, message = "起始编号必须大于等于1")
    private Integer startNumeric;

    @Schema(description = "创建数量", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "创建数量不能为空")
    @Min(value = 1, message = "创建数量必须大于等于1")
    @Max(value = 50, message = "一次最多创建50个桌台")
    private Integer count;

    @Schema(description = "桌台类型 1-普通 2-专业 3-大师", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "桌台类型不能为空")
    private Integer tableType;

    @Schema(description = "计费规则ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "计费规则ID不能为空")
    private String priceRuleId;
}
