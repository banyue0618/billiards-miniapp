package org.dromara.billiards.iot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.dromara.billiards.iot.domain.BlsIotDevice;
import org.dromara.billiards.iot.domain.bo.BlsIotDeviceBo;
import org.dromara.billiards.iot.domain.vo.BlsIotDeviceVo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * IoT设备Service接口
 *
 * @author banyue
 * @date 2025-10-16
 */
public interface IBlsIotDeviceService extends IService<BlsIotDevice> {

    /**
     * 查询IoT设备
     *
     * @param id 主键
     * @return IoT设备
     */
    BlsIotDeviceVo queryById(Long id);

    /**
     * 分页查询IoT设备列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return IoT设备分页列表
     */
    TableDataInfo<BlsIotDeviceVo> queryPageList(BlsIotDeviceBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的IoT设备列表
     *
     * @param bo 查询条件
     * @return IoT设备列表
     */
    List<BlsIotDeviceVo> queryList(BlsIotDeviceBo bo);

    /**
     * 新增IoT设备
     *
     * @param bo IoT设备
     * @return 是否新增成功
     */
    Boolean insertByBo(BlsIotDeviceBo bo);

    /**
     * 修改IoT设备
     *
     * @param bo IoT设备
     * @return 是否修改成功
     */
    Boolean updateByBo(BlsIotDeviceBo bo);

    /**
     * 校验并批量删除IoT设备信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);


    /**
     * 根据设备编码查询设备
     * @param deviceCode 设备编码
     * @return 设备信息
     */
    BlsIotDevice queryByDeviceCode(String deviceCode);
}
