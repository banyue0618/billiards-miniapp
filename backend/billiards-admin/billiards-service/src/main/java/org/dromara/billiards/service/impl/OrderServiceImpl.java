package org.dromara.billiards.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.github.binarywang.wxpay.bean.result.WxPayRefundQueryV3Result;
import org.dromara.billiards.common.constant.BilliardsConstants;
import org.dromara.billiards.common.constant.OrderChannelEnum;
import org.dromara.billiards.common.constant.OrderCompleteFlagEnum;
import org.dromara.billiards.common.exception.BilliardsException;
import org.dromara.billiards.common.result.ResultCode;
import org.dromara.billiards.common.utils.OrderNumberGenerator;
import org.dromara.billiards.convert.OrderConvert;
import org.dromara.billiards.domain.bo.OrderUpdateDto;
import org.dromara.billiards.iot.service.IoTOrchestrationService;
import org.dromara.billiards.listener.event.OrderCompletedEvent;
import org.dromara.billiards.domain.bo.BlsEventOutboxBo;
import org.dromara.billiards.common.constant.AggregateTypeEnum;
import org.dromara.billiards.common.constant.OutboxEventTypeEnum;
import org.dromara.billiards.listener.event.RefundRequestedEvent;
import org.dromara.billiards.mapper.OrderMapper;
import org.dromara.billiards.domain.entity.*;
import org.dromara.billiards.domain.vo.OrderVO;
import org.dromara.billiards.service.*;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.dromara.billiards.domain.bo.OrderQueryRequest;
import org.dromara.billiards.service.pricing.PricingResult;
import org.dromara.billiards.service.pricing.PricingStrategy;
import org.dromara.billiards.service.pricing.PricingStrategyFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dromara.billiards.security.MerchantQueryHelper;
import org.dromara.billiards.security.MerchantWriteGuard;
import org.dromara.common.satoken.utils.LoginHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
@DS(BilliardsConstants.DS_BILLIARDS_PLATFORM)
public class OrderServiceImpl extends ServiceImpl<OrderMapper, BlsOrder> implements OrderService {

    private final PriceRuleService priceRuleService;
    private final PricingStrategyFactory pricingStrategyFactory;
    private final IBlsPayRecordService payRecordService;
    private final IBlsRefundRecordService refundRecordService;
    private final StoreService storeService;
    private final TableService tableService;
    private final IBlsWalletAccountService walletAccountService;
    private final IBlsTableUsageService blsTableUsageService;
    private final IBlsMemberUserService memberUserService;
    private final ApplicationEventPublisher eventPublisher;
    private final IBlsEventOutboxService eventOutboxService;
    private final ObjectMapper objectMapper;
    private final OrderConvert orderConvert = OrderConvert.INSTANCE;

    private final IoTOrchestrationService ioTOrchestrationService;

