package org.dromara.billiards.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import org.dromara.billiards.common.constant.BilliardsConstants;
import org.dromara.billiards.common.constant.OrderChannelEnum;
import org.dromara.billiards.common.exception.BilliardsException;
import org.dromara.billiards.common.result.ResultCode;
import org.dromara.billiards.common.utils.OrderNumberGenerator;
import org.dromara.billiards.convert.OrderConvert;
import org.dromara.billiards.mapper.OrderMapper;
import org.dromara.billiards.mapper.TableMapper;
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
import org.dromara.common.satoken.utils.LoginHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
@DS(BilliardsConstants.DS_BILLIARDS_PLATFORM)
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private final TableMapper tableMapper;
    private final PriceRuleService priceRuleService;
    private final PricingStrategyFactory pricingStrategyFactory;
    private final UserService billiardsUserService;
    private final IPayRecordService payRecordService;
    private final IBlsRefundRecordService refundRecordService;
    private final StoreService storeService;
    private final OrderConvert orderConvert = OrderConvert.INSTANCE;

    @Override
    public IPage<Order> pageAdminOrders(OrderQueryRequest request) {
        Page<Order> pageParam = new Page<>(request.getPageNum(), request.getPageSize());
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.like(StringUtils.isNotBlank(request.getOrderNo()), Order::getOrderNo, request.getOrderNo());
        queryWrapper.eq(request.getUserId() != null, Order::getUserId, request.getUserId());
        queryWrapper.eq(StringUtils.isNotBlank(request.getStoreId()), Order::getStoreId, request.getStoreId());
        queryWrapper.eq(request.getStatus() != null, Order::getStatus, request.getStatus());

        if (request.getStartTime() != null && request.getEndTime() != null) {
            queryWrapper.between(Order::getCreateTime, request.getStartTime(), request.getEndTime());
        } else if (request.getStartTime() != null) {
            queryWrapper.ge(Order::getCreateTime, request.getStartTime());
        } else if (request.getEndTime() != null) {
            queryWrapper.le(Order::getCreateTime, request.getEndTime());
        }
        queryWrapper.orderByDesc(Order::getCreateTime);
        return this.page(pageParam, queryWrapper);
    }
    /**
     * 手动结束订单，防止意外情况发生
     * @param orderId 订单ID
     * @return 是否成功，如果操作失败或订单未找到则抛出异常
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean endAdminOrder(String orderId) {
        Order order = this.getById(orderId);
        if (order == null) {
            throw BilliardsException.of(ResultCode.ORDER_NOT_EXIST);
        }
        if (order.getStatus() != null && order.getStatus() != 0) {
            throw BilliardsException.of(ResultCode.ORDER_ALREADY_ENDED);
        }
        endOrder(order);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelAdminOrder(String orderId) {
        Order order = this.getById(orderId);
        if (order == null) {
            throw BilliardsException.of(ResultCode.ORDER_NOT_EXIST);
        }
        if (order.getStatus() != null && order.getStatus() == 2) {
             throw BilliardsException.of(ResultCode.ORDER_STATUS_ERROR);
        }

//        order.setStatus(2);
        order.setEndTime(LocalDateTime.now());
        boolean success = this.updateById(order);
        if (!success) {
            throw BilliardsException.of(ResultCode.ERROR);
        }

        if (order.getStatus() != null && order.getStatus() == 0) {
            Table table = tableMapper.selectById(order.getTableId());
            if (table != null) {
                table.setStatus(0);
                tableMapper.updateById(table);
            } else {
                log.warn("Order {} references a non-existent tableId {} during cancelOrder.", orderId, order.getTableId());
            }
        }
        return true;
    }

    /**
     * 创建订单
     * @param tableId 桌台ID
     * @return 订单对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Order createOrder(String tableId, String channel) {
        // 检查用户是否有进行中的订单
//        List<OrderVO> existingOrder = getCurrentOrder(LoginHelper.getUserIdStr());
//        if (CollectionUtil.isNotEmpty(existingOrder)) {
//            throw BilliardsException.of(ResultCode.ORDER_IN_PROGRESS);
//        }
        if(OrderChannelEnum.fromString(channel) == null) {
            throw BilliardsException.of(ResultCode.INVALID_CHANNEL);
        }
        // 检查桌台是否被占用
        if (isTableOccupied(tableId)) {
            throw BilliardsException.of(ResultCode.TABLE_OCCUPIED);
        }

        // 获取桌台信息
        Table table = tableMapper.selectById(tableId);
        if (table == null) {
            throw BilliardsException.of(ResultCode.TABLE_NOT_EXIST);
        }

        // 创建订单
        Order order = this.orderInit(table);
        order.setChannel(channel);

        // 保存订单
        if (!save(order)) {
            throw BilliardsException.of(ResultCode.ERROR);
        }

        table.setStatus(1);
        tableMapper.updateById(table);

        return order;
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
    public Order calculateCurrentAmount(String orderId) {
        // 获取订单信息
        Order order = getById(orderId);
        if (order == null) {
            throw BilliardsException.of(ResultCode.ORDER_NOT_EXIST);
        }
        PricingResult result = calculateAmount(order.getStartTime(), null, order.getPriceRuleId(), false);
        fillOrderResult(order, result);
        return order;
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
        Order order = getById(orderId);
        // 如果订单不存在，抛出异常
        if (order == null) {
            throw BilliardsException.of(ResultCode.ORDER_NOT_EXIST);
        }
        // 校验订单属于是否当前用户
        // 如果订单的用户ID与当前登录用户ID不一致，抛出权限异常
        if (!LoginHelper.getUserId().equals(order.getUserId())) {
            throw BilliardsException.of(ResultCode.FORBIDDEN);
        }
        OrderVO orderVO = orderConvert.toVo(order);
        // 如果是进行中订单，计算实时金额
        if (order.getStatus() != null && order.getStatus() == 0 && order.getStartTime() != null) {
            PricingResult pricingResult = calculateAmount(order.getStartTime(), null, order.getPriceRuleId(), false);
            // 更新VO对象
            fillOrderVO(orderVO, pricingResult);
        }
        return orderVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Order endUserOrder(String orderId) {
        Order order = this.getById(orderId);
        if (order == null) {
            throw BilliardsException.of(ResultCode.ORDER_NOT_EXIST);
        }
        if (LoginHelper.getUserId() != order.getUserId()) {
            throw BilliardsException.of(ResultCode.FORBIDDEN);
        }
        return endOrder(order); // Return the updated order
    }

    @Override
    public IPage<Order> listUserOrders(OrderQueryRequest queryRequest) {
        queryRequest.setUserId(LoginHelper.getUserId());
        Page<Order> pageParam = new Page<>(queryRequest.getPageNum(), queryRequest.getPageSize());
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getUserId, queryRequest.getUserId());

        queryWrapper.eq(queryRequest.getStatus() != null, Order::getStatus, queryRequest.getStatus());
        if (queryRequest.getStartTime() != null && queryRequest.getEndTime() != null) {
            queryWrapper.between(Order::getCreateTime, queryRequest.getStartTime(), queryRequest.getEndTime());
        } else if (queryRequest.getStartTime() != null) {
            queryWrapper.ge(Order::getCreateTime, queryRequest.getStartTime());
        } else if (queryRequest.getEndTime() != null) {
            queryWrapper.le(Order::getCreateTime, queryRequest.getEndTime());
        }

        queryWrapper.orderByDesc(Order::getCreateTime);
        return this.page(pageParam, queryWrapper);
    }

    @Override
    public List<OrderVO> getCurrentOrderWithCalculation() {
        List<OrderVO> orderVOS = this.getCurrentOrder(LoginHelper.getUserId());
        if (CollectionUtil.isNotEmpty(orderVOS)) {
            // 计算金额
            for (OrderVO orderVO : orderVOS) {
                // 计算实时金额
                PricingResult pricingResult = calculateAmount(orderVO.getStartTime(), null, orderVO.getPriceRuleId(), false);
                // 更新VO对象
                fillOrderVO(orderVO, pricingResult);
            }
        }
        return orderVOS;
    }

    @Override
    public Order getRefundingOrder() {
        // 找出当前用户退款中的订单，只有一条
        List<Order> orders = this.list(new LambdaQueryWrapper<Order>()
                .eq(Order::getUserId, LoginHelper.getUserIdStr())
                .eq(Order::getPaymentStatus, 2) // 2 for refunding
                .orderByDesc(Order::getCreateTime)
                .last("LIMIT 1"));
        if (CollectionUtil.isNotEmpty(orders)) {
            return orders.get(0);
        }
        return null;
    }

    @Override
    public Order completeOrder(String orderId) {
        Order order = this.getById(orderId);
        if (order == null) {
            throw BilliardsException.of(ResultCode.ORDER_NOT_EXIST);
        }
        if(order.getPaymentStatus() != 2){
            throw BilliardsException.of(ResultCode.ORDER_NOT_REFUNDING);
        }
        order.setPaymentStatus(1);
        // 更新订单状态
        if (!this.updateById(order)) {
            throw BilliardsException.of(ResultCode.ERROR);
        }
        return order;
    }

    @Override
    public List<Order> listOngoingOrders() {
        // 查询所有进行中的订单
        return this.list(new LambdaQueryWrapper<Order>()
                .eq(Order::getStatus, 0) // 0 for in progress
                .orderByDesc(Order::getCreateTime));
    }

    @Override
    public void detectOrders() {
        List<Order> orders = listOngoingOrders();
        // 计算使用费用，如果消费金额达到一定额度，则发出提醒，告知用户余额不足，如果余额为0，直接结束当前订单
        for (Order order : orders) {
            // 计算当前订单的实时金额
            PricingResult result = calculateAmount(order.getStartTime(), null, order.getPriceRuleId(), false);

            // 获取当前用户的余额
            BigDecimal userBalance = billiardsUserService.getById(order.getUserId()).getBalance();

            // 余额不足预警提醒的阈值
            BigDecimal warningThreshold = new BigDecimal("10.00"); // 例如10元

            // 计算消费金额与余额的差值。
            BigDecimal balanceDifference = userBalance.subtract(result.getActualAmount());

            // 判断是否达到临界点，如果已经在临界点范围之内，则发出提醒。 如果差值为0，表示用户余额使用完毕
            if (balanceDifference.compareTo(BigDecimal.ZERO) <= 0) {
                // 用户余额不足，结束订单
                endOrder(order);
                log.warn("User {} has insufficient balance, ending order {}", order.getUserId(), order.getOrderNo());
            } else if (balanceDifference.compareTo(warningThreshold) <= 0) {
                // 余额不足预警提醒
                log.warn("User {} is approaching low balance threshold, current balance: {}, order: {}",
                         order.getUserId(), userBalance, order.getOrderNo());
                // todo
            }

        }
        log.info("Order detection completed, processed {} ongoing orders.", orders.size());
    }

    @Override
    public OrderVO getOrderDetail(String orderId) {
        OrderVO order = baseMapper.selectOrderDetail(orderId);
        if (order == null) {
            throw BilliardsException.of(ResultCode.ORDER_NOT_EXIST);
        }
        // 如果是进行中订单，计算实时金额
        PricingResult pricingResult = calculateAmount(order.getStartTime(), order.getEndTime(), order.getPriceRuleId(), false);
        // 更新VO对象
        fillOrderVO(order, pricingResult);
        return order;
    }


    private Order orderInit(Table table){
        Order order = new Order();
        order.setOrderNo(OrderNumberGenerator.generate());
        order.setUserId(LoginHelper.getUserId());
        order.setStoreId(table.getStoreId());
        order.setStoreName(storeService.getById(table.getStoreId()).getName());
        order.setTableId(table.getId());
        order.setTableNumber(table.getTablePrefix() + table.getTableNumber());
        order.setPriceRuleId(table.getPriceRuleId());
        order.setStartTime(LocalDateTime.now());
        order.setDuration(0);
        order.setOriginalAmount(BigDecimal.ZERO);
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setActualAmount(BigDecimal.ZERO);
        order.setPaymentStatus(0);
        order.setStatus(0);
        return order;
    }

    /**
     * 用户结束计费，订单的支付状态从未支付变成已支付、订单的状态从进行中变成已完成。
     * 如果需要退款，则支付状态变成退款中、订单状态变成已结束
     * 待退款回调完成，变成已支付、订单也变成已完成
     * @param order
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Order endOrder(Order order){
        if (order.getStatus() != null && order.getStatus() != 0) {
            throw BilliardsException.of(ResultCode.ORDER_ALREADY_ENDED);
        }
        // 计算消费额
        PricingResult result = calculateAmount(order.getStartTime(), null, order.getPriceRuleId(), false);
        // 更新订单信息
        fillOrderResult(order, result);
        order.setStatus(1); // 1 for completed


        // 解锁桌台
        Table table = tableMapper.selectById(order.getTableId());
        table.setStatus(0);
        tableMapper.updateById(table);

        // 扣费操作，返回用户余额
        final BigDecimal deductBalance = billiardsUserService.deductBalance(result.getActualAmount(), order.getUserId());

        // 如果剩余金额大于0，发起微信退款(以后考虑异步，以防止微信调用失败导致无法结束订单)
        if(deductBalance.compareTo(BigDecimal.ZERO) > 0){
            // 更新订单支付状态为退款中
            order.setPaymentStatus(2);
            this.updateById(order);
            // 获取系统中该用户已支付的最新记录，
            PayRecord lastPayRecord = payRecordService.getLastPayRecord(LoginHelper.getUserId());

            // 创建退款记录 BlsRefundRecord
            refundRecordService.createRefund(order.getId(), lastPayRecord, deductBalance);
            return order;
        }
        // 无需退款，直接更新记录为 已支付
        order.setPaymentStatus(1); // 已支付
        this.updateById(order);
        // 发起微信退款
        return order;
    }

    private PricingResult calculateAmount(LocalDateTime startTime, LocalDateTime endTime, String priceRuleId, boolean isMember){
        // 获取计费规则
        PriceRule priceRule = priceRuleService.getById(priceRuleId);

        // 计算当前使用时长
        endTime = endTime == null ? LocalDateTime.now() : endTime;

        // 判断开始时间不能大于结束时间
        if (startTime.isAfter(endTime)) {
            throw BilliardsException.of(ResultCode.INVALID_TIME_RANGE);
        }

        Duration duration = Duration.between(startTime, endTime);
        int minutes = (int) duration.toMinutes();

        // 确保达到最低消费时长
        minutes = Math.max(minutes, priceRule.getMinMinutes());

        // 例如: isMember = userService.checkMemberStatus(order.getUserId());

        // 使用策略模式计算费用
        PricingStrategy strategy = pricingStrategyFactory.getStrategy(priceRule.getRuleType());
        PricingResult result = strategy.calculatePrice(null, priceRule, minutes, isMember);
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

    private void fillOrderResult(Order order, PricingResult result){
        order.setDuration(result.getDuration());
        order.setOriginalAmount(result.getOriginalAmount());
        order.setDiscountAmount(result.getDiscountAmount());
        order.setActualAmount(result.getActualAmount());
        order.setEndTime(result.getEndTime());
    }

}
