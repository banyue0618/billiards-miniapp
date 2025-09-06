package org.dromara.system.domain;

import org.dromara.common.tenant.core.TenantEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 商户对象 sys_merchant
 *
 * @author banyue
 * @date 2025-09-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_merchant")
public class SysMerchant extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 商家ID
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 微信商户id
     */
    private String wxMchId;

    /**
     * 商家名称
     */
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
