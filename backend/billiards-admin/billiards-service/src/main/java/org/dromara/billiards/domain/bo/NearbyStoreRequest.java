package org.dromara.billiards.domain.bo;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 附近门店查询请求DTO
 */
@Data
public class NearbyStoreRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String storeId;

    /**
     * 纬度
     */
    @NotNull(message = "纬度不能为空")
    private BigDecimal latitude;

    /**
     * 经度
     */
    @NotNull(message = "经度不能为空")
    private BigDecimal longitude;

    /**
     * 搜索半径(公里)
     */
    private Integer radius = 5;
}
