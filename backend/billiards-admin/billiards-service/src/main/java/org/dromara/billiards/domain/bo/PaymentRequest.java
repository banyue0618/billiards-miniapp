package org.dromara.billiards.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 支付请求DTO
 *
 * @author zhangsip
 * @date 2024-07-02
 */
@Data
@Schema(description = "支付请求参数")
public class PaymentRequest {

    /**
     * 充值金额（单位：元）
     */
    @NotNull(message = "充值金额不能为空")
    @Min(value = 1, message = "充值金额必须大于0")
    @Schema(description = "充值金额（单位：元）", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal amount;

    /**
     * 门店id
     */
    @NotNull(message = "门店不能为空")
    @Schema(description = "所属门店", requiredMode = Schema.RequiredMode.REQUIRED)
    private String storeId;
}
