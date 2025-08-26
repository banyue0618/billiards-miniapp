package org.dromara.billiards.domain.bo;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 价格计算请求DTO
 */
@Data
public class PriceCalculateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 价格规则ID
     */
    @NotBlank(message = "价格规则ID不能为空")
    private String priceRuleId;

    /**
     * 开始时间
     */
    @NotBlank(message = "开始时间不能为空")
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 是否为会员
     */
    private Boolean isMember = false;
}
