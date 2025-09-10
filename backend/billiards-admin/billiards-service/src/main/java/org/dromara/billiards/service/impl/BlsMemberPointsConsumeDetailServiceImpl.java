package org.dromara.billiards.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.dromara.billiards.domain.entity.BlsMemberPointsConsumeDetail;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.dromara.billiards.domain.bo.BlsMemberPointsConsumeDetailBo;
import org.dromara.billiards.domain.vo.BlsMemberPointsConsumeDetailVo;
import org.dromara.billiards.mapper.BlsMemberPointsConsumeDetailMapper;
import org.dromara.billiards.service.IBlsMemberPointsConsumeDetailService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 会员积分消费详情Service业务层处理
 *
 * @author banyue
 * @date 2025-09-09
 */
@RequiredArgsConstructor
@Service
public class BlsMemberPointsConsumeDetailServiceImpl extends ServiceImpl<BlsMemberPointsConsumeDetailMapper, BlsMemberPointsConsumeDetail> implements IBlsMemberPointsConsumeDetailService {

    /**
     * 查询会员积分消费详情
     *
     * @param id 主键
     * @return 会员积分消费详情
     */
    @Override
    public BlsMemberPointsConsumeDetailVo queryById(String id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询会员积分消费详情列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 会员积分消费详情分页列表
     */
    @Override
    public TableDataInfo<BlsMemberPointsConsumeDetailVo> queryPageList(BlsMemberPointsConsumeDetailBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BlsMemberPointsConsumeDetail> lqw = buildQueryWrapper(bo);
        Page<BlsMemberPointsConsumeDetailVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的会员积分消费详情列表
     *
     * @param bo 查询条件
     * @return 会员积分消费详情列表
     */
    @Override
    public List<BlsMemberPointsConsumeDetailVo> queryList(BlsMemberPointsConsumeDetailBo bo) {
        LambdaQueryWrapper<BlsMemberPointsConsumeDetail> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<BlsMemberPointsConsumeDetail> buildQueryWrapper(BlsMemberPointsConsumeDetailBo bo) {
        LambdaQueryWrapper<BlsMemberPointsConsumeDetail> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(BlsMemberPointsConsumeDetail::getId);
        return lqw;
    }

    /**
     * 新增会员积分消费详情
     *
     * @param bo 会员积分消费详情
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(BlsMemberPointsConsumeDetailBo bo) {
        BlsMemberPointsConsumeDetail add = MapstructUtils.convert(bo, BlsMemberPointsConsumeDetail.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改会员积分消费详情
     *
     * @param bo 会员积分消费详情
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(BlsMemberPointsConsumeDetailBo bo) {
        BlsMemberPointsConsumeDetail update = MapstructUtils.convert(bo, BlsMemberPointsConsumeDetail.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(BlsMemberPointsConsumeDetail entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除会员积分消费详情信息
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
