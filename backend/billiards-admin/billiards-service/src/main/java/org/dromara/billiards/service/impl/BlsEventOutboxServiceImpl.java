package org.dromara.billiards.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.dromara.billiards.common.constant.BilliardsConstants;
import org.dromara.billiards.domain.entity.BlsEventOutbox;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.dromara.billiards.domain.bo.BlsEventOutboxBo;
import org.dromara.billiards.domain.vo.BlsEventOutboxVo;
import org.dromara.billiards.mapper.BlsEventOutboxMapper;
import org.dromara.billiards.service.IBlsEventOutboxService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Collection;

/**
 * 本地消息(Outbox)Service业务层处理
 *
 * @author banyue
 * @date 2025-09-15
 */
@RequiredArgsConstructor
@Service
@DS(BilliardsConstants.DS_BILLIARDS_PLATFORM)
public class BlsEventOutboxServiceImpl extends ServiceImpl<BlsEventOutboxMapper, BlsEventOutbox> implements IBlsEventOutboxService {

    /**
     * 查询本地消息(Outbox)
     *
     * @param id 主键
     * @return 本地消息(Outbox)
     */
    @Override
    public BlsEventOutboxVo queryById(String id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询本地消息(Outbox)列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 本地消息(Outbox)分页列表
     */
    @Override
    public TableDataInfo<BlsEventOutboxVo> queryPageList(BlsEventOutboxBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BlsEventOutbox> lqw = buildQueryWrapper(bo);
        Page<BlsEventOutboxVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的本地消息(Outbox)列表
     *
     * @param bo 查询条件
     * @return 本地消息(Outbox)列表
     */
    @Override
    public List<BlsEventOutboxVo> queryList(BlsEventOutboxBo bo) {
        LambdaQueryWrapper<BlsEventOutbox> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<BlsEventOutbox> buildQueryWrapper(BlsEventOutboxBo bo) {
        LambdaQueryWrapper<BlsEventOutbox> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(BlsEventOutbox::getId);
        lqw.eq(StringUtils.isNotBlank(bo.getMerchantId()), BlsEventOutbox::getMerchantId, bo.getMerchantId());
        lqw.eq(StringUtils.isNotBlank(bo.getAggregateType()), BlsEventOutbox::getAggregateType, bo.getAggregateType());
        lqw.eq(StringUtils.isNotBlank(bo.getAggregateId()), BlsEventOutbox::getAggregateId, bo.getAggregateId());
        lqw.eq(StringUtils.isNotBlank(bo.getEventType()), BlsEventOutbox::getEventType, bo.getEventType());
        lqw.eq(StringUtils.isNotBlank(bo.getPayload()), BlsEventOutbox::getPayload, bo.getPayload());
        lqw.eq(bo.getStatus() != null, BlsEventOutbox::getStatus, bo.getStatus());
        lqw.eq(bo.getRetryCount() != null, BlsEventOutbox::getRetryCount, bo.getRetryCount());
        lqw.eq(bo.getNextRetryTime() != null, BlsEventOutbox::getNextRetryTime, bo.getNextRetryTime());
        lqw.eq(StringUtils.isNotBlank(bo.getLastError()), BlsEventOutbox::getLastError, bo.getLastError());
        lqw.eq(bo.getIsDelete() != null, BlsEventOutbox::getIsDelete, bo.getIsDelete());
        return lqw;
    }

    /**
     * 新增本地消息(Outbox)
     *
     * @param bo 本地消息(Outbox)
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(BlsEventOutboxBo bo) {
        BlsEventOutbox add = MapstructUtils.convert(bo, BlsEventOutbox.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改本地消息(Outbox)
     *
     * @param bo 本地消息(Outbox)
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(BlsEventOutboxBo bo) {
        BlsEventOutbox update = MapstructUtils.convert(bo, BlsEventOutbox.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(BlsEventOutbox entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除本地消息(Outbox)信息
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

    @Override
    public List<BlsEventOutbox> queryPendingMessages() {
        List<BlsEventOutbox> list = baseMapper.selectList(new LambdaQueryWrapper<BlsEventOutbox>()
            .in(BlsEventOutbox::getStatus, 0L, 2L)
            .isNull(BlsEventOutbox::getNextRetryTime)
            .last("limit 200"));

        list.addAll(baseMapper.selectList(new LambdaQueryWrapper<BlsEventOutbox>()
            .in(BlsEventOutbox::getStatus, 2L)
            .le(BlsEventOutbox::getNextRetryTime, LocalDateTime.now())
            .last("limit 200")));

        return list;
    }
}
