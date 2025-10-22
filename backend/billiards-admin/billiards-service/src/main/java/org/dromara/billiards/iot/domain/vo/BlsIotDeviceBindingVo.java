package org.dromara.billiards.iot.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import org.dromara.billiards.iot.domain.BlsIotDeviceBinding;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;



/**
 * 设备业务绑定（定义场景与设备动作映射）视图对象 bls_iot_device_binding
 *
 * @author banyue
 * @date 2025-10-16
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = BlsIotDeviceBinding.class)
public class BlsIotDeviceBindingVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ExcelProperty(value = "主键")
    private Long id;

    /**
     * 业务场景：open_table/close_table/timeout等
     */
    @ExcelProperty(value = "业务场景：open_table/close_table/timeout等", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "device_scene")
    private String scene;

    /**
     * 设备编号（外键）
     */
    @ExcelProperty(value = "设备编号", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "外=键")
    private String deviceCode;

    /**
     * 控制命令：turn_on/turn_off/play_audio等
     */
    @ExcelProperty(value = "控制命令：turn_on/turn_off/play_audio等", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "device_control_command")
    private String command;

    /**
     * 命令参数（如音量、文件名等）
     */
    @ExcelProperty(value = "命令参数", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "如=音量、文件名等")
    private String params;

    /**
     * 是否启用：1启用，0禁用
     */
    @ExcelProperty(value = "是否启用：1启用，0禁用", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "enable_status")
    private Long enabled;

    /**
     * MQTT主题（仅MQTT协议设备使用）
     */
    private String mqttTopic;
}
