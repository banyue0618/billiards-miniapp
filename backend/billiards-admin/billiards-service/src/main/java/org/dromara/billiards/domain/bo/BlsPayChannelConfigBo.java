package org.dromara.billiards.domain.bo;

import org.dromara.billiards.domain.entity.BlsPayChannelConfig;
import org.dromara.billiards.domain.entity.BlsTenantMchEntity;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 支付服务商配置(门店>商户>租户)业务对象 bls_pay_channel_config
 *
 * @author banyue
 * @date 2025-08-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = BlsPayChannelConfig.class, reverseConvertGenerate = false)
public class BlsPayChannelConfigBo extends BlsTenantMchEntity {

    /**
     * ID
     */
    @NotBlank(message = "ID不能为空", groups = { EditGroup.class })
    private String id;

    /**
     * 商家ID(可空=租户级)
     */
    private String merchantId;

    /**
     * 门店ID(可空=商户/租户级)
     */
    private String storeId;

    /**
     * AppId
     */
    @NotBlank(message = "AppId不能为空", groups = { AddGroup.class, EditGroup.class })
    private String appId;

    /**
     * 子商户号
     */
    @NotBlank(message = "子商户号不能为空", groups = { AddGroup.class, EditGroup.class })
    private String subMchId;

    /**
     * 0启用 1停用
     */
    @NotNull(message = "0启用 1停用不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long status;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    private Long isDelete;


}
