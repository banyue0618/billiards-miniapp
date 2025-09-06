package org.dromara.system.domain;

import org.dromara.common.mybatis.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serial;

/**
 * 租户注册申请对象 sys_tenant_apply
 *
 * @author banyue
 * @date 2025-09-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_tenant_apply")
public class SysTenantApply extends BaseEntity {

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
     * 公司/机构名称
     */
    private String companyName;

    /**
     * 统一社会信用代码
     */
    private String creditCode;

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
     * 省
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 区/县
     */
    private String district;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 营业执照附件(资源ID/URL)
     */
    private String businessLicense;

    /**
     * 租户套餐ID（可选）
     */
    private Long packageId;

    /**
     * 预计账户数(可选，用于套餐评估)
     */
    private Long expectedUsers;

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
