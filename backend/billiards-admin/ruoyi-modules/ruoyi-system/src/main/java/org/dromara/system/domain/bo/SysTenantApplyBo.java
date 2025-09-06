package org.dromara.system.domain.bo;

import org.dromara.system.domain.SysTenantApply;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 租户注册申请业务对象 sys_tenant_apply
 *
 * @author banyue
 * @date 2025-09-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SysTenantApply.class, reverseConvertGenerate = false)
public class SysTenantApplyBo extends BaseEntity {

    /**
     * 主键
     */
    @NotNull(message = "主键不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 申请单号
     */
    private String applyNo;

    /**
     * 公司/机构名称
     */
    @NotBlank(message = "公司/机构名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String companyName;

    /**
     * 统一社会信用代码
     */
    private String creditCode;

    /**
     * 联系人姓名
     */
    @NotBlank(message = "联系人姓名不能为空", groups = { AddGroup.class, EditGroup.class })
    private String contactName;

    /**
     * 联系人手机号
     */
    @NotBlank(message = "联系人手机号不能为空", groups = { AddGroup.class, EditGroup.class })
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
}
