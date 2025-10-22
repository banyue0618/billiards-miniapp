package org.dromara.billiards.iot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.dromara.billiards.iot.domain.BlsIotControlLog;
import org.dromara.billiards.iot.domain.bo.BlsIotControlLogBo;
import org.dromara.billiards.iot.domain.vo.BlsIotControlLogVo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 设备控制日志（记录执行命令历史）Service接口
 *
 * @author banyue
 * @date 2025-10-16
 */
public interface IBlsIotControlLogService extends IService<BlsIotControlLog> {

    /**
     * 查询设备控制日志（记录执行命令历史）
     *
     * @param id 主键
     * @return 设备控制日志（记录执行命令历史）
     */
    BlsIotControlLogVo queryById(Long id);

    /**
     * 分页查询设备控制日志（记录执行命令历史）列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 设备控制日志（记录执行命令历史）分页列表
     */
    TableDataInfo<BlsIotControlLogVo> queryPageList(BlsIotControlLogBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的设备控制日志（记录执行命令历史）列表
     *
     * @param bo 查询条件
     * @return 设备控制日志（记录执行命令历史）列表
     */
    List<BlsIotControlLogVo> queryList(BlsIotControlLogBo bo);

    /**
     * 新增设备控制日志（记录执行命令历史）
     *
     * @param bo 设备控制日志（记录执行命令历史）
     * @return 是否新增成功
     */
    Boolean insertByBo(BlsIotControlLogBo bo);

    /**
     * 修改设备控制日志（记录执行命令历史）
     *
     * @param bo 设备控制日志（记录执行命令历史）
     * @return 是否修改成功
     */
    Boolean updateByBo(BlsIotControlLogBo bo);

    /**
     * 校验并批量删除设备控制日志（记录执行命令历史）信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
