package org.dromara.billiards.domain.bo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Description
 * @Author zhangsip
 * @Version V1.0.0
 * @Since 1.0
 * @Date 2025/9/4
 */
@Data
public class OrderUpdateDto implements Serializable {

    private BigDecimal amount;

    private String remark;

}
