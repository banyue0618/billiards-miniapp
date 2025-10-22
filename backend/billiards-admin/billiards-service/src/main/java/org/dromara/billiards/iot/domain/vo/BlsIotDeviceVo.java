package org.dromara.billiards.iot.domain.vo;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import org.dromara.billiards.iot.domain.BlsIotDevice;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;



/**
 * IoT设备视图对象 bls_iot_device
 *
 * @author banyue
 * @date 2025-10-16
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = BlsIotDevice.class)
public class BlsIotDeviceVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 设备唯一编号
     */
    @ExcelProperty(value = "设备唯一编号")
    private String code;

    /**
     * 设备名称
     */
    @ExcelProperty(value = "设备名称")
    private String name;

    /**
     * 设备类型：light/lock/speaker/other
     */
    @ExcelProperty(value = "设备类型：light/lock/speaker/other", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "device_type")
    private String type;

    /**
     * 协议类型：mqtt/http/modbus
     */
    @ExcelProperty(value = "协议类型：mqtt/http/modbus", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "device_protocol_type")
    private String protocol;

    /**
     * 协议配置（topic/ip/port等）
     */
    @ExcelProperty(value = "协议配置", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "t=opic/ip/port等")
    private String protocolConfig;

    /**
     * 设备状态：online/offline/error
     */
    @ExcelProperty(value = "设备状态：online/offline/error", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "device_status")
    private String status;

    /**
     * 最后心跳时间
     */
    @ExcelProperty(value = "最后心跳时间")
    private Date lastHeartbeat;

    /**
     * 备注信息
     */
    @ExcelProperty(value = "备注信息")
    private String remark;


}
