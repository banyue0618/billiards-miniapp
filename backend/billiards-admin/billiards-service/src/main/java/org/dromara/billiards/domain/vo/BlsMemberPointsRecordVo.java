package org.dromara.billiards.domain.vo;

import java.util.Date;

import org.dromara.billiards.domain.entity.BlsMemberPointsRecord;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;


/**
 * 会员积分记录视图对象 bls_member_points_record
 *
 * @author banyue
 * @date 2025-06-17
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = BlsMemberPointsRecord.class)
public class BlsMemberPointsRecordVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    @ExcelProperty(value = "记录ID")
    private String id;

    /**
     * 用户ID
     */
    @ExcelProperty(value = "用户ID")
    private Long userId;

    /**
     * 积分数量（正数表示获取，负数表示消耗）
     */
    @ExcelProperty(value = "积分数量", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "正=数表示获取，负数表示消耗")
    private Long points;

    /**
     * 类型：1-获取 2-消耗
     */
    @ExcelProperty(value = "类型：1-获取 2-消耗")
    private Long type;

    /**
     * 场景，与积分规则表场景对应
     */
    @ExcelProperty(value = "场景，与积分规则表场景对应")
    private Long scene;

    /**
     * 对应的规则ID
     */
    @ExcelProperty(value = "对应的规则ID")
    private String ruleId;

    /**
     * 关联业务ID（如订单ID、活动ID等）
     */
    @ExcelProperty(value = "关联业务ID", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "如=订单ID、活动ID等")
    private String businessId;

    /**
     * 积分描述
     */
    @ExcelProperty(value = "积分描述")
    private String description;

    /**
     * 过期时间
     */
    @ExcelProperty(value = "过期时间")
    private Date expireTime;


}
