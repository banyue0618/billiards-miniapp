package org.dromara.billiards.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 充值记录视图对象
 *
 * @author banyue
 */
@Data
@Schema(description = "充值记录VO")
public class RechargeRecordVO {

    @Schema(description = "记录ID")
    private String id;

    @Schema(description = "充值流水号")
    private String payNo;

    @Schema(description = "充值金额(元)")
    private BigDecimal amount;

    @Schema(description = "支付渠道")
    private String channel;

    @Schema(description = "支付状态: 0-待支付, 1-已支付, 2-支付失败")
    private Integer paymentStatus;

    @Schema(description = "微信支付交易号")
    private String transactionId;

    @Schema(description = "商户名称")
    private String merchantName;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "退款状态: 0-退款中, 1-退款成功, 2-退款失败")
    private Integer refundStatus;

    @Schema(description = "退款金额(元)")
    private BigDecimal refundAmount;
}

