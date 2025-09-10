package org.dromara.billiards.domain.vo;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import org.dromara.billiards.domain.entity.BlsMemberChangeLog;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;



/**
 * 会员变更记录视图对象 bls_member_change_log
 *
 * @author banyue
 * @date 2025-09-15
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = BlsMemberChangeLog.class)
public class BlsMemberChangeLogVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    @ExcelProperty(value = "记录ID")
    private String id;

    /**
     * 商家ID
     */
    @ExcelProperty(value = "商家ID")
    private String merchantId;

    /**
     * 用户ID
     */
    @ExcelProperty(value = "用户ID")
    private String userId;

    /**
     * 变更类型 NEW/RENEWAL/UPGRADE/EXPIRED
     */
    @ExcelProperty(value = "变更类型 NEW/RENEWAL/UPGRADE/EXPIRED")
    private String changeType;

    /**
     * 变更前等级
     */
    @ExcelProperty(value = "变更前等级")
    private String beforeLevel;

    /**
     * 变更后等级
     */
    @ExcelProperty(value = "变更后等级")
    private String afterLevel;

    /**
     * 变更前过期时间
     */
    @ExcelProperty(value = "变更前过期时间")
    private LocalDateTime beforeExpire;

    /**
     * 变更后过期时间
     */
    @ExcelProperty(value = "变更后过期时间")
    private LocalDateTime afterExpire;

    /**
     * 关联订单ID
     */
    @ExcelProperty(value = "关联订单ID")
    private String orderId;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;


}