    @Override
    public IPage<BlsOrder> pageAdminOrders(OrderQueryRequest request) {
        Page<BlsOrder> pageParam = new Page<>(request.getPageNum(), request.getPageSize());
        LambdaQueryWrapper<BlsOrder> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.like(StringUtils.isNotBlank(request.getOrderNo()), BlsOrder::getOrderNo, request.getOrderNo());
        queryWrapper.eq(request.getUserId() != null, BlsOrder::getUserId, request.getUserId());
        queryWrapper.eq(StringUtils.isNotBlank(request.getStoreId()), BlsOrder::getStoreId, request.getStoreId());
        queryWrapper.eq(request.getStatus() != null, BlsOrder::getStatus, request.getStatus());

        if (request.getStartTime() != null && request.getEndTime() != null) {
            queryWrapper.between(BlsOrder::getCreateTime, request.getStartTime(), request.getEndTime());
        } else if (request.getStartTime() != null) {
            queryWrapper.ge(BlsOrder::getCreateTime, request.getStartTime());
        } else if (request.getEndTime() != null) {
            queryWrapper.le(BlsOrder::getCreateTime, request.getEndTime());
        }
        // 可选商户范围过滤
        MerchantQueryHelper.apply(queryWrapper, BlsOrder::getMerchantId);
        queryWrapper.orderByDesc(BlsOrder::getCreateTime);

        Page<BlsOrder> page = this.page(pageParam, queryWrapper);

        if (CollectionUtil.isNotEmpty(page.getRecords())) {
            // 流式计算实时金额，并返回vo对象
            page.setRecords(page.getRecords().stream().map(order -> {
                if(order.getStatus() == 1){
                    return order; // 已完成订单不计算
                }
                PricingResult pricingResult = calculateAmount(order.getStartTime(), order.getEndTime(), order.getPriceRuleId(), false);
                fillOrderResult(order, pricingResult);
                return order;
            }).toList());
        }
        return page;
    }
    /**
     * 手动结束订单，防止意外情况发生
     *
     * @param orderId   订单ID
     * @param updateDto
     * @return 是否成功，如果操作失败或订单未找到则抛出异常
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean endAdminOrder(String orderId, OrderUpdateDto updateDto) {
        BlsOrder blsOrder = this.getById(orderId);
        if (blsOrder == null) {
            throw BilliardsException.of(ResultCode.ORDER_NOT_EXIST);
        }
        // 商户越权校验
        MerchantWriteGuard.assertWritable(blsOrder.getMerchantId());
        if (blsOrder.getStatus() != null && blsOrder.getStatus() != 0) {
            throw BilliardsException.of(ResultCode.ORDER_ALREADY_ENDED);
        }
        blsOrder.setCompleteFlag(OrderCompleteFlagEnum.ADMIN_END.getCode()); // 设置管理员结束标志
        blsOrder.setRemark(updateDto.getRemark());
        endOrder(blsOrder);
        return true;
    }

    /**
     * 管理员取消订单
     * @param orderId 订单ID
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelAdminOrder(String orderId) {
        BlsOrder blsOrder = this.getById(orderId);
        if (blsOrder == null) {
            throw BilliardsException.of(ResultCode.ORDER_NOT_EXIST);
        }
        // 商户越权校验
        MerchantWriteGuard.assertWritable(blsOrder.getMerchantId());
        if (blsOrder.getStatus() != null && blsOrder.getStatus() == 2) {
             throw BilliardsException.of(ResultCode.ORDER_STATUS_ERROR);
        }

//        order.setStatus(2);
        blsOrder.setEndTime(LocalDateTime.now());
        boolean success = this.updateById(blsOrder);
        if (!success) {
            throw BilliardsException.of(ResultCode.ERROR);
        }
        // 释放桌台
        tableService.releaseTable(blsOrder.getTableId());
        return true;
    }

    /**
     * 创建订单
     * @param tableId 桌台ID
     * @return 订单对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BlsOrder createOrder(String tableId, String channel) {
        // 限定渠道
        if(OrderChannelEnum.fromString(channel) == null) {
            throw BilliardsException.of(ResultCode.INVALID_CHANNEL);
        }

        // 检查用户是否有进行中的订单
        List<OrderVO> existingOrder = getCurrentOrder(LoginHelper.getUserId());
        if (CollectionUtil.isNotEmpty(existingOrder)) {
            throw BilliardsException.of(ResultCode.ORDER_IN_PROGRESS);
        }

        // 获取桌台信息
        BlsTable blsTable = tableService.lockTable(tableId); // 锁定桌台，防止其他用户同时使用

        // 创建订单
        BlsOrder blsOrder = this.orderInit(blsTable);
        blsOrder.setChannel(channel);

        // 保存订单
        if (!save(blsOrder)) {
            throw BilliardsException.of(ResultCode.ERROR);
        }

        // 生成桌台使用记录
        if(!blsTableUsageService.saveTableUsage(blsOrder)){
            throw BilliardsException.of(ResultCode.ERROR);
        }

        // 触发物联网开台场景
        ioTOrchestrationService.openTable(blsTable.getId(), blsOrder.getId());

        return blsOrder;
    }

    /**
     * 获取用户当前进行中的订单
     * @param userId 用户ID
     * @return 订单对象，如果没有则返回null
     */
    @Override
    public List<OrderVO> getCurrentOrder(Long userId) {
        return baseMapper.selectInProgressByUserId(userId);
    }

