package org.dromara.billiards.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 桌台实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("bls_table")
public class BlsTable extends BlsTenantMchEntity {

    /**
     * 桌台ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 所属门店ID
     */
    private String storeId;

    /**
     * 桌台编号
     */
    private String tableNumber;

    /**
     * 桌台编号前缀
     */
    private String tablePrefix;

    /**
     * 桌台编号数字部分
     */
    private Integer tableNumeric;

    /**
     * 桌台类型 1-普通 2-专业 3-大师
     */
    private Integer tableType;

    /**
     * 桌台描述
     */
    private String description;

    /**
     * 桌台图片URL
     */
    private String image;

    /**
     * 二维码资源ID
     */
    private String qrcodeUrl;

    /**
     * 计费规则ID
     */
    private String priceRuleId;

    /**
     * 状态 0-空闲 1-使用中 2-维修中 3-锁定
     */
    private Integer status;

    @Version
    private Integer version;
}
