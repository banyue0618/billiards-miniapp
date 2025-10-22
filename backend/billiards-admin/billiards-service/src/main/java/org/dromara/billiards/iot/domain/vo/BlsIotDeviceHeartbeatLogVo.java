package org.dromara.billiards.iot.domain.vo;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.dromara.billiards.iot.domain.BlsIotDeviceHeartbeatLog;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;



/**
 * IoT设备心跳记录视图对象 bls_iot_device_heartbeat_log
 *
 * @author banyue
 * @date 2025-10-22
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = BlsIotDeviceHeartbeatLog.class)
public class BlsIotDeviceHeartbeatLogVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @ExcelProperty(value = "主键ID")
    private Long id;

    /**
     * 设备编码（与iot_device表关联）
     */
    @ExcelProperty(value = "设备编码", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "与=iot_device表关联")
    private String deviceCode;

    /**
     * 设备IP地址（可选）
     */
    @ExcelProperty(value = "设备IP地址", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "可=选")
    private String ipAddress;

    /**
     * 固件版本（可选）
     */
    @ExcelProperty(value = "固件版本", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "可=选")
    private String firmwareVersion;

    /**
     * 心跳上报时间
     */
    @ExcelProperty(value = "心跳上报时间")
    private Date heartbeatTime;

    /**
     * WiFi信号强度（dBm，可选）
     */
    @ExcelProperty(value = "WiFi信号强度", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "d=Bm，可选")
    private Long signalStrength;

}
