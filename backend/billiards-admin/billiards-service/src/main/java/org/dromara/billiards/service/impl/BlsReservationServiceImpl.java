package org.dromara.billiards.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.linpeilie.Converter;
import jakarta.annotation.Resource;
import org.dromara.billiards.common.constant.BilliardsConstants;
import org.dromara.billiards.common.constant.ReservationStatusEnum;
import org.dromara.billiards.common.constant.TableStatusEnum;
import org.dromara.billiards.common.exception.BilliardsException;
import org.dromara.billiards.common.result.ResultCode;
import org.dromara.billiards.common.utils.ReservationNumberGenerator;
import org.dromara.billiards.domain.entity.BlsReservation;
import org.dromara.billiards.domain.entity.BlsTable;
import org.dromara.billiards.domain.vo.BlsReservationVo;
import org.dromara.billiards.security.MerchantQueryHelper;
import org.dromara.billiards.service.StoreService;
import org.dromara.billiards.service.TableService;
import org.dromara.common.core.utils.DateUtils;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.dromara.billiards.domain.bo.BlsReservationBo;
import org.dromara.billiards.mapper.BlsReservationMapper;
import org.dromara.billiards.service.IBlsReservationService;
import org.dromara.common.satoken.utils.LoginHelper;
import com.baomidou.lock.annotation.Lock4j;
import org.dromara.billiards.service.ReservationConfigService;
import org.dromara.billiards.config.BlsReserveConfig;
import org.dromara.common.tenant.helper.TenantHelper;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Collection;

/**
 * 用户预约记录Service业务层处理
 *
 * @author banyue
 * @date 2025-11-03
 */
@Service
@DS(BilliardsConstants.DS_BILLIARDS_PLATFORM)
@Slf4j
public class BlsReservationServiceImpl  extends ServiceImpl<BlsReservationMapper, BlsReservation> implements IBlsReservationService{

    @Lazy
    @Resource
    private TableService tableService;

    @Lazy
    @Resource
    private StoreService storeService;

    @Resource
    private Converter converter;

    @Lazy
    @Resource
    private ReservationConfigService reservationConfigService;

