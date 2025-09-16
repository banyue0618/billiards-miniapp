package org.dromara.billiards.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 支付服务商配置(门店>商户>租户)对象 bls_pay_channel_config
 *
 * @author banyue
 * @date 2025-08-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("bls_pay_channel_config")
public class BlsPayChannelConfig extends BilliardsBaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id")
    private String id;

    /**
     * 门店ID(可空=商户/租户级)
     */
    private String storeId;

    /**
     * AppId
     */
    private String appId;

    /**
     * 子商户号
     */
    private String subMchId;

    /**
     * 0启用 1停用
     */
    private Long status;

}
