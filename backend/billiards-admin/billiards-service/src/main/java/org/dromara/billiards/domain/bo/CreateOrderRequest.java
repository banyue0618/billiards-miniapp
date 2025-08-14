package org.dromara.billiards.domain.bo;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 创建订单请求DTO
 */
@Data
public class CreateOrderRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 渠道
     */
    @NotBlank(message = "渠道不能为空")
    private String channel;

    /**
     * 桌台ID
     */
    @NotBlank(message = "桌台ID不能为空")
    private String tableId;
}
