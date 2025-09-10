package org.dromara.billiards.domain.vo;

import java.time.LocalDateTime;

import org.dromara.billiards.domain.entity.BlsMemberPointsValidity;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;


/**
 * 积分有效期视图对象 bls_member_points_validity
 *
 * @author banyue
 * @date 2025-06-17
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = BlsMemberPointsValidity.class)
public class BlsMemberPointsValidityVo implements Serializable {

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
     * 积分数量
     */
    @ExcelProperty(value = "积分数量")
    private Long points;

    /**
     * 剩余积分数量
     */
    @ExcelProperty(value = "剩余积分数量")
    private Long remainingPoints;

    /**
     * 过期时间
     */
    @ExcelProperty(value = "过期时间")
    private LocalDateTime expireTime;


}
