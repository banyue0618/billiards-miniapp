package org.dromara.billiards.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
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
public class BlsTenantMchEntity extends BilliardsBaseEntity{

    /**
     * 租户编号
     */
    @TableField(value = "tenant_id", fill = FieldFill.INSERT)
    private String tenantId;


    /**
     * 商户id
     */
    @TableField(value = "merchant_id", fill = FieldFill.INSERT)
    private String merchantId;
}
