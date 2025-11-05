package org.dromara.billiards.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.dromara.billiards.domain.entity.BlsReservation;
import org.dromara.billiards.domain.vo.BlsReservationVo;
import org.dromara.billiards.domain.bo.BlsReservationBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 用户预约记录Service接口
 *
 * @author banyue
 * @date 2025-11-03
 */
public interface IBlsReservationService extends IService<BlsReservation> {

    /**
     * 查询用户预约记录
     *
     * @param id 主键
     * @return 用户预约记录
     */
    BlsReservationVo queryById(Long id);

    /**
     * 分页查询用户预约记录列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 用户预约记录分页列表
     */
    TableDataInfo<BlsReservationVo> queryPageList(BlsReservationBo bo, PageQuery pageQuery);


    /**
     * 分页查询用户预约记录列表（个人用户使用）
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 用户预约记录分页列表
     */
    IPage<BlsReservationVo> queryPage(BlsReservationBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的用户预约记录列表
     *
     * @param bo 查询条件
     * @return 用户预约记录列表
     */
    List<BlsReservationVo> queryList(BlsReservationBo bo);

    /**
     * 新增用户预约记录
     *
     * @param bo 用户预约记录
     * @return 是否新增成功
     */
    Boolean insertByBo(BlsReservationBo bo);

    /**
     * 修改用户预约记录
     *
     * @param bo 用户预约记录
     * @return 是否修改成功
     */
    Boolean updateByBo(BlsReservationBo bo);

    /**
     * 校验并批量删除用户预约记录信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    /**
     * 预约桌台
     * @param bo
     * @return
     */
    BlsReservationVo reserve(BlsReservationBo bo);

    /**
     * 获取用户进行中的预约记录
     */
    BlsReservationVo current();

    /**
     * 检查并标记过期的预约记录
     * 查询所有预约中状态且未签到的预约，如果开始时间 + 过期阈值 < 当前时间，则标记为已过期
     * 
     * @return 过期的预约数量
     */
    int checkAndExpireReservations();
}