    /**
     * 计算订单实时金额
     * @param orderId 订单ID
     * @return 当前金额
     */
    @Override
    public BlsOrder calculateCurrentAmount(String orderId) {
        // 获取订单信息
        BlsOrder blsOrder = getById(orderId);
        if (blsOrder == null) {
            throw BilliardsException.of(ResultCode.ORDER_NOT_EXIST);
        }
        PricingResult result = calculateAmount(blsOrder.getStartTime(), null, blsOrder.getPriceRuleId(), LoginHelper.isMember());
        fillOrderResult(blsOrder, result);
        return blsOrder;
    }

    /**
     * 判断桌台是否被占用
     * @param tableId 桌台ID
     * @return 是否被占用
     */
    @Override
    public boolean isTableOccupied(String tableId) {
        OrderVO order = baseMapper.selectInProgressByTableId(tableId);
        return order != null;
    }

    @Override
    public OrderVO getUserOrderDetail(String orderId) {
        BlsOrder blsOrder = getById(orderId);
        // 如果订单不存在，抛出异常
        if (blsOrder == null) {
            throw BilliardsException.of(ResultCode.ORDER_NOT_EXIST);
        }
        // 校验订单属于是否当前用户
        // 如果订单的用户ID与当前登录用户ID不一致，抛出权限异常
        if (!LoginHelper.getUserId().equals(blsOrder.getUserId())) {
            throw BilliardsException.of(ResultCode.FORBIDDEN);
        }
        OrderVO orderVO = orderConvert.toVo(blsOrder);
        // 如果是进行中订单，计算实时金额
        if (blsOrder.getStatus() != null && blsOrder.getStatus() == 0 && blsOrder.getStartTime() != null) {
            PricingResult pricingResult = calculateAmount(blsOrder.getStartTime(), null, blsOrder.getPriceRuleId(), LoginHelper.isMember());
            // 更新VO对象
            fillOrderVO(orderVO, pricingResult);
        }
        return orderVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BlsOrder endUserOrder(String orderId) {
        BlsOrder blsOrder = this.getById(orderId);
        if (blsOrder == null) {
            throw BilliardsException.of(ResultCode.ORDER_NOT_EXIST);
        }
        if (LoginHelper.getUserId() != blsOrder.getUserId()) {
            throw BilliardsException.of(ResultCode.FORBIDDEN);
        }
        blsOrder.setCompleteFlag(OrderCompleteFlagEnum.USER_END.getCode()); // 设置用户结束标志
        return endOrder(blsOrder); // Return the updated order
    }

    @Override
    public IPage<BlsOrder> listUserOrders(OrderQueryRequest queryRequest) {
        queryRequest.setUserId(LoginHelper.getUserId());
        Page<BlsOrder> pageParam = new Page<>(queryRequest.getPageNum(), queryRequest.getPageSize());
        LambdaQueryWrapper<BlsOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BlsOrder::getUserId, queryRequest.getUserId());

        queryWrapper.eq(queryRequest.getStatus() != null, BlsOrder::getStatus, queryRequest.getStatus());
        if (queryRequest.getStartTime() != null && queryRequest.getEndTime() != null) {
            queryWrapper.between(BlsOrder::getCreateTime, queryRequest.getStartTime(), queryRequest.getEndTime());
        } else if (queryRequest.getStartTime() != null) {
            queryWrapper.ge(BlsOrder::getCreateTime, queryRequest.getStartTime());
        } else if (queryRequest.getEndTime() != null) {
            queryWrapper.le(BlsOrder::getCreateTime, queryRequest.getEndTime());
        }

        queryWrapper.orderByDesc(BlsOrder::getCreateTime);
        return this.page(pageParam, queryWrapper);
    }

    @Override
    public List<OrderVO> getCurrentOrderWithCalculation() {
        List<OrderVO> orderVOS = this.getCurrentOrder(LoginHelper.getUserId());
        if (CollectionUtil.isNotEmpty(orderVOS)) {
            // 计算金额
            for (OrderVO orderVO : orderVOS) {
                // 计算实时金额
                PricingResult pricingResult = calculateAmount(orderVO.getStartTime(), null, orderVO.getPriceRuleId(), LoginHelper.isMember());
                // 更新VO对象
                fillOrderVO(orderVO, pricingResult);
            }
        }
        return orderVOS;
    }

