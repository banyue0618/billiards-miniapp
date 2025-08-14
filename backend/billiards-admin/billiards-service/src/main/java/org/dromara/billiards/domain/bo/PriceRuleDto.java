package org.dromara.billiards.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;

import java.math.BigDecimal;

@Data
@Schema(description = "计费规则数据传输对象")
public class PriceRuleDto {

    @Schema(description = "规则ID，更新时使用")
    @NotBlank(message = "ID不能为空", groups = EditGroup.class)
    private String id;

    // storeId can be optional depending on whether a rule is global to merchant or specific to store
    @Schema(description = "所属门店ID (可选，如果规则特定于门店)")
    private String storeId;

    @NotBlank(message = "规则名称不能为空", groups = {AddGroup.class, EditGroup.class})
    @Schema(description = "规则名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @NotNull(message = "规则类型不能为空", groups = {AddGroup.class, EditGroup.class})
    @Min(value = 1, message = "规则类型不合法", groups = {AddGroup.class, EditGroup.class})
    @Max(value = 2, message = "规则类型不合法", groups = {AddGroup.class, EditGroup.class})
    @Schema(description = "规则类型 1-标准计费 2-阶梯计费", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer ruleType;

    @Schema(description = "单价(元/分钟)", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal priceUnit;

    @DecimalMin(value = "0.00", message = "会员价格必须大于等于0", groups = {AddGroup.class, EditGroup.class})
    @Schema(description = "会员价格(元/分钟)")
    private BigDecimal memberPrice;

    @Schema(description = "最低消费时长(分钟)", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer minMinutes;

    @DecimalMin(value = "0.00", message = "封顶价格必须大于等于0", groups = {AddGroup.class, EditGroup.class})
    @Schema(description = "封顶价格")
    private BigDecimal maxPrice;

    @Schema(description = "阶梯计费规则JSON (当ruleType为2时)")
    private String ladderRules; // Consider validating this based on ruleType if it's a JSON string

    @Schema(description = "会员折扣")
    private BigDecimal memberDiscount;

    // Status is usually handled by a separate endpoint or internally by service on create
    // @NotNull(message = "状态不能为空", groups = {AddGroup.class, EditGroup.class})
    // @Schema(description = "状态 0-启用 1-停用")
    // private Integer status;
}
