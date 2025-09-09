package org.dromara.billiards.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.file.annotation.FileResource;

import java.math.BigDecimal;

/**
 * 门店实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("bls_store")
public class BlsStore extends BilliardsBaseEntity {

    /**
     * 门店ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 所属商家ID
     */
    private String merchantId = "1";

    /**
     * 门店名称
     */
    private String name;

    /**
     * 门店封面图URL
     */
    @FileResource
    private String coverImage;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 区县
     */
    private String district;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 纬度坐标
     */
    private BigDecimal latitude;

    /**
     * 经度坐标
     */
    private BigDecimal longitude;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 营业时间
     */
    private String businessHours;

    /**
     * 公告
     */
    private String announcement;

    /**
     * 状态 0-正常 1-休息 2-停业
     */
    private Integer status;
}
