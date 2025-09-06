package org.dromara.system.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.common.tenant.helper.TenantHelper;
import org.dromara.system.domain.SysMerchant;
import org.dromara.system.domain.bo.SysMerchantBo;
import org.dromara.system.mapper.SysMerchantMapper;
import org.dromara.system.service.ISysMerchantService;
import org.dromara.system.domain.bo.SysMerchantApplyBo;
import org.dromara.system.domain.vo.SysMerchantApplyVo;
import org.dromara.system.domain.SysMerchantApply;
import org.dromara.system.mapper.SysMerchantApplyMapper;
import org.dromara.system.service.ISysMerchantApplyService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 商户注册申请Service业务层处理
 *
 * @author banyue
 * @date 2025-09-01
 */
@RequiredArgsConstructor
@Service
public class SysMerchantApplyServiceImpl implements ISysMerchantApplyService {

    private final SysMerchantApplyMapper baseMapper;
    private final ISysMerchantService merchantService;

    /**
     * 查询商户注册申请
     *
     * @param id 主键
     * @return 商户注册申请
     */
    @Override
    public SysMerchantApplyVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询商户注册申请列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 商户注册申请分页列表
     */
    @Override
    public TableDataInfo<SysMerchantApplyVo> queryPageList(SysMerchantApplyBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<SysMerchantApply> lqw = buildQueryWrapper(bo);
        Page<SysMerchantApplyVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的商户注册申请列表
     *
     * @param bo 查询条件
     * @return 商户注册申请列表
     */
    @Override
    public List<SysMerchantApplyVo> queryList(SysMerchantApplyBo bo) {
        LambdaQueryWrapper<SysMerchantApply> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<SysMerchantApply> buildQueryWrapper(SysMerchantApplyBo bo) {
        LambdaQueryWrapper<SysMerchantApply> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(SysMerchantApply::getId);
        lqw.eq(StringUtils.isNotBlank(bo.getApplyNo()), SysMerchantApply::getApplyNo, bo.getApplyNo());
        lqw.like(StringUtils.isNotBlank(bo.getName()), SysMerchantApply::getName, bo.getName());
        lqw.eq(StringUtils.isNotBlank(bo.getWxMchId()), SysMerchantApply::getWxMchId, bo.getWxMchId());
        lqw.like(StringUtils.isNotBlank(bo.getContactName()), SysMerchantApply::getContactName, bo.getContactName());
        lqw.eq(StringUtils.isNotBlank(bo.getContactPhone()), SysMerchantApply::getContactPhone, bo.getContactPhone());
        lqw.eq(StringUtils.isNotBlank(bo.getContactEmail()), SysMerchantApply::getContactEmail, bo.getContactEmail());
        lqw.eq(StringUtils.isNotBlank(bo.getProvince()), SysMerchantApply::getProvince, bo.getProvince());
        lqw.eq(StringUtils.isNotBlank(bo.getCity()), SysMerchantApply::getCity, bo.getCity());
        lqw.eq(StringUtils.isNotBlank(bo.getDistrict()), SysMerchantApply::getDistrict, bo.getDistrict());
        lqw.eq(StringUtils.isNotBlank(bo.getAddress()), SysMerchantApply::getAddress, bo.getAddress());
        lqw.eq(StringUtils.isNotBlank(bo.getLogo()), SysMerchantApply::getLogo, bo.getLogo());
        lqw.eq(StringUtils.isNotBlank(bo.getBusinessLicense()), SysMerchantApply::getBusinessLicense, bo.getBusinessLicense());
        lqw.like(StringUtils.isNotBlank(bo.getBankAccountName()), SysMerchantApply::getBankAccountName, bo.getBankAccountName());
        lqw.eq(StringUtils.isNotBlank(bo.getBankAccountNo()), SysMerchantApply::getBankAccountNo, bo.getBankAccountNo());
        lqw.like(StringUtils.isNotBlank(bo.getBankName()), SysMerchantApply::getBankName, bo.getBankName());
        lqw.eq(bo.getStatus() != null, SysMerchantApply::getStatus, bo.getStatus());
        lqw.eq(bo.getAuditBy() != null, SysMerchantApply::getAuditBy, bo.getAuditBy());
        lqw.eq(bo.getAuditTime() != null, SysMerchantApply::getAuditTime, bo.getAuditTime());
        lqw.eq(StringUtils.isNotBlank(bo.getAuditReason()), SysMerchantApply::getAuditReason, bo.getAuditReason());
        return lqw;
    }

    /**
     * 新增商户注册申请
     *
     * @param bo 商户注册申请
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(SysMerchantApplyBo bo) {
        SysMerchantApply add = MapstructUtils.convert(bo, SysMerchantApply.class);
        validEntityBeforeSave(add);
        long l = IdUtil.getSnowflake().nextId();
        add.setApplyNo(String.valueOf(l));
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改商户注册申请
     *
     * @param bo 商户注册申请
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(SysMerchantApplyBo bo) {
        SysMerchantApply update = MapstructUtils.convert(bo, SysMerchantApply.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(SysMerchantApply entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除商户注册申请信息
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
    public void approve(Long id, SysMerchantApplyBo applyBo) {
        SysMerchantApply apply = baseMapper.selectById(id);
        if (apply == null) throw new ServiceException("申请不存在");
        if (apply.getStatus() != null && apply.getStatus() != 0) throw new ServiceException("该申请已处理");
        if (org.apache.commons.lang3.StringUtils.isBlank(apply.getTenantId())) throw new ServiceException("缺少租户标识");

        SysMerchantBo bo = new SysMerchantBo();
        bo.setName(apply.getName());
        bo.setLogo(apply.getLogo());
        bo.setContactName(apply.getContactName());
        bo.setContactPhone(apply.getContactPhone());

        // 在对应租户上下文创建商户
        TenantHelper.dynamic(apply.getTenantId(), () -> merchantService.insertByBo(bo));

        SysMerchantApply upd = new SysMerchantApply();
        upd.setId(id);
        upd.setStatus(1L);
        upd.setAuditBy(LoginHelper.getUserId());
        upd.setAuditReason(applyBo.getAuditReason());
        baseMapper.updateById(upd);
    }

    @Override
    public void reject(Long id, String reason) {
        SysMerchantApply apply = baseMapper.selectById(id);
        if (apply == null) throw new ServiceException("申请不存在");
        if (apply.getStatus() != null && apply.getStatus() != 0) throw new ServiceException("该申请已处理");
        SysMerchantApply upd = new SysMerchantApply();
        upd.setId(id);
        upd.setStatus(2L);
        upd.setAuditBy(LoginHelper.getUserId());
        upd.setAuditTime(DateUtil.date());
        upd.setAuditReason(reason);
        baseMapper.updateById(upd);
    }
}
