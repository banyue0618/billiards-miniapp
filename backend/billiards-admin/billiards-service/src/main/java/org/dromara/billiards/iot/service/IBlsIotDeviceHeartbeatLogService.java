package org.dromara.billiards.iot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.dromara.billiards.iot.domain.BlsIotDeviceHeartbeatLog;
import org.dromara.billiards.iot.domain.vo.BlsIotDeviceHeartbeatLogVo;
import org.dromara.billiards.iot.domain.bo.BlsIotDeviceHeartbeatLogBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * IoT设备心跳记录Service接口
 *
 * @author banyue
 * @date 2025-10-22
 */
public interface IBlsIotDeviceHeartbeatLogService extends IService<BlsIotDeviceHeartbeatLog> {

    /**
     * 查询IoT设备心跳记录
     *
     * @param id 主键
     * @return IoT设备心跳记录
     */
    BlsIotDeviceHeartbeatLogVo queryById(Long id);

    /**
     * 分页查询IoT设备心跳记录列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return IoT设备心跳记录分页列表
     */
    TableDataInfo<BlsIotDeviceHeartbeatLogVo> queryPageList(BlsIotDeviceHeartbeatLogBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的IoT设备心跳记录列表
     *
     * @param bo 查询条件
     * @return IoT设备心跳记录列表
     */
    List<BlsIotDeviceHeartbeatLogVo> queryList(BlsIotDeviceHeartbeatLogBo bo);

    /**
     * 新增IoT设备心跳记录
     *
     * @param bo IoT设备心跳记录
     * @return 是否新增成功
     */
    Boolean insertByBo(BlsIotDeviceHeartbeatLogBo bo);

    /**
     * 修改IoT设备心跳记录
     *
     * @param bo IoT设备心跳记录
     * @return 是否修改成功
     */
    Boolean updateByBo(BlsIotDeviceHeartbeatLogBo bo);

    /**
     * 校验并批量删除IoT设备心跳记录信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