    @Override
    public BlsOrder getRefundingOrder() {
        // 找出当前用户退款中的订单，只有一条
        List<BlsOrder> blsOrders = this.list(new LambdaQueryWrapper<BlsOrder>()
                .eq(BlsOrder::getUserId, LoginHelper.getUserIdStr())
                .eq(BlsOrder::getPaymentStatus, 2) // 2 for refunding
                .orderByDesc(BlsOrder::getCreateTime)
                .last("LIMIT 1"));
        if (CollectionUtil.isNotEmpty(blsOrders)) {
            return blsOrders.get(0);
        }
        return null;
    }

    @Override
    public BlsOrder completeOrder(String orderId) {
        BlsOrder blsOrder = this.getById(orderId);
        if (blsOrder == null) {
            throw BilliardsException.of(ResultCode.ORDER_NOT_EXIST);
        }
        if(blsOrder.getPaymentStatus() != 2){
            throw BilliardsException.of(ResultCode.ORDER_NOT_REFUNDING);
        }
        blsOrder.setPaymentStatus(1);
        // 更新订单状态
        if (!this.updateById(blsOrder)) {
            throw BilliardsException.of(ResultCode.ERROR);
        }
        return blsOrder;
    }

    @Override
    public List<BlsOrder> listOngoingOrders() {
        // 查询所有进行中的订单
        return list(new LambdaQueryWrapper<BlsOrder>()
                .eq(BlsOrder::getStatus, 0) // 0 for in progress
                .orderByDesc(BlsOrder::getCreateTime));
    }

    @Override
    @Transactional
    public void detectOrders() {
        List<BlsOrder> blsOrders = listOngoingOrders();
        // 计算使用费用，如果消费金额达到一定额度，则发出提醒，告知用户余额不足，如果余额为0，直接结束当前订单
        for (BlsOrder blsOrder : blsOrders) {
            // todo 获取用户会员标识
            // 计算当前订单的实时金额
            PricingResult result = calculateAmount(blsOrder.getStartTime(), null, blsOrder.getPriceRuleId(), false);

            // 获取当前用户的余额
            BigDecimal userBalance = walletAccountService.getWalletAccountByUserId(blsOrder.getUserId()).getBalance();

            // 余额不足预警提醒的阈值
            BigDecimal warningThreshold = new BigDecimal("10.00"); // 例如10元

            // 计算消费金额与余额的差值。
            BigDecimal balanceDifference = userBalance.subtract(result.getActualAmount());

            // 判断是否达到临界点，如果已经在临界点范围之内，则发出提醒。 如果差值为0，表示用户余额使用完毕
            // 自动结束订单是否使用积分
            if (balanceDifference.compareTo(BigDecimal.ZERO) <= 0) {
                // 用户余额不足，结束订单
                blsOrder.setRemark("用户余额不足，定时任务自动结束订单");
                blsOrder.setCompleteFlag(OrderCompleteFlagEnum.TIMEOUT_END.getCode()); // 设置系统结束标志
                endOrder(blsOrder);
                log.warn("User {} has insufficient balance, ending order {}", blsOrder.getUserId(), blsOrder.getOrderNo());
                continue;
            }
            // 如果余额差值小于等于预警阈值，发出余额不足预警提醒
            if (balanceDifference.compareTo(warningThreshold) <= 0) {
                // 余额不足预警提醒
                log.warn("User {} is approaching low balance threshold, current balance: {}, order: {}",
                    blsOrder.getUserId(), userBalance, blsOrder.getOrderNo());
                // todo 给用户发出提醒
            }
        }
        log.info("Order detection completed, processed {} ongoing orders.", blsOrders.size());
    }

