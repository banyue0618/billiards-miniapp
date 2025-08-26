package org.dromara.billiards.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 计费规则预览DTO
 */
@Data
@Schema(description = "计费规则预览请求参数")
public class PriceRulePreviewDto {

    @Schema(description = "计费规则ID")
    private String ruleId;

    @Schema(description = "使用分钟数")
    private Integer minutes;

    @Schema(description = "是否会员")
    private Boolean isMember;
}
