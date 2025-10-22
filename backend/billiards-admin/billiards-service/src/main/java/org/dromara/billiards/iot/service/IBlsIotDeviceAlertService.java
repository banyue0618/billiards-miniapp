package org.dromara.billiards.iot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.dromara.billiards.iot.domain.BlsIotDeviceAlert;
import org.dromara.billiards.iot.domain.bo.BlsIotDeviceAlertBo;
import org.dromara.billiards.iot.domain.vo.BlsIotDeviceAlertVo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 设备告警（记录设备异常信息）Service接口
 *
 * @author banyue
 * @date 2025-10-16
 */
public interface IBlsIotDeviceAlertService extends IService<BlsIotDeviceAlert> {

    /**
     * 查询设备告警（记录设备异常信息）
     *
     * @param id 主键
     * @return 设备告警（记录设备异常信息）
     */
    BlsIotDeviceAlertVo queryById(Long id);

    /**
     * 分页查询设备告警（记录设备异常信息）列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 设备告警（记录设备异常信息）分页列表
     */
    TableDataInfo<BlsIotDeviceAlertVo> queryPageList(BlsIotDeviceAlertBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的设备告警（记录设备异常信息）列表
     *
     * @param bo 查询条件
     * @return 设备告警（记录设备异常信息）列表
     */
    List<BlsIotDeviceAlertVo> queryList(BlsIotDeviceAlertBo bo);

    /**
     * 新增设备告警（记录设备异常信息）
     *
     * @param bo 设备告警（记录设备异常信息）
     * @return 是否新增成功
     */
    Boolean insertByBo(BlsIotDeviceAlertBo bo);

    /**
     * 修改设备告警（记录设备异常信息）
     *
     * @param bo 设备告警（记录设备异常信息）
     * @return 是否修改成功
     */
    Boolean updateByBo(BlsIotDeviceAlertBo bo);

    /**
     * 校验并批量删除设备告警（记录设备异常信息）信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
