package org.dromara.billiards.domain.bo;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 桌台查询请求DTO
 */
@Data
public class TableQueryRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 页码
     */
    private Integer pageNumber = 1;

    /**
     * 每页数量
     */
    private Integer pageSize = 10;

    /**
     * 门店ID
     */
    @NotBlank(message = "门店ID不能为空")
    private String storeId;

    /**
     * 桌台状态 0-空闲 1-使用中 2-维修中 3-锁定
     */
    private Integer status;

    /**
     * 桌台类型 1-普通 2-专业 3-大师
     */
    private String tableType;

    /**
     * 桌台编号
     */
    private String tableNumber;
}
