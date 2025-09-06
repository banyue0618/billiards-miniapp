package org.dromara.billiards.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 计费规则预览结果VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "计费规则预览结果")
public class PriceRulePreviewVO {

    @Schema(description = "计算的费用")
    private BigDecimal fee;

    @Schema(description = "计算详情")
    private List<CalculationDetail> details;

    /**
     * 计算详情
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "计算详情")
    public static class CalculationDetail {

        @Schema(description = "描述信息")
        private String description;
    }
}
