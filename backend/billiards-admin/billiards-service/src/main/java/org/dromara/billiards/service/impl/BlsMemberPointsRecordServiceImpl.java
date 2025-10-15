package org.dromara.billiards.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.dromara.billiards.common.constant.BilliardsConstants;
import org.dromara.billiards.domain.entity.BlsMemberPointsValidity;
import org.dromara.billiards.security.MerchantQueryHelper;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.dromara.billiards.domain.bo.BlsMemberPointsRecordBo;
import org.dromara.billiards.domain.vo.BlsMemberPointsRecordVo;
import org.dromara.billiards.domain.entity.BlsMemberPointsRecord;
import org.dromara.billiards.mapper.BlsMemberPointsRecordMapper;
import org.dromara.billiards.service.IBlsMemberPointsRecordService;

import java.util.List;
import java.util.Collection;

/**
 * 会员积分记录Service业务层处理
 *
 * @author banyue
 * @date 2025-06-17
 */
@RequiredArgsConstructor
@Service
@DS(BilliardsConstants.DS_BILLIARDS_PLATFORM)
public class BlsMemberPointsRecordServiceImpl extends ServiceImpl<BlsMemberPointsRecordMapper, BlsMemberPointsRecord> implements IBlsMemberPointsRecordService {

    /**
     * 查询会员积分记录
     *
     * @param id 主键
     * @return 会员积分记录
     */
    @Override
    public BlsMemberPointsRecordVo queryById(String id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询会员积分记录列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 会员积分记录分页列表
     */
    @Override
    public TableDataInfo<BlsMemberPointsRecordVo> queryPageList(BlsMemberPointsRecordBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BlsMemberPointsRecord> lqw = buildQueryWrapper(bo);
        Page<BlsMemberPointsRecordVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的会员积分记录列表
     *
     * @param bo 查询条件
     * @return 会员积分记录列表
     */
    @Override
    public List<BlsMemberPointsRecordVo> queryList(BlsMemberPointsRecordBo bo) {
        LambdaQueryWrapper<BlsMemberPointsRecord> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<BlsMemberPointsRecord> buildQueryWrapper(BlsMemberPointsRecordBo bo) {
        LambdaQueryWrapper<BlsMemberPointsRecord> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(BlsMemberPointsRecord::getId);
        lqw.eq(bo.getUserId() != null, BlsMemberPointsRecord::getUserId, bo.getUserId());
        lqw.eq(bo.getPoints() != null, BlsMemberPointsRecord::getPoints, bo.getPoints());
        lqw.eq(bo.getType() != null && bo.getType() > 0, BlsMemberPointsRecord::getType, bo.getType());
        lqw.eq(bo.getScene() != null, BlsMemberPointsRecord::getScene, bo.getScene());
        lqw.eq(StringUtils.isNotBlank(bo.getRuleId()), BlsMemberPointsRecord::getRuleId, bo.getRuleId());
        lqw.eq(StringUtils.isNotBlank(bo.getBusinessId()), BlsMemberPointsRecord::getBusinessId, bo.getBusinessId());
        lqw.eq(StringUtils.isNotBlank(bo.getDescription()), BlsMemberPointsRecord::getDescription, bo.getDescription());
        lqw.eq(bo.getExpireTime() != null, BlsMemberPointsRecord::getExpireTime, bo.getExpireTime());
        // 可选商户范围过滤
        MerchantQueryHelper.apply(lqw, BlsMemberPointsRecord::getMerchantId);
        return lqw;
    }

    /**
     * 新增会员积分记录
     *
     * @param bo 会员积分记录
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(BlsMemberPointsRecordBo bo) {
        BlsMemberPointsRecord add = MapstructUtils.convert(bo, BlsMemberPointsRecord.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改会员积分记录
     *
     * @param bo 会员积分记录
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(BlsMemberPointsRecordBo bo) {
        BlsMemberPointsRecord update = MapstructUtils.convert(bo, BlsMemberPointsRecord.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(BlsMemberPointsRecord entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除会员积分记录信息
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
