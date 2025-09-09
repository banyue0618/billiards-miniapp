package org.dromara.billiards.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.dromara.billiards.common.constant.BilliardsConstants;
import org.dromara.billiards.domain.entity.BlsTableUsage;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.dromara.billiards.domain.bo.BlsMemberUserBo;
import org.dromara.billiards.domain.vo.BlsMemberUserVo;
import org.dromara.billiards.domain.entity.BlsMemberUser;
import org.dromara.billiards.mapper.BlsMemberUserMapper;
import org.dromara.billiards.service.IBlsMemberUserService;

import java.util.List;
import java.util.Collection;

/**
 * 会员用户Service业务层处理
 *
 * @author banyue
 * @date 2025-06-17
 */
@RequiredArgsConstructor
@Service
@DS(BilliardsConstants.DS_BILLIARDS_PLATFORM)
public class BlsMemberUserServiceImpl implements IBlsMemberUserService {

    private final BlsMemberUserMapper baseMapper;

    /**
     * 查询会员用户
     *
     * @param id 主键
     * @return 会员用户
     */
    @Override
    public BlsMemberUserVo queryById(String id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询会员用户列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 会员用户分页列表
     */
    @Override
    public TableDataInfo<BlsMemberUserVo> queryPageList(BlsMemberUserBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BlsMemberUser> lqw = buildQueryWrapper(bo);
        Page<BlsMemberUserVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的会员用户列表
     *
     * @param bo 查询条件
     * @return 会员用户列表
     */
    @Override
    public List<BlsMemberUserVo> queryList(BlsMemberUserBo bo) {
        LambdaQueryWrapper<BlsMemberUser> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<BlsMemberUser> buildQueryWrapper(BlsMemberUserBo bo) {
        LambdaQueryWrapper<BlsMemberUser> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(BlsMemberUser::getId);
        lqw.eq(bo.getUserId() != null, BlsMemberUser::getUserId, bo.getUserId());
        lqw.eq(bo.getLevelCode() != null, BlsMemberUser::getLevelCode, bo.getLevelCode());
        lqw.eq(bo.getTotalAmount() != null, BlsMemberUser::getTotalAmount, bo.getTotalAmount());
        lqw.eq(bo.getPoints() != null, BlsMemberUser::getPoints, bo.getPoints());
        lqw.eq(bo.getMonthlyUsedMinutes() != null, BlsMemberUser::getMonthlyUsedMinutes, bo.getMonthlyUsedMinutes());
        lqw.eq(bo.getLevelExpireTime() != null, BlsMemberUser::getLevelExpireTime, bo.getLevelExpireTime());
        lqw.eq(bo.getLastConsumeTime() != null, BlsMemberUser::getLastConsumeTime, bo.getLastConsumeTime());
        lqw.eq(bo.getStatus() != null, BlsMemberUser::getStatus, bo.getStatus());
        return lqw;
    }

    /**
     * 新增会员用户
     *
     * @param bo 会员用户
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(BlsMemberUserBo bo) {
        BlsMemberUser add = MapstructUtils.convert(bo, BlsMemberUser.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改会员用户
     *
     * @param bo 会员用户
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(BlsMemberUserBo bo) {
        BlsMemberUser update = MapstructUtils.convert(bo, BlsMemberUser.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(BlsMemberUser entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除会员用户信息
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
