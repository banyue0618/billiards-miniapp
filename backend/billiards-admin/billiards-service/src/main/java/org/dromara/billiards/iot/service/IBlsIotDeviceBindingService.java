package org.dromara.billiards.iot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.dromara.billiards.iot.domain.BlsIotDeviceBinding;
import org.dromara.billiards.iot.domain.bo.BlsIotDeviceBindingBo;
import org.dromara.billiards.iot.domain.vo.BlsIotDeviceBindingVo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 设备业务绑定（定义场景与设备动作映射）Service接口
 *
 * @author banyue
 * @date 2025-10-16
 */
public interface IBlsIotDeviceBindingService extends IService<BlsIotDeviceBinding> {

    /**
     * 查询设备业务绑定（定义场景与设备动作映射）
     *
     * @param id 主键
     * @return 设备业务绑定（定义场景与设备动作映射）
     */
    BlsIotDeviceBindingVo queryById(Long id);

    /**
     * 分页查询设备业务绑定（定义场景与设备动作映射）列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 设备业务绑定（定义场景与设备动作映射）分页列表
     */
    TableDataInfo<BlsIotDeviceBindingVo> queryPageList(BlsIotDeviceBindingBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的设备业务绑定（定义场景与设备动作映射）列表
     *
     * @param bo 查询条件
     * @return 设备业务绑定（定义场景与设备动作映射）列表
     */
    List<BlsIotDeviceBindingVo> queryList(BlsIotDeviceBindingBo bo);

    /**
     * 新增设备业务绑定（定义场景与设备动作映射）
     *
     * @param bo 设备业务绑定（定义场景与设备动作映射）
     * @return 是否新增成功
     */
    Boolean insertByBo(BlsIotDeviceBindingBo bo);

    /**
     * 修改设备业务绑定（定义场景与设备动作映射）
     *
     * @param bo 设备业务绑定（定义场景与设备动作映射）
     * @return 是否修改成功
     */
    Boolean updateByBo(BlsIotDeviceBindingBo bo);

    /**
     * 校验并批量删除设备业务绑定（定义场景与设备动作映射）信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    /**
     * 根据台桌ID和场景查询绑定的设备动作列表
     * @param tableId
     * @param scene
     * @return
     */
    List<BlsIotDeviceBinding> selectByTableIdAndScene(String tableId, String scene);
}
