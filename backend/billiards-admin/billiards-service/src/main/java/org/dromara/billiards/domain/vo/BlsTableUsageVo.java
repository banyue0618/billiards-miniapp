package org.dromara.billiards.domain.vo;

import java.time.LocalDateTime;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import org.dromara.billiards.domain.entity.BlsTableUsage;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;



/**
 * 桌台使用记录视图对象 bls_table_usage
 *
 * @author banyue
 * @date 2025-09-01
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = BlsTableUsage.class)
public class BlsTableUsageVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    @ExcelProperty(value = "记录ID")
    private String id;

    /**
     * 门店ID
     */
    @ExcelProperty(value = "门店ID")
    private String storeId;

    /**
     * 桌台ID
     */
    @ExcelProperty(value = "桌台ID")
    private String tableId;

    /**
     * 订单ID
     */
    @ExcelProperty(value = "订单ID")
    private String orderId;

    /**
     * 用户ID
     */
    @ExcelProperty(value = "用户ID")
    private Long userId;

    /**
     * 开始时间
     */
    @ExcelProperty(value = "开始时间")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @ExcelProperty(value = "结束时间")
    private LocalDateTime endTime;

    /**
     * 使用时长(分钟)
     */
    @ExcelProperty(value = "使用时长(分钟)")
    private Integer duration;

}
