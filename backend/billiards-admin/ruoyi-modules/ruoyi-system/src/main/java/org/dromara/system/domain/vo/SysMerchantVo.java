package org.dromara.system.domain.vo;

import org.dromara.system.domain.SysMerchant;
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
 * 商户视图对象 sys_merchant
 *
 * @author banyue
 * @date 2025-09-01
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SysMerchant.class)
public class SysMerchantVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 商家ID
     */
    @ExcelProperty(value = "商家ID")
    private Long id;

    /**
     * 微信商户id
     */
    @ExcelProperty(value = "微信商户id")
    private String wxMchId;

    /**
     * 商家名称
     */
    @ExcelProperty(value = "商家名称")
    private String name;

    /**
     * 商家Logo
     */
    @ExcelProperty(value = "商家Logo")
    private String logo;

    /**
     * 联系人姓名
     */
    @ExcelProperty(value = "联系人姓名")
    private String contactName;

    /**
     * 联系电话
     */
    @ExcelProperty(value = "联系电话")
    private String contactPhone;

    /**
     * 状态 0-正常 1-冻结
     */
    @ExcelProperty(value = "状态 0-正常 1-冻结")
    private Long status;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    @ExcelProperty(value = "删除标志", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "0=代表存在,1=代表删除")
    private Long isDelete;


}
