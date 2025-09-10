package org.dromara.billiards.domain.vo;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import org.dromara.billiards.domain.entity.BlsEventOutbox;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;



/**
 * 本地消息(Outbox)视图对象 bls_event_outbox
 *
 * @author banyue
 * @date 2025-09-15
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = BlsEventOutbox.class)
public class BlsEventOutboxVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 事件ID
     */
    @ExcelProperty(value = "事件ID")
    private String id;

    /**
     * 商家ID
     */
    @ExcelProperty(value = "商家ID")
    private String merchantId;

    /**
     * 聚合根类型，如 ORDER
     */
    @ExcelProperty(value = "聚合根类型，如 ORDER")
    private String aggregateType;

    /**
     * 聚合根ID，如订单ID
     */
    @ExcelProperty(value = "聚合根ID，如订单ID")
    private String aggregateId;

    /**
     * 事件类型，如 ORDER_COMPLETED/REFUND_REQUESTED
     */
    @ExcelProperty(value = "事件类型，如 ORDER_COMPLETED/REFUND_REQUESTED")
    private String eventType;

    /**
     * 事件载荷JSON
     */
    @ExcelProperty(value = "事件载荷JSON")
    private String payload;

    /**
     * 状态：0-NEW 1-SENT 2-FAILED
     */
    @ExcelProperty(value = "状态：0-NEW 1-SENT 2-FAILED")
    private Long status;

    /**
     * 重试次数
     */
    @ExcelProperty(value = "重试次数")
    private Long retryCount;

    /**
     * 下次重试时间
     */
    @ExcelProperty(value = "下次重试时间")
    private LocalDateTime nextRetryTime;

    /**
     * 最后一次错误信息
     */
    @ExcelProperty(value = "最后一次错误信息")
    private String lastError;


}
