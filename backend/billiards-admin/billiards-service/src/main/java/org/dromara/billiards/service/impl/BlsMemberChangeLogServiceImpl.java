package org.dromara.billiards.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.dromara.billiards.common.constant.BilliardsConstants;
import org.dromara.billiards.domain.entity.BlsMemberChangeLog;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.dromara.billiards.domain.bo.BlsMemberChangeLogBo;
import org.dromara.billiards.domain.vo.BlsMemberChangeLogVo;
import org.dromara.billiards.mapper.BlsMemberChangeLogMapper;
import org.dromara.billiards.service.IBlsMemberChangeLogService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 会员变更记录Service业务层处理
 *
 * @author banyue
 * @date 2025-09-15
 */
@RequiredArgsConstructor
@Service
@DS(BilliardsConstants.DS_BILLIARDS_PLATFORM)
public class BlsMemberChangeLogServiceImpl extends ServiceImpl<BlsMemberChangeLogMapper, BlsMemberChangeLog> implements IBlsMemberChangeLogService  {

    /**
     * 查询会员变更记录
     *
     * @param id 主键
     * @return 会员变更记录
     */
    @Override
    public BlsMemberChangeLogVo queryById(String id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询会员变更记录列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 会员变更记录分页列表
     */
    @Override
    public TableDataInfo<BlsMemberChangeLogVo> queryPageList(BlsMemberChangeLogBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BlsMemberChangeLog> lqw = buildQueryWrapper(bo);
        Page<BlsMemberChangeLogVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的会员变更记录列表
     *
     * @param bo 查询条件
     * @return 会员变更记录列表
     */
    @Override
    public List<BlsMemberChangeLogVo> queryList(BlsMemberChangeLogBo bo) {
        LambdaQueryWrapper<BlsMemberChangeLog> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<BlsMemberChangeLog> buildQueryWrapper(BlsMemberChangeLogBo bo) {
        LambdaQueryWrapper<BlsMemberChangeLog> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(BlsMemberChangeLog::getId);
        lqw.eq(bo.getUserId() != null, BlsMemberChangeLog::getUserId, bo.getUserId());
        lqw.eq(StringUtils.isNotBlank(bo.getChangeType()), BlsMemberChangeLog::getChangeType, bo.getChangeType());
        lqw.eq(StringUtils.isNotBlank(bo.getBeforeLevel()), BlsMemberChangeLog::getBeforeLevel, bo.getBeforeLevel());
        lqw.eq(StringUtils.isNotBlank(bo.getAfterLevel()), BlsMemberChangeLog::getAfterLevel, bo.getAfterLevel());
        lqw.eq(bo.getBeforeExpire() != null, BlsMemberChangeLog::getBeforeExpire, bo.getBeforeExpire());
        lqw.eq(bo.getAfterExpire() != null, BlsMemberChangeLog::getAfterExpire, bo.getAfterExpire());
        lqw.eq(StringUtils.isNotBlank(bo.getOrderId()), BlsMemberChangeLog::getOrderId, bo.getOrderId());
        return lqw;
    }

    /**
     * 新增会员变更记录
     *
     * @param bo 会员变更记录
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(BlsMemberChangeLogBo bo) {
        BlsMemberChangeLog add = MapstructUtils.convert(bo, BlsMemberChangeLog.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改会员变更记录
     *
     * @param bo 会员变更记录
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(BlsMemberChangeLogBo bo) {
        BlsMemberChangeLog update = MapstructUtils.convert(bo, BlsMemberChangeLog.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(BlsMemberChangeLog entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除会员变更记录信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteByIds(ids) > 0;
    }
}
