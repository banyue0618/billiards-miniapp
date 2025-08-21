package org.dromara.billiards.domain.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description
 * @Author zhangsip
 * @Version V1.0.0
 * @Since 1.0
 * @Date 2025/8/21
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TenantMchEntity extends BilliardsBaseEntity{

    /**
     * 租户编号
     */
    private String tenantId;


    /**
     * 商户id
     */
    private String merchantId;
}
