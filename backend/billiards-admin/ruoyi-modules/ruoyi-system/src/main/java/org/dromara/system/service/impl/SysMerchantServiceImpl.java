package org.dromara.system.service.impl;

import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.dromara.system.domain.bo.SysMerchantBo;
import org.dromara.system.domain.vo.SysMerchantVo;
import org.dromara.system.domain.SysMerchant;
import org.dromara.system.mapper.SysMerchantMapper;
import org.dromara.system.service.ISysMerchantService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 商户Service业务层处理
 *
 * @author banyue
 * @date 2025-09-01
 */
@RequiredArgsConstructor
@Service
public class SysMerchantServiceImpl implements ISysMerchantService {

    private final SysMerchantMapper baseMapper;

    /**
     * 查询商户
     *
     * @param id 主键
     * @return 商户
     */
    @Override
    public SysMerchantVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询商户列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 商户分页列表
     */
    @Override
    public TableDataInfo<SysMerchantVo> queryPageList(SysMerchantBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<SysMerchant> lqw = buildQueryWrapper(bo);
        Page<SysMerchantVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的商户列表
     *
     * @param bo 查询条件
     * @return 商户列表
     */
    @Override
    public List<SysMerchantVo> queryList(SysMerchantBo bo) {
        LambdaQueryWrapper<SysMerchant> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<SysMerchant> buildQueryWrapper(SysMerchantBo bo) {
        LambdaQueryWrapper<SysMerchant> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(SysMerchant::getId);
        lqw.eq(StringUtils.isNotBlank(bo.getWxMchId()), SysMerchant::getWxMchId, bo.getWxMchId());
        lqw.like(StringUtils.isNotBlank(bo.getName()), SysMerchant::getName, bo.getName());
        lqw.eq(StringUtils.isNotBlank(bo.getLogo()), SysMerchant::getLogo, bo.getLogo());
        lqw.like(StringUtils.isNotBlank(bo.getContactName()), SysMerchant::getContactName, bo.getContactName());
        lqw.eq(StringUtils.isNotBlank(bo.getContactPhone()), SysMerchant::getContactPhone, bo.getContactPhone());
        lqw.eq(bo.getStatus() != null, SysMerchant::getStatus, bo.getStatus());
        lqw.eq(bo.getIsDelete() != null, SysMerchant::getIsDelete, bo.getIsDelete());
        return lqw;
    }

    /**
     * 新增商户
     *
     * @param bo 商户
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(SysMerchantBo bo) {
        SysMerchant add = MapstructUtils.convert(bo, SysMerchant.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改商户
     *
     * @param bo 商户
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(SysMerchantBo bo) {
        SysMerchant update = MapstructUtils.convert(bo, SysMerchant.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(SysMerchant entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除商户信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteByIds(ids) > 0;
    }
}
