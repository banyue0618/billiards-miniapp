package org.dromara.billiards.domain.vo;

import java.math.BigDecimal;
import org.dromara.billiards.domain.entity.BlsMemberLevelConfig;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;


/**
 * 会员等级配置视图对象 bls_member_level_config
 *
 * @author banyue
 * @date 2025-06-17
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = BlsMemberLevelConfig.class)
public class BlsMemberLevelConfigVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 配置ID
     */
    @ExcelProperty(value = "配置ID")
    private String id;

    /**
     * 等级编码
     */
    @ExcelProperty(value = "等级编码")
    private String levelCode;

    /**
     * 等级名称
     */
    @ExcelProperty(value = "等级名称")
    private String levelName;

    /**
     * 所需累计消费金额
     */
    @ExcelProperty(value = "所需累计消费金额")
    private BigDecimal requiredAmount;

    /**
     * 折扣率
     */
    @ExcelProperty(value = "折扣率")
    private BigDecimal discount;

    /**
     * 每月赠送时长（分钟）
     */
    @ExcelProperty(value = "每月赠送时长", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "分=钟")
    private Long monthlyFreeMinutes;

    /**
     * 积分获取倍率
     */
    @ExcelProperty(value = "积分获取倍率")
    private BigDecimal pointsMultiplier;

    /**
     * 生日特权折扣率
     */
    @ExcelProperty(value = "生日特权折扣率")
    private BigDecimal birthdayDiscount;

    /**
     * 可带朋友享受会员价的人数
     */
    @ExcelProperty(value = "可带朋友享受会员价的人数")
    private Long friendPrivilegeCount;

    /**
     * 专属客服服务 0-否 1-是
     */
    @ExcelProperty(value = "专属客服服务 0-否 1-是")
    private Long vipService;

    /**
     * 预约特权 0-否 1-是
     */
    @ExcelProperty(value = "预约特权 0-否 1-是")
    private Long reservationPrivilege;

    /**
     * 等级图标
     */
    @ExcelProperty(value = "等级图标")
    private String levelIcon;

    /**
     * 等级背景图
     */
    @ExcelProperty(value = "等级背景图")
    private String levelBackground;

    /**
     * 等级描述
     */
    @ExcelProperty(value = "等级描述")
    private String description;

    /**
     * 状态 0-启用 1-禁用
     */
    @ExcelProperty(value = "状态 0-启用 1-禁用")
    private Integer status;


}
