package org.dromara.billiards.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;

import java.math.BigDecimal;

@Data
@Schema(description = "门店数据传输对象") // 更通用的描述
public class StoreDto {

    @NotBlank(message = "门店名称不能为空", groups = {AddGroup.class, EditGroup.class}) // 假设名称在新增和编辑时都不能为空
    @Schema(description = "门店名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "门店封面图URL (相对路径，由文件上传接口返回)")
    private String coverImage;

    @NotBlank(message = "省份不能为空", groups = {AddGroup.class, EditGroup.class})
    @Schema(description = "省份", requiredMode = Schema.RequiredMode.REQUIRED)
    private String province;

    @NotBlank(message = "城市不能为空", groups = {AddGroup.class, EditGroup.class})
    @Schema(description = "城市", requiredMode = Schema.RequiredMode.REQUIRED)
    private String city;

    @NotBlank(message = "区县不能为空", groups = {AddGroup.class, EditGroup.class})
    @Schema(description = "区县", requiredMode = Schema.RequiredMode.REQUIRED)
    private String district;

    @NotBlank(message = "详细地址不能为空", groups = {AddGroup.class, EditGroup.class})
    @Schema(description = "详细地址", requiredMode = Schema.RequiredMode.REQUIRED)
    private String address;

    @NotNull(message = "纬度坐标不能为空", groups = {AddGroup.class, EditGroup.class})
    @DecimalMin(value = "-90.0", message = "纬度最小值为-90")
    @DecimalMax(value = "90.0", message = "纬度最大值为90")
    @Schema(description = "纬度坐标", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal latitude;

    @NotNull(message = "经度坐标不能为空", groups = {AddGroup.class, EditGroup.class})
    @DecimalMin(value = "-180.0", message = "经度最小值为-180")
    @DecimalMax(value = "180.0", message = "经度最大值为180")
    @Schema(description = "经度坐标", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal longitude;

    @Pattern(regexp = "^(1\\d{10}|(\\d{3,4}-)?\\d{7,8}(-\\d{1,4})?)$", message = "联系电话格式不正确", groups = {AddGroup.class, EditGroup.class})
    @Schema(description = "联系电话 (手机号或座机号)")
    private String contactPhone;

    @Schema(description = "营业时间 (例如：周一至周五 09:00-18:00)")
    private String businessHours;

    @Schema(description = "公告")
    private String announcement;

    // 对于编辑操作，通常需要传入ID
    @NotNull(message = "ID不能为空", groups = EditGroup.class)
    @Schema(description = "门店ID，更新时使用")
    private String id;

    // status 字段通常不在创建DTO中，而是在专门的状态更新接口中，或者在Service层处理
    // 如果也希望通过此DTO更新状态，可以添加，并配合EditGroup
    // @Schema(description = "状态 0-正常 1-休息 2-停业", groups = EditGroup.class)
    // private Integer status;
}
