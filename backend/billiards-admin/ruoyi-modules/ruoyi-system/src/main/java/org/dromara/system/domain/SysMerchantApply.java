package org.dromara.system.domain;

import org.dromara.common.tenant.core.TenantEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serial;

/**
 * 商户注册申请对象 sys_merchant_apply
 *
 * @author banyue
 * @date 2025-09-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_merchant_apply")
public class SysMerchantApply extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 申请单号
     */
    private String applyNo;

    /**
     * 商户名称
     */
    private String name;

    /**
     * 微信商户号(选填或后置提交)
     */
    private String wxMchId;

    /**
     * 联系人姓名
     */
    private String contactName;

    /**
     * 联系人手机号
     */
    private String contactPhone;

    /**
     * 联系人邮箱
     */
    private String contactEmail;

    /**
     * 
     */
    private String province;

    /**
     * 
     */
    private String city;

    /**
     * 
     */
    private String district;

    /**
     * 
     */
    private String address;

    /**
     * Logo 资源ID/URL
     */
    private String logo;

    /**
     * 营业执照附件(资源ID/URL)
     */
    private String businessLicense;

    /**
     * 结算-账户名
     */
    private String bankAccountName;

    /**
     * 结算-账号
     */
    private String bankAccountNo;

    /**
     * 结算-开户行
     */
    private String bankName;

    /**
     * 备注/补充说明
     */
    private String remark;

    /**
     * 状态:0待审 1通过 2驳回
     */
    private Long status;

    /**
     * 审核人ID（sys_user）
     */
    private Long auditBy;

    /**
     * 审核时间
     */
    private Date auditTime;

    /**
     * 审核意见/驳回原因
     */
    private String auditReason;

    /**
     * 
     */
    private Long isDelete;


}