    /**
     * 查询用户预约记录
     *
     * @param id 主键
     * @return 用户预约记录
     */
    @Override
    public BlsReservationVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询用户预约记录列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 用户预约记录分页列表
     */
    @Override
    public TableDataInfo<BlsReservationVo> queryPageList(BlsReservationBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BlsReservation> lqw = buildQueryWrapper(bo);
        Page<BlsReservationVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    public IPage<BlsReservationVo> queryPage(BlsReservationBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BlsReservation> lqw = buildQueryWrapper(bo);
        // 如果已登录
        if (LoginHelper.isLogin()) {
            lqw.eq(BlsReservation::getUserId, LoginHelper.getUserId());
        }
        return baseMapper.selectVoPage(pageQuery.build(), lqw);
    }

    /**
     * 查询符合条件的用户预约记录列表
     *
     * @param bo 查询条件
     * @return 用户预约记录列表
     */
    @Override
    public List<BlsReservationVo> queryList(BlsReservationBo bo) {
        LambdaQueryWrapper<BlsReservation> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<BlsReservation> buildQueryWrapper(BlsReservationBo bo) {
        LambdaQueryWrapper<BlsReservation> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(BlsReservation::getId);
        lqw.eq(StringUtils.isNotBlank(bo.getReservationNo()), BlsReservation::getReservationNo, bo.getReservationNo());
        lqw.eq(bo.getStartTime() != null, BlsReservation::getStartTime, bo.getStartTime());
        lqw.eq(bo.getEndTime() != null, BlsReservation::getEndTime, bo.getEndTime());
        lqw.eq(bo.getStatus() != null, BlsReservation::getStatus, bo.getStatus());
        lqw.eq(bo.getPayStatus() != null, BlsReservation::getPayStatus, bo.getPayStatus());

        // 可选商户范围过滤
        MerchantQueryHelper.apply(lqw, BlsReservation::getMerchantId);
        return lqw;
    }

    /**
     * 新增用户预约记录
     *
     * @param bo 用户预约记录
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(BlsReservationBo bo) {
        BlsReservation add = MapstructUtils.convert(bo, BlsReservation.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改用户预约记录
     *
     * @param bo 用户预约记录
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(BlsReservationBo bo) {
        BlsReservation update = MapstructUtils.convert(bo, BlsReservation.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(BlsReservation entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除用户预约记录信息
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

    /**
     * 预约桌台
     * 使用分布式锁确保同一桌台同一时间段只能被一个用户预约
     * 锁的key为 table:{tableId}，确保同一桌台的操作串行化
     *
     * @param bo
     * @return
     */
    @Override
    @Lock4j(keys = {"'table:' + #bo.tableId"}, acquireTimeout = 3000, expire = 10000)
    @Transactional(rollbackFor = Exception.class)
    public BlsReservationVo reserve(BlsReservationBo bo) {
        // 查询桌台门店信息
        BlsTable blsTable = tableService.getById(bo.getTableId());
        if(blsTable == null){
            throw BilliardsException.of(ResultCode.TABLE_NOT_EXIST);
        }
        if(blsTable.getStatus() != TableStatusEnum.FREE.getCode() && blsTable.getStatus() != TableStatusEnum.RESERVED.getCode()){
            throw BilliardsException.of(ResultCode.TABLE_OCCUPIED);
        }
        // 检查用户是否存在进行中的预约，如果存在，不允许预约
        LambdaQueryWrapper<BlsReservation> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(BlsReservation::getUserId, LoginHelper.getUserId());
        queryWrapper.eq(BlsReservation::getStatus, ReservationStatusEnum.PENDING.getCode());
        long existingCount = count(queryWrapper);
        if (existingCount > 0) {
            throw BilliardsException.of(ResultCode.USER_HAS_PENDING_RESERVATION);
        }

        // 检查用户当日预约次数、历史爽约次数、是否被封禁；(暂不实现)

        // 判断当前桌台预约时间段不重叠
        checkTimeConflict(bo.getTableId(), bo.getStartTime(), bo.getEndTime(), null);

        // 创建预约记录
        BlsReservation reservation = new BlsReservation();
        reservation.setReservationNo(ReservationNumberGenerator.generate());
        reservation.setUserId(LoginHelper.getUserId());
        reservation.setStoreId(blsTable.getStoreId());
        reservation.setTableId(bo.getTableId());
        reservation.setTableNumber(blsTable.getTableNumber());
        reservation.setStoreName(storeService.getById(blsTable.getStoreId()).getName());
        reservation.setMerchantId(blsTable.getMerchantId());
        reservation.setTenantId(blsTable.getTenantId());
        reservation.setStartTime(bo.getStartTime());
        reservation.setEndTime(bo.getEndTime());
        reservation.setStatus(ReservationStatusEnum.PENDING.getCode());
        reservation.setPayStatus(0L); // 未支付

        // 保存预约记录
        if (!save(reservation)) {
            throw BilliardsException.of(ResultCode.ERROR);
        }

        // TODO: 微信消息通知

        return baseMapper.selectVoById(reservation.getId());
    }

    @Override
    public BlsReservationVo current() {
        LambdaQueryWrapper<BlsReservation> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(BlsReservation::getUserId, LoginHelper.getUserId());
        queryWrapper.eq(BlsReservation::getStatus, ReservationStatusEnum.PENDING.getCode());
        BlsReservation reservation = getOne(queryWrapper);
        return converter.convert(reservation, BlsReservationVo.class);
    }

    /**
     * 检查时间段是否冲突
     * @param tableId 桌台ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param excludeId 排除的预约ID（用于更新时排除自己）
     */
    private void checkTimeConflict(String tableId, LocalDateTime startTime, LocalDateTime endTime, Long excludeId) {
        LambdaQueryWrapper<BlsReservation> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(BlsReservation::getTableId, tableId);
        // 只检查预约中的记录
        queryWrapper.eq(BlsReservation::getStatus, ReservationStatusEnum.PENDING.getCode());
        // 排除指定ID（用于更新时排除自己）
        if (excludeId != null) {
            queryWrapper.ne(BlsReservation::getId, excludeId);
        }

        // 检查时间段重叠：新预约的开始时间在已有预约的时间段内，或新预约的结束时间在已有预约的时间段内
        // 或新预约包含已有预约（新开始时间<=已有开始时间 且 新结束时间>=已有结束时间）
        queryWrapper.and(wrapper -> wrapper
            .or(w -> w.le(BlsReservation::getStartTime, startTime).ge(BlsReservation::getEndTime, startTime))
            .or(w -> w.le(BlsReservation::getStartTime, endTime).ge(BlsReservation::getEndTime, endTime))
            .or(w -> w.ge(BlsReservation::getStartTime, startTime).le(BlsReservation::getEndTime, endTime))
        );

        long count = count(queryWrapper);
        if (count > 0) {
            throw BilliardsException.of(ResultCode.RESERVATION_TIME_CONFLICT);
        }
    }

    /**
     * 查询桌台在指定时间点之后最近的预约记录
     * @param tableId 桌台ID
     * @param afterTime 指定时间点（可选，如果为null则查询当前时间之后的预约）
     * @return 预约记录，如果没有则返回null
     */
    public BlsReservation findUpcomingReservation(String tableId, LocalDateTime afterTime) {
        if (afterTime == null) {
            afterTime = LocalDateTime.now();
        }

        LambdaQueryWrapper<BlsReservation> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(BlsReservation::getTableId, tableId);
        queryWrapper.eq(BlsReservation::getStatus, ReservationStatusEnum.PENDING.getCode());
        queryWrapper.ge(BlsReservation::getStartTime, DateUtils.toDate(afterTime));
        queryWrapper.orderByAsc(BlsReservation::getStartTime);
        queryWrapper.last("LIMIT 1");

        return getOne(queryWrapper);
    }

    /**
     * 取消预约（线下扫码优先级更高时使用）
     * @param reservationId 预约ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void cancelReservation(Long reservationId) {
        BlsReservation reservation = getById(reservationId);
        if (reservation == null) {
            throw BilliardsException.of(ResultCode.RESERVATION_NOT_EXIST);
        }
        if (reservation.getStatus() != ReservationStatusEnum.PENDING.getCode()) {
            // 只有预约中状态才能取消
            return;
        }
        reservation.setStatus(ReservationStatusEnum.CANCELLED.getCode());
        reservation.setCancelTime(new Date());
        updateById(reservation);
    }

    /**
     * 检查并标记过期的预约记录
     * 查询所有预约中状态且未签到的预约，如果开始时间 + 过期阈值 < 当前时间，则标记为已过期
     *
     * @return 过期的预约数量
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int checkAndExpireReservations() {
        LocalDateTime now = LocalDateTime.now();
        int totalExpiredCount = 0;

        // 查询所有预约中状态且未签到的预约记录
        LambdaQueryWrapper<BlsReservation> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(BlsReservation::getStatus, ReservationStatusEnum.PENDING.getCode());
        queryWrapper.isNull(BlsReservation::getCheckInTime); // 未签到
        queryWrapper.le(BlsReservation::getStartTime, now); // 开始时间 <= 当前时间

        List<BlsReservation> pendingReservations = list(queryWrapper);

        if (pendingReservations.isEmpty()) {
            return 0;
        }

        // 按租户分组处理，因为每个租户的过期配置可能不同
        for (BlsReservation reservation : pendingReservations) {
            try {
                // 切换到对应租户上下文获取配置
                String tenantId = reservation.getTenantId();
                BlsReserveConfig config = getReservationConfig(tenantId);

                // 计算过期时间点：开始时间 + 过期阈值
                // reservation.getStartTime() 返回 LocalDateTime
                LocalDateTime startTime = reservation.getStartTime();
                LocalDateTime expireTime = startTime.plusMinutes(config.getExpireAfterStartMinutes());

                // 如果当前时间已经超过过期时间点，则标记为已过期
                if (now.isAfter(expireTime) || now.isEqual(expireTime)) {
                    reservation.setStatus(ReservationStatusEnum.EXPIRED.getCode());
                    reservation.setRemark("系统自动标记为已过期");
                    updateById(reservation);
                    totalExpiredCount++;

                    // TODO: 如果启用了退款策略，可以在这里处理退款逻辑
                    // 如果预约有支付定金，根据退款策略决定是否退款
                }
            } catch (Exception e) {
                // 记录异常但继续处理其他预约
                log.error("处理预约过期失败，预约ID: {}, 租户ID: {}", reservation.getId(), reservation.getTenantId(), e);
            }
        }

        if (totalExpiredCount > 0) {
            log.info("预约过期检查完成，共标记 {} 条预约为已过期", totalExpiredCount);
        }

        return totalExpiredCount;
    }

    /**
     * 获取预约配置（独立方法，不参与事务，确保数据源切换生效）
     * 注意：此方法不使用事务，避免与调用方的事务冲突导致数据源切换失效
     *
     * @param tenantId 租户ID
     * @return 预约配置
     */
    private BlsReserveConfig getReservationConfig(String tenantId) {
        // 直接调用配置服务，不使用事务传播
        // ReservationConfigServiceImpl 类上已标注 @DS(ADMIN)，会切换到 admin 数据源
        return reservationConfigService.getConfig(tenantId);
    }
}