    @Override
    public OrderVO getOrderDetail(String orderId) {
        OrderVO order = baseMapper.selectOrderDetail(orderId);
        if (order == null) {
            throw BilliardsException.of(ResultCode.ORDER_NOT_EXIST);
        }
        // todo 获取用户会员标识
        // 如果是进行中订单，计算实时金额
        PricingResult pricingResult = calculateAmount(order.getStartTime(), order.getEndTime(), order.getPriceRuleId(), false);
        // 更新VO对象
        fillOrderVO(order, pricingResult);
        return order;
    }


    private BlsOrder orderInit(BlsTable blsTable){
        BlsOrder blsOrder = new BlsOrder();
        blsOrder.setOrderNo(OrderNumberGenerator.generate());
        blsOrder.setUserId(LoginHelper.getUserId());
        blsOrder.setStoreId(blsTable.getStoreId());
        blsOrder.setStoreName(storeService.getById(blsTable.getStoreId()).getName());
        blsOrder.setTableId(blsTable.getId());
        blsOrder.setTableNumber(blsTable.getTableNumber());
        blsOrder.setPriceRuleId(blsTable.getPriceRuleId());
        blsOrder.setStartTime(LocalDateTime.now());
        blsOrder.setDuration(0);
        blsOrder.setOriginalAmount(BigDecimal.ZERO);
        blsOrder.setDiscountAmount(BigDecimal.ZERO);
        blsOrder.setActualAmount(BigDecimal.ZERO);
        blsOrder.setPaymentStatus(0);
        blsOrder.setStatus(0);
        return blsOrder;
    }

    /**
     * 用户结束计费，订单的支付状态从未支付变成已支付、订单的状态从进行中变成已完成。
     * 如果需要退款，则支付状态变成退款中、订单状态变成已结束
     * 待退款回调完成，变成已支付、订单也变成已完成
     * @param blsOrder
     * @return
     */

