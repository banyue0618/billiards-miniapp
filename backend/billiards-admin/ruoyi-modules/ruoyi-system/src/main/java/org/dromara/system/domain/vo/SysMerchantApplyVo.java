package org.dromara.system.domain.vo;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.dromara.system.domain.SysMerchantApply;
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
 * 商户注册申请视图对象 sys_merchant_apply
 *
 * @author banyue
 * @date 2025-09-01
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SysMerchantApply.class)
public class SysMerchantApplyVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ExcelProperty(value = "主键")
    private Long id;

    /**
     * 申请单号
     */
    @ExcelProperty(value = "申请单号")
    private String applyNo;

    /**
     * 商户名称
     */
    @ExcelProperty(value = "商户名称")
    private String name;

    /**
     * 微信商户号(选填或后置提交)
     */
    @ExcelProperty(value = "微信商户号(选填或后置提交)")
    private String wxMchId;

    /**
     * 联系人姓名
     */
    @ExcelProperty(value = "联系人姓名")
    private String contactName;

    /**
     * 联系人手机号
     */
    @ExcelProperty(value = "联系人手机号")
    private String contactPhone;

    /**
     * 联系人邮箱
     */
    @ExcelProperty(value = "联系人邮箱")
    private String contactEmail;

    /**
     * 
     */
    @ExcelProperty(value = "")
    private String province;

    /**
     * 
     */
    @ExcelProperty(value = "")
    private String city;

    /**
     * 
     */
    @ExcelProperty(value = "")
    private String district;

    /**
     * 
     */
    @ExcelProperty(value = "")
    private String address;

    /**
     * Logo 资源ID/URL
     */
    @ExcelProperty(value = "Logo 资源ID/URL")
    private String logo;

    /**
     * 营业执照附件(资源ID/URL)
     */
    @ExcelProperty(value = "营业执照附件(资源ID/URL)")
    private String businessLicense;

    /**
     * 结算-账户名
     */
    @ExcelProperty(value = "结算-账户名")
    private String bankAccountName;

    /**
     * 结算-账号
     */
    @ExcelProperty(value = "结算-账号")
    private String bankAccountNo;

    /**
     * 结算-开户行
     */
    @ExcelProperty(value = "结算-开户行")
    private String bankName;

    /**
     * 备注/补充说明
     */
    @ExcelProperty(value = "备注/补充说明")
    private String remark;

    /**
     * 状态:0待审 1通过 2驳回
     */
    @ExcelProperty(value = "状态:0待审 1通过 2驳回")
    private Long status;

    /**
     * 审核人ID（sys_user）
     */
    @ExcelProperty(value = "审核人ID", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "s=ys_user")
    private Long auditBy;

    /**
     * 审核时间
     */
    @ExcelProperty(value = "审核时间")
    private Date auditTime;

    /**
     * 审核意见/驳回原因
     */
    @ExcelProperty(value = "审核意见/驳回原因")
    private String auditReason;

    /**
     * 
     */
    @ExcelProperty(value = "")
    private Long isDelete;


}
