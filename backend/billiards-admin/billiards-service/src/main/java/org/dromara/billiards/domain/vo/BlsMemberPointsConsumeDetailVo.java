package org.dromara.billiards.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import org.dromara.billiards.domain.entity.BlsMemberPointsConsumeDetail;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;



/**
 * 会员积分消费详情视图对象 bls_member_points_consume_detail
 *
 * @author banyue
 * @date 2025-09-09
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = BlsMemberPointsConsumeDetail.class)
public class BlsMemberPointsConsumeDetailVo implements Serializable {

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
     * 本行扣减的积分（正数表示扣减量）
     */
    @ExcelProperty(value = "本行扣减的积分", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "正=数表示扣减量")
    private Long points;


}