    @Transactional(rollbackFor = Exception.class)
    @Override
    public BlsOrder endOrder(BlsOrder blsOrder){
        if (blsOrder.getStatus() != null && blsOrder.getStatus() != 0) {
            throw BilliardsException.of(ResultCode.ORDER_ALREADY_ENDED);
        }
        // todo 获取用户会员标识
        // 计算消费额
        PricingResult result = calculateAmount(blsOrder.getStartTime(), null, blsOrder.getPriceRuleId(), false);

        //默认按照100积分抵扣一元自动抵扣。
        Long pointsToDeduct = result.getActualAmount().multiply(BigDecimal.valueOf(100)).setScale(0, BigDecimal.ROUND_FLOOR).longValue();
        BigDecimal deducted = memberUserService.deductPointsFifo(blsOrder.getUserId(), blsOrder.getMerchantId(), blsOrder.getId(), pointsToDeduct, 1L, blsOrder.getPriceRuleId(), null);
        if(deducted.compareTo(BigDecimal.ZERO) > 0){
            // 如果抵扣成功，更新实际支付金额
            result.setDiscountAmount(result.getDiscountAmount().add(deducted));
            result.setActualAmount(result.getActualAmount().subtract(deducted).max(BigDecimal.ZERO));
        }

        // 更新订单信息
        fillOrderResult(blsOrder, result);
        blsOrder.setStatus(1); // 1 for completed

        // 解锁桌台
        if(!tableService.releaseTable(blsOrder.getTableId())){
            throw BilliardsException.of(ResultCode.ERROR);
        }

        // 记录桌台结束时间
        if(!blsTableUsageService.trackTableUsage(blsOrder)){
            throw BilliardsException.of(ResultCode.ERROR);
        }

        // 扣费操作，返回退款金额
        final BigDecimal refundAmount = walletAccountService.deductBalance(blsOrder.getUserId(), result.getActualAmount());

        // 标记支付状态（退款中或已支付）
        if(refundAmount.compareTo(BigDecimal.ZERO) > 0){
            blsOrder.setPaymentStatus(2); // 退款中
        } else {
            blsOrder.setPaymentStatus(1); // 已支付
        }
        this.updateById(blsOrder);

        // 事务内写 Outbox，状态为 0（NEW）
        // 事务提交后立即发布事件（快速路径），定时任务作为兜底重试机制
        // 注意：OutboxHelper 支持处理两种状态（0-NEW 和 3-PROCESSING），保证幂等性
        try {
            BlsEventOutboxBo completed = new BlsEventOutboxBo();
            completed.setMerchantId(blsOrder.getMerchantId());
            completed.setTenantId(blsOrder.getTenantId());
            completed.setAggregateType(AggregateTypeEnum.ORDER.name());
            completed.setAggregateId(blsOrder.getId());
            completed.setEventType(OutboxEventTypeEnum.ORDER_COMPLETED.name());
            Map<String,Object> completedPayload = new HashMap<>();
            completedPayload.put("orderId", blsOrder.getId());
            completedPayload.put("userId", blsOrder.getUserId());
            completedPayload.put("merchantId", blsOrder.getMerchantId());
            completedPayload.put("actualAmount", blsOrder.getActualAmount());
            completed.setPayload(objectMapper.writeValueAsString(completedPayload));
            completed.setStatus(0L); // 0=NEW，事务提交后立即处理
            completed.setRetryCount(0L);
            eventOutboxService.insertByBo(completed);

            if (refundAmount.compareTo(BigDecimal.ZERO) > 0) {
                BlsPayRecord lastPay = payRecordService.getLastPayRecord(blsOrder.getUserId());
                BlsEventOutboxBo refund = new BlsEventOutboxBo();
                refund.setMerchantId(blsOrder.getMerchantId());
                refund.setTenantId(blsOrder.getTenantId());
                refund.setAggregateType(AggregateTypeEnum.ORDER.name());
                refund.setAggregateId(blsOrder.getId());
                refund.setEventType(OutboxEventTypeEnum.REFUND_REQUESTED.name());
                Map<String,Object> refundPayload = new HashMap<>();
                refundPayload.put("orderId", blsOrder.getId());
                refundPayload.put("refundAmount", refundAmount);
                refundPayload.put("lastPayRecordId", lastPay.getId());
                refund.setPayload(objectMapper.writeValueAsString(refundPayload));
                refund.setStatus(0L); // 0=NEW，事务提交后立即处理
                refund.setRetryCount(0L);
                eventOutboxService.insertByBo(refund);
            }
        } catch (Exception e) {
            log.error("write outbox failed orderId={}, err=", blsOrder.getId(), e);
            throw BilliardsException.of(ResultCode.ERROR);
        }

        // 【双通道投递】事务提交后立即发布事件（快速路径）
        // 如果处理失败或应用崩溃，定时任务会扫描状态=0的记录进行重试
        // 业务层通过唯一索引保证幂等性，即使重复发布也不会导致数据问题
        eventPublisher.publishEvent(new OrderCompletedEvent(this, blsOrder));
        if (refundAmount.compareTo(BigDecimal.ZERO) > 0) {
            BlsPayRecord lastPay = payRecordService.getLastPayRecord(blsOrder.getUserId());
            eventPublisher.publishEvent(new RefundRequestedEvent(this, blsOrder, refundAmount, lastPay.getId()));
        }

        // 关灯
        ioTOrchestrationService.closeTable(blsOrder.getTableId(), blsOrder.getId());

        return blsOrder;
    }

