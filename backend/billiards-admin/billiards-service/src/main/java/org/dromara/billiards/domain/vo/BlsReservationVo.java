package org.dromara.billiards.domain.vo;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import org.dromara.billiards.domain.entity.BlsReservation;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;



/**
 * 用户预约记录视图对象 bls_reservation
 *
 * @author banyue
 * @date 2025-11-03
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = BlsReservation.class)
public class BlsReservationVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 预约编号（可用于展示或查询）
     */
    @ExcelProperty(value = "预约编号", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "可=用于展示或查询")
    private String reservationNo;

    /**
     * 预约开始时间
     */
    @ExcelProperty(value = "预约开始时间")
    private Date startTime;

    /**
     * 预约结束时间
     */
    @ExcelProperty(value = "预约结束时间")
    private Date endTime;

    /**
     * 状态：0=预约中,1=已到店,2=已完成,3=已取消,4=已过期
     */
    @ExcelProperty(value = "状态：0=预约中,1=已到店,2=已完成,3=已取消,4=已过期")
    private Long status;

    /**
     * 支付状态：0=未支付,1=已支付,2=已退款
     */
    @ExcelProperty(value = "支付状态：0=未支付,1=已支付,2=已退款")
    private Long payStatus;

    /**
     * 支付金额
     */
    @ExcelProperty(value = "支付金额")
    private Long payAmount;

    /**
     * 支付时间
     */
    @ExcelProperty(value = "支付时间")
    private Date payTime;

    /**
     * 备注（如包厢号、特殊说明）
     */
    @ExcelProperty(value = "备注", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "如=包厢号、特殊说明")
    private String remark;


}
