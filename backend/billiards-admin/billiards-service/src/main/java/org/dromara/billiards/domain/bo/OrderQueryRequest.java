package org.dromara.billiards.domain.bo;

import lombok.Data;
import lombok.experimental.Accessors;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 订单查询请求DTO
 */
@Data
@Accessors(chain = true)
public class OrderQueryRequest extends PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 门店ID
     */
    private String storeId;

    /**
     * 状态 0-进行中 1-已完成 2-已取消
     */
    private Integer status;

    /**
     * 开始时间（查询范围）
     */
    private LocalDateTime startTime;

    /**
     * 结束时间（查询范围）
     */
    private LocalDateTime endTime;
}
