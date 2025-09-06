package org.dromara.system.domain.bo;

import org.dromara.system.domain.SysMerchantApply;
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
 * 商户注册申请业务对象 sys_merchant_apply
 *
 * @author banyue
 * @date 2025-09-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SysMerchantApply.class, reverseConvertGenerate = false)
public class SysMerchantApplyBo extends BaseEntity {

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
     * 商户名称
     */
    @NotBlank(message = "商户名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String name;

    /**
     * 微信商户号(选填或后置提交)
     */
    private String wxMchId;

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
}
