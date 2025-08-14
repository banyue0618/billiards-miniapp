package org.dromara.billiards.domain.bo;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 门店查询请求DTO
 */
@Data
public class StoreQueryRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 页码
     */
    private Integer page = 1;

    /**
     * 每页数量
     */
    private Integer size = 10;

    /**
     * 门店名称
     */
    private String name;

    /**
     * 状态：0-正常 1-休息 2-停业
     */
    private Integer status;

    /**
     * 搜索关键词（用于小程序端搜索）
     */
    private String keyword;

    /**
     * 经度
     */
    private BigDecimal longitude;

    /**
     * 纬度
     */
    private BigDecimal latitude;

    /**
     * 搜索半径(公里)
     */
    private Integer radius = 5;
}
