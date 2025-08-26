package org.dromara.billiards.domain.vo;

import java.math.BigDecimal;
import java.util.Date;

import org.dromara.billiards.domain.entity.BlsMemberUser;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;


/**
 * 会员用户视图对象 bls_member_user
 *
 * @author banyue
 * @date 2025-06-17
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = BlsMemberUser.class)
public class BlsMemberUserVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 会员ID
     */
    @ExcelProperty(value = "会员ID")
    private String id;

    /**
     * 用户ID
     */
    @ExcelProperty(value = "用户ID")
    private Long userId;

    /**
     * 当前等级编码
     */
    @ExcelProperty(value = "当前等级编码")
    private Long levelCode;

    /**
     * 累计消费金额
     */
    @ExcelProperty(value = "累计消费金额")
    private BigDecimal totalAmount;

    /**
     * 当前积分
     */
    @ExcelProperty(value = "当前积分")
    private Long points;

    /**
     * 本月已使用免费时长（分钟）
     */
    @ExcelProperty(value = "本月已使用免费时长", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "分=钟")
    private Long monthlyUsedMinutes;

    /**
     * 等级有效期
     */
    @ExcelProperty(value = "等级有效期")
    private Date levelExpireTime;

    /**
     * 最近消费时间
     */
    @ExcelProperty(value = "最近消费时间")
    private Date lastConsumeTime;

    /**
     * 状态：0-正常 1-禁用
     */
    @ExcelProperty(value = "状态：0-正常 1-禁用")
    private Long status;


}
