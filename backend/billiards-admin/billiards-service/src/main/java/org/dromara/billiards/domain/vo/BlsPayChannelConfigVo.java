package org.dromara.billiards.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import org.dromara.billiards.domain.entity.BlsPayChannelConfig;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;



/**
 * 支付服务商配置(门店>商户>租户)视图对象 bls_pay_channel_config
 *
 * @author banyue
 * @date 2025-08-28
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = BlsPayChannelConfig.class)
public class BlsPayChannelConfigVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @ExcelProperty(value = "ID")
    private String id;

    /**
     * 商家ID(可空=租户级)
     */
    @ExcelProperty(value = "商家ID(可空=租户级)")
    private String merchantId;

    /**
     * 门店ID(可空=商户/租户级)
     */
    @ExcelProperty(value = "门店ID(可空=商户/租户级)")
    private String storeId;

    /**
     * AppId
     */
    @ExcelProperty(value = "AppId")
    private String appId;

    /**
     * 子商户号
     */
    @ExcelProperty(value = "子商户号")
    private String subMchId;

    /**
     * 0启用 1停用
     */
    @ExcelProperty(value = "0启用 1停用")
    private Long status;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    @ExcelProperty(value = "删除标志", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "0=代表存在,1=代表删除")
    private Long isDelete;


}
