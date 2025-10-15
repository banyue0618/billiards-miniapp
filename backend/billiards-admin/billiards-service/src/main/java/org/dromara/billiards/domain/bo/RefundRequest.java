package org.dromara.billiards.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 退款请求对象
 *
 * @author banyue
 */
@Data
@Schema(description = "退款请求")
public class RefundRequest {

    @Schema(description = "充值记录ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "充值记录ID不能为空")
    private String payRecordId;
}

