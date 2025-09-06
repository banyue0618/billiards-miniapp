package org.dromara.billiards.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Description
 * @Author zhangsip
 * @Version V1.0.0
 * @Since 1.0
 * @Date 2025/5/15
 */
@Data
@Schema(description = "订单视图对象")
public class OrderVO {

    @Schema(description = "订单ID")
    private String id;

    @Schema(description = "订单编号")
    private String orderNo;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "门店ID")
    private String storeId;

    @Schema(description = "门店名称")
    private String storeName;

    @Schema(description = "桌台ID")
    private String tableId;

    @Schema(description = "桌台编号")
    private String tableNumber;

    @Schema(description = "计费规则ID")
    private String priceRuleId;

    @Schema(description = "开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @Schema(description = "使用时长(分钟)")
    private Integer duration;

    @Schema(description = "原始金额")
    private BigDecimal originalAmount;

    @Schema(description = "折扣金额")
    private BigDecimal discountAmount;

    @Schema(description = "实际金额")
    private BigDecimal actualAmount;

    @Schema(description = "支付状态 0-未支付 1-已支付 2-退款")
    private Integer paymentStatus;

    @Schema(description = "状态 0-进行中 1-已完成 2-已取消")
    private Integer status;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @JsonIgnore
    private String createBy;

    @JsonIgnore
    private String updateBy;

    @Schema(description = "当前订单的单价（标准计费）")
    private BigDecimal priceUnit;

    @Schema(description = "会员价格（标准计费）")
    private BigDecimal memberPrice;

    @Schema(description = "阶梯计费明细（阶梯计费）")
    private String ladderRules;

    @Schema(description = "会员折扣")
    private BigDecimal memberDiscount;

    @Schema(description = "退款金额")
    private BigDecimal refundAmount;

    @Schema(description = "退款状态 0-退款中 1-退款成功 2-退款失败")
    private Integer refundStatus;

    @Schema(description = "开台用户")
    private String userName;

    @Schema(description = "用户手机号")
    private String userPhone;
}
