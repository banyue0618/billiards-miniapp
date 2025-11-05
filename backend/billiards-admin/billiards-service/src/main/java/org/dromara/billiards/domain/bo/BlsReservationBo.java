package org.dromara.billiards.domain.bo;

import org.dromara.billiards.domain.entity.BlsReservation;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 用户预约记录业务对象 bls_reservation
 *
 * @author banyue
 * @date 2025-11-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = BlsReservation.class, reverseConvertGenerate = false)
public class BlsReservationBo extends BaseEntity {

    /**
     * 主键ID
     */
    @NotNull(message = "主键ID不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 预约编号（可用于展示或查询）
     */
    @NotBlank(message = "预约编号（可用于展示或查询）不能为空", groups = { EditGroup.class })
    private String reservationNo;

    /**
     * 预约开始时间
     */
    @NotNull(message = "预约开始时间不能为空", groups = { AddGroup.class, EditGroup.class })
    private LocalDateTime startTime;

    /**
     * 预约结束时间
     */
    @NotNull(message = "预约结束时间不能为空", groups = { AddGroup.class, EditGroup.class })
    private LocalDateTime endTime;

    /**
     * 状态：0=预约中,1=已到店,2=已完成,3=已取消,4=已过期
     */
    private Long status;

    /**
     * 支付状态：0=未支付,1=已支付,2=已退款
     */
    private Long payStatus;

    /**
     * 取消时间
     */
    private Date cancelTime;

    /**
     * 备注（如包厢号、特殊说明）
     */
    private String remark;


    /**
     * 预约的桌台ID
     */
    @NotNull(message = "请选择预约的桌台", groups = { AddGroup.class, EditGroup.class })
    private String tableId;
}