    @Override
    public void orderRefundByAdmin(String orderId) {
        // 根据订单找到退款失败的记录
        BlsOrder blsOrder = this.getById(orderId);
        if(blsOrder ==null){
            throw BilliardsException.of(ResultCode.ORDER_NOT_EXIST);
        }
        // 商户越权校验
        MerchantWriteGuard.assertWritable(blsOrder.getMerchantId());
        if(blsOrder.getPaymentStatus() != 2){
            throw BilliardsException.of(ResultCode.ORDER_NOT_REFUNDING);
        }

        try {
            // 查询订单对应的退款记录
            BlsRefundRecord refundRecord = refundRecordService.queryRecordByOrderId(orderId);

            // 主动查询退款结果
            WxPayRefundQueryV3Result refundResult = refundRecordService.queryRefundResult(refundRecord.getId());

            // 处理查询结果
            refundRecordService.handleRefundResult(refundResult.getStatus(), refundRecord, true);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<OrderVO> listOngoingOrders(String storeId) {
        LambdaQueryWrapper<BlsOrder> queryWrapper = new LambdaQueryWrapper<BlsOrder>()
            .eq(BlsOrder::getStatus, 0) // 0 for in progress
            .eq(StringUtils.isNotBlank(storeId), BlsOrder::getStoreId, storeId)
            .orderByDesc(BlsOrder::getCreateTime);
        MerchantQueryHelper.apply(queryWrapper, BlsOrder::getMerchantId);
        List<BlsOrder> orders = this.list(queryWrapper);

        // 流式计算实时金额，并返回vo对象
        if (CollectionUtil.isNotEmpty(orders)) {
            return orders.stream().map(order -> {
                PricingResult pricingResult = calculateAmount(order.getStartTime(), null, order.getPriceRuleId(), false);
                OrderVO orderVO = orderConvert.toVo(order);
                fillOrderVO(orderVO, pricingResult);
                return orderVO;
            }).toList();
        }
        return orderConvert.toVoList(orders);
    }

    @Override
    public void changeOrderAmount(String orderId, OrderUpdateDto updateDto) {
        BlsOrder blsOrder = this.getById(orderId);
        if(blsOrder == null){
            throw BilliardsException.of(ResultCode.ORDER_NOT_EXIST);
        }
        // 商户越权校验
        MerchantWriteGuard.assertWritable(blsOrder.getMerchantId());
        if(blsOrder.getStatus() != null && blsOrder.getStatus() != 0){
            throw BilliardsException.of(ResultCode.ORDER_ALREADY_ENDED);
        }
        blsOrder.setActualAmount(updateDto.getAmount());
        blsOrder.setRemark(updateDto.getRemark());
        if(!this.updateById(blsOrder)){
            throw BilliardsException.of(ResultCode.ERROR);
        }

    }

    private PricingResult calculateAmount(LocalDateTime startTime, LocalDateTime endTime, String priceRuleId, boolean isMember){
        // 获取计费规则
        BlsPriceRule blsPriceRule = priceRuleService.getById(priceRuleId);

        // 计算当前使用时长
        endTime = endTime == null ? LocalDateTime.now() : endTime;

        // 判断开始时间不能大于结束时间
        if (startTime.isAfter(endTime)) {
            throw BilliardsException.of(ResultCode.INVALID_TIME_RANGE);
        }

        Duration duration = Duration.between(startTime, endTime);
        int minutes = (int) duration.toMinutes();

        // 确保达到最低消费时长
        minutes = Math.max(minutes, blsPriceRule.getMinMinutes());

        // 例如: isMember = userService.checkMemberStatus(order.getUserId());

        // 使用策略模式计算费用
        PricingStrategy strategy = pricingStrategyFactory.getStrategy(blsPriceRule.getRuleType());
        PricingResult result = strategy.calculatePrice(null, blsPriceRule, minutes, isMember);
        result.setEndTime(endTime);
        result.setStartTime(startTime);
        result.setDuration(minutes);
        return result;
    }

    private void fillOrderVO(OrderVO orderVO, PricingResult pricingResult){
        orderVO.setOriginalAmount(pricingResult.getOriginalAmount());
        orderVO.setDiscountAmount(pricingResult.getDiscountAmount());
        orderVO.setActualAmount(pricingResult.getActualAmount());
        orderVO.setDuration(pricingResult.getDuration());

        orderVO.setPriceUnit(pricingResult.getPriceUnit());
        orderVO.setMemberPrice(pricingResult.getMemberPrice());
        orderVO.setLadderRules(pricingResult.getLadderRules());
        orderVO.setMemberDiscount(pricingResult.getMemberDiscount());
    }

    private void fillOrderResult(BlsOrder blsOrder, PricingResult result){
        blsOrder.setDuration(result.getDuration());
        blsOrder.setOriginalAmount(result.getOriginalAmount());
        blsOrder.setDiscountAmount(result.getDiscountAmount());
        blsOrder.setActualAmount(result.getActualAmount());
        blsOrder.setEndTime(result.getEndTime());
    }

}
