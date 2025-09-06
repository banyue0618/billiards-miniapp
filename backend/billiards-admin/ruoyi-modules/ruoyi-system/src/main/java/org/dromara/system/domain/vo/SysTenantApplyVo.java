package org.dromara.system.domain.vo;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.dromara.system.domain.SysTenantApply;
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
 * 租户注册申请视图对象 sys_tenant_apply
 *
 * @author banyue
 * @date 2025-09-01
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SysTenantApply.class)
public class SysTenantApplyVo implements Serializable {

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
     * 公司/机构名称
     */
    @ExcelProperty(value = "公司/机构名称")
    private String companyName;

    /**
     * 统一社会信用代码
     */
    @ExcelProperty(value = "统一社会信用代码")
    private String creditCode;

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
     * 省
     */
    @ExcelProperty(value = "省")
    private String province;

    /**
     * 市
     */
    @ExcelProperty(value = "市")
    private String city;

    /**
     * 区/县
     */
    @ExcelProperty(value = "区/县")
    private String district;

    /**
     * 详细地址
     */
    @ExcelProperty(value = "详细地址")
    private String address;

    /**
     * 营业执照附件(资源ID/URL)
     */
    @ExcelProperty(value = "营业执照附件(资源ID/URL)")
    private String businessLicense;

    /**
     * 租户套餐ID（可选）
     */
    @ExcelProperty(value = "租户套餐ID", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "可=选")
    private Long packageId;

    /**
     * 预计账户数(可选，用于套餐评估)
     */
    @ExcelProperty(value = "预计账户数(可选，用于套餐评估)")
    private Long expectedUsers;

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
