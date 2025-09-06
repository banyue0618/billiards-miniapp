package org.dromara.system.domain.bo;

import org.dromara.system.domain.SysMerchant;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 商户业务对象 sys_merchant
 *
 * @author banyue
 * @date 2025-09-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SysMerchant.class, reverseConvertGenerate = false)
public class SysMerchantBo extends BaseEntity {

    /**
     * 商家ID
     */
    @NotNull(message = "商家ID不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 微信商户id
     */
    private String wxMchId;

    /**
     * 商家名称
     */
    @NotBlank(message = "商家名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String name;

    /**
     * 商家Logo
     */
    private String logo;

    /**
     * 联系人姓名
     */
    private String contactName;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 状态 0-正常 1-冻结
     */
    private Long status;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    private Long isDelete;


}
