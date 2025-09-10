package org.dromara.billiards.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.dromara.billiards.common.constant.BilliardsConstants;
import org.dromara.billiards.domain.bo.BlsUserTenantBo;
import org.dromara.billiards.domain.entity.BlsUser;
import org.dromara.billiards.domain.entity.BlsUserTenant;
import org.dromara.billiards.domain.vo.BlsUserTenantVo;
import org.dromara.billiards.mapper.BlsUserTenantMapper;
import org.dromara.billiards.service.IBlsUserTenantService;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 平台用户-租户映射Service业务层处理
 *
 * @author banyue
 * @date 2025-08-28
 */
@RequiredArgsConstructor
@Service
@DS(BilliardsConstants.DS_BILLIARDS_PLATFORM)
public class BlsUserTenantServiceImpl implements IBlsUserTenantService {

    private final BlsUserTenantMapper baseMapper;

    /**
     * 查询平台用户-租户映射
     *
     * @param id 主键
     * @return 平台用户-租户映射
     */
    @Override
    public BlsUserTenantVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询平台用户-租户映射列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 平台用户-租户映射分页列表
     */
    @Override
    public TableDataInfo<BlsUserTenantVo> queryPageList(BlsUserTenantBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BlsUserTenant> lqw = buildQueryWrapper(bo);
        Page<BlsUserTenantVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的平台用户-租户映射列表
     *
     * @param bo 查询条件
     * @return 平台用户-租户映射列表
     */
    @Override
    public List<BlsUserTenantVo> queryList(BlsUserTenantBo bo) {
        LambdaQueryWrapper<BlsUserTenant> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<BlsUserTenant> buildQueryWrapper(BlsUserTenantBo bo) {
        LambdaQueryWrapper<BlsUserTenant> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(BlsUserTenant::getId);
        lqw.eq(bo.getUserId() != null, BlsUserTenant::getUserId, bo.getUserId());
        lqw.like(StringUtils.isNotBlank(bo.getTenantUserName()), BlsUserTenant::getTenantUserName, bo.getTenantUserName());
        lqw.eq(StringUtils.isNotBlank(bo.getTenantPhone()), BlsUserTenant::getTenantPhone, bo.getTenantPhone());
        lqw.eq(StringUtils.isNotBlank(bo.getRole()), BlsUserTenant::getRole, bo.getRole());
        lqw.eq(bo.getIsMember() != null, BlsUserTenant::getIsMember, bo.getIsMember());
        lqw.eq(bo.getStatus() != null, BlsUserTenant::getStatus, bo.getStatus());
        lqw.eq(bo.getFirstTime() != null, BlsUserTenant::getFirstTime, bo.getFirstTime());
        lqw.eq(bo.getLastTime() != null, BlsUserTenant::getLastTime, bo.getLastTime());
        return lqw;
    }

    /**
     * 新增平台用户-租户映射
     *
     * @param bo 平台用户-租户映射
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(BlsUserTenantBo bo) {
        BlsUserTenant add = MapstructUtils.convert(bo, BlsUserTenant.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改平台用户-租户映射
     *
     * @param bo 平台用户-租户映射
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(BlsUserTenantBo bo) {
        BlsUserTenant update = MapstructUtils.convert(bo, BlsUserTenant.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(BlsUserTenant entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除平台用户-租户映射信息
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

    @Override
    public Boolean saveUserTenantRecord(BlsUser blsUser) {

        if (blsUser == null || blsUser.getId() == null) {
            return false;
        }
        BlsUserTenantBo bo = new BlsUserTenantBo();
        bo.setUserId(blsUser.getId());
        List<BlsUserTenantVo> existingRecords = this.queryList(bo);
        if (existingRecords != null && !existingRecords.isEmpty()) {
            // 已存在租户记录，无需重复添加
            return true;
        }
        // 新增租户记录
        BlsUserTenantBo newRecord = new BlsUserTenantBo();
        newRecord.setUserId(blsUser.getId());
        newRecord.setTenantUserName(blsUser.getNickname());
        newRecord.setTenantPhone(blsUser.getPhone());
        newRecord.setRole("USER"); // 默认角色为USER
        newRecord.setIsMember(0L); // 默认非会员
        newRecord.setStatus(0L); // 默认状态为正常
        newRecord.setFirstTime(LocalDateTime.now());
        newRecord.setLastTime(LocalDateTime.now());
        return this.insertByBo(newRecord);
    }
}
