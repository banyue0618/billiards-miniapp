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
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.common.tenant.helper.TenantHelper;
import org.dromara.system.domain.SysTenant;
import org.dromara.system.domain.bo.SysTenantBo;
import org.dromara.system.mapper.SysTenantMapper;
import org.dromara.system.service.ISysTenantService;
import org.springframework.stereotype.Service;
import org.dromara.system.domain.bo.SysTenantApplyBo;
import org.dromara.system.domain.vo.SysTenantApplyVo;
import org.dromara.system.domain.SysTenantApply;
import org.dromara.system.mapper.SysTenantApplyMapper;
import org.dromara.system.service.ISysTenantApplyService;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Collection;

/**
 * 租户注册申请Service业务层处理
 *
 * @author banyue
 * @date 2025-09-01
 */
@RequiredArgsConstructor
@Service
public class SysTenantApplyServiceImpl implements ISysTenantApplyService {

    private final SysTenantApplyMapper baseMapper;
    private final ISysTenantService tenantService;

    /**
     * 查询租户注册申请
     *
     * @param id 主键
     * @return 租户注册申请
     */
    @Override
    public SysTenantApplyVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询租户注册申请列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 租户注册申请分页列表
     */
    @Override
    public TableDataInfo<SysTenantApplyVo> queryPageList(SysTenantApplyBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<SysTenantApply> lqw = buildQueryWrapper(bo);
        Page<SysTenantApplyVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的租户注册申请列表
     *
     * @param bo 查询条件
     * @return 租户注册申请列表
     */
    @Override
    public List<SysTenantApplyVo> queryList(SysTenantApplyBo bo) {
        LambdaQueryWrapper<SysTenantApply> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<SysTenantApply> buildQueryWrapper(SysTenantApplyBo bo) {
        LambdaQueryWrapper<SysTenantApply> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(SysTenantApply::getId);
        lqw.eq(StringUtils.isNotBlank(bo.getApplyNo()), SysTenantApply::getApplyNo, bo.getApplyNo());
        lqw.like(StringUtils.isNotBlank(bo.getCompanyName()), SysTenantApply::getCompanyName, bo.getCompanyName());
        lqw.eq(StringUtils.isNotBlank(bo.getCreditCode()), SysTenantApply::getCreditCode, bo.getCreditCode());
        lqw.like(StringUtils.isNotBlank(bo.getContactName()), SysTenantApply::getContactName, bo.getContactName());
        lqw.eq(StringUtils.isNotBlank(bo.getContactPhone()), SysTenantApply::getContactPhone, bo.getContactPhone());
        lqw.eq(StringUtils.isNotBlank(bo.getContactEmail()), SysTenantApply::getContactEmail, bo.getContactEmail());
        lqw.eq(StringUtils.isNotBlank(bo.getProvince()), SysTenantApply::getProvince, bo.getProvince());
        lqw.eq(StringUtils.isNotBlank(bo.getCity()), SysTenantApply::getCity, bo.getCity());
        lqw.eq(StringUtils.isNotBlank(bo.getDistrict()), SysTenantApply::getDistrict, bo.getDistrict());
        lqw.eq(StringUtils.isNotBlank(bo.getAddress()), SysTenantApply::getAddress, bo.getAddress());
        lqw.eq(StringUtils.isNotBlank(bo.getBusinessLicense()), SysTenantApply::getBusinessLicense, bo.getBusinessLicense());
        lqw.eq(bo.getPackageId() != null, SysTenantApply::getPackageId, bo.getPackageId());
        lqw.eq(bo.getExpectedUsers() != null, SysTenantApply::getExpectedUsers, bo.getExpectedUsers());
        lqw.eq(bo.getStatus() != null, SysTenantApply::getStatus, bo.getStatus());
        lqw.eq(bo.getAuditBy() != null, SysTenantApply::getAuditBy, bo.getAuditBy());
        lqw.eq(bo.getAuditTime() != null, SysTenantApply::getAuditTime, bo.getAuditTime());
        lqw.eq(StringUtils.isNotBlank(bo.getAuditReason()), SysTenantApply::getAuditReason, bo.getAuditReason());
        return lqw;
    }

    /**
     * 新增租户注册申请
     *
     * @param bo 租户注册申请
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(SysTenantApplyBo bo) {
        SysTenantApply add = MapstructUtils.convert(bo, SysTenantApply.class);
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
     * 修改租户注册申请
     *
     * @param bo 租户注册申请
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(SysTenantApplyBo bo) {
        SysTenantApply update = MapstructUtils.convert(bo, SysTenantApply.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(SysTenantApply entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除租户注册申请信息
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
    public void approve(Long id, SysTenantApplyBo applyBo) {
        SysTenantApply apply = baseMapper.selectById(id);
        if (apply == null) throw new ServiceException("申请不存在");
        if (apply.getStatus() != null && apply.getStatus() != 0) throw new ServiceException("该申请已处理");

        // 组装租户Bo（最小必需集）
        SysTenantBo bo = new SysTenantBo();
        bo.setCompanyName(apply.getCompanyName());
        bo.setContactUserName(apply.getContactName());
        bo.setContactPhone(apply.getContactPhone());
        bo.setPackageId(applyBo.getPackageId());
        // 生成初始账号
        String username = apply.getContactPhone();
        bo.setUsername(username);
        bo.setPassword("123456");

        // 创建租户（忽略当前租户上下文）
        TenantHelper.ignore(() -> tenantService.insertByBo(bo));

        // 更新申请状态
        SysTenantApply upd = new SysTenantApply();
        upd.setId(id);
        upd.setStatus(1L);
        upd.setAuditBy(LoginHelper.getUserId());
        upd.setAuditTime(DateUtil.date());
        upd.setAuditReason(applyBo.getAuditReason());
        baseMapper.updateById(upd);

        // todo 异步发送消息，短信通知申请人等操作
    }

    @Override
    public void reject(Long id, String reason) {
        SysTenantApply apply = baseMapper.selectById(id);
        if (apply == null) throw new ServiceException("申请不存在");
        if (apply.getStatus() != null && apply.getStatus() != 0) throw new ServiceException("该申请已处理");
        SysTenantApply upd = new SysTenantApply();
        upd.setId(id);
        upd.setStatus(2L);
        upd.setAuditBy(LoginHelper.getUserId());
        upd.setAuditTime(DateUtil.date());
        upd.setAuditReason(reason);
        baseMapper.updateById(upd);
    }
}
