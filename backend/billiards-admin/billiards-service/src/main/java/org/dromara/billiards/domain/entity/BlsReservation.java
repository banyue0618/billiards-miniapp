package org.dromara.billiards.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;

import java.io.Serial;

/**
 * 用户预约记录对象 bls_reservation
 *
 * @author banyue
 * @date 2025-11-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("bls_reservation")
public class BlsReservation extends BilliardsBaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 所属商家ID
     */
    private String merchantId;

    /**
     * 预约编号（可用于展示或查询）
     */
    private String reservationNo;

    /**
     * 预约用户ID
     */
    private Long userId;

    /**
     * 门店ID
     */
    private String storeId;

    /**
     * 台球桌ID
     */
    private String tableId;

    /**
     * 预约开始时间
     */
    private Date startTime;

    /**
     * 预约结束时间
     */
    private Date endTime;

    /**
     * 状态：0=预约中,1=已到店,2=已完成,3=已取消,4=已过期
     */
    private Long status;

    /**
     * 支付状态：0=未支付,1=已支付,2=已退款
     */
    private Long payStatus;

    /**
     * 支付金额
     */
    private Long payAmount;

    /**
     * 支付时间
     */
    private Date payTime;

    /**
     * 到店确认时间（扫码/签到）
     */
    private Date checkInTime;

    /**
     * 取消时间
     */
    private Date cancelTime;

    /**
     * 备注（如包厢号、特殊说明）
     */
    private String remark;

}
