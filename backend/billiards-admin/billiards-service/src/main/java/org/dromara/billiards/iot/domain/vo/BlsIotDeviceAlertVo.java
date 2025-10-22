package org.dromara.billiards.iot.domain.vo;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import org.dromara.billiards.iot.domain.BlsIotDeviceAlert;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;



/**
 * 设备告警（记录设备异常信息）视图对象 bls_iot_device_alert
 *
 * @author banyue
 * @date 2025-10-16
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = BlsIotDeviceAlert.class)
public class BlsIotDeviceAlertVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 告警类型：offline/control_failed等
     */
    @ExcelProperty(value = "告警类型：offline/control_failed等", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "alert_type")
    private String alertType;

    /**
     * 告警级别：info/warning/error/critical
     */
    @ExcelProperty(value = "告警级别：info/warning/error/critical", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "alert_level")
    private String alertLevel;

    /**
     * 设备编号
     */
    @ExcelProperty(value = "设备编号")
    private String deviceCode;

    /**
     * 设备名称
     */
    @ExcelProperty(value = "设备名称")
    private String deviceName;

    /**
     * 处理状态：pending/resolved/ignored
     */
    @ExcelProperty(value = "处理状态：pending/resolved/ignored", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "alert_process_status")
    private String status;

    /**
     * 处理人
     */
    @ExcelProperty(value = "处理人")
    private String handler;

    /**
     * 处理时间
     */
    @ExcelProperty(value = "处理时间")
    private Date handleTime;


}
