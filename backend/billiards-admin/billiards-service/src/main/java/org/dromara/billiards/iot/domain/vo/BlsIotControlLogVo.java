package org.dromara.billiards.iot.domain.vo;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import org.dromara.billiards.iot.domain.BlsIotControlLog;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;



/**
 * 设备控制日志（记录执行命令历史）视图对象 bls_iot_control_log
 *
 * @author banyue
 * @date 2025-10-16
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = BlsIotControlLog.class)
public class BlsIotControlLogVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 设备编号
     */
    @ExcelProperty(value = "设备编号")
    private String deviceCode;

    /**
     * 控制命令
     */
    @ExcelProperty(value = "控制命令", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "device_control_command")
    private String command;

    /**
     * 触发场景：open_table/close_table等
     */
    @ExcelProperty(value = "触发场景：open_table/close_table等", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "trigger_scene")
    private String triggerScene;

    /**
     * 执行状态：success/failed/timeout/pending
     */
    @ExcelProperty(value = "执行状态：success/failed/timeout/pending", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "device_control_command_execute_status")
    private String status;

    /**
     * 执行时间
     */
    @ExcelProperty(value = "执行时间")
    private Date executeTime;

    /**
     * 响应耗时（毫秒）
     */
    @ExcelProperty(value = "响应耗时", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "毫=秒")
    private Long responseTime;


}
