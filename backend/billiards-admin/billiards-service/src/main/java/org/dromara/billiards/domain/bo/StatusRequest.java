package org.dromara.billiards.domain.bo;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 状态更新请求DTO
 */
@Data
public class StatusRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @NotBlank(message = "ID不能为空")
    private String id;

    /**
     * 状态
     */
    @NotNull(message = "状态不能为空")
    private Integer status;

    /**
     * 状态描述（如门店公告）
     */
    private String announcement;
}
