package org.dromara.billiards.controller.admin;

import org.dromara.billiards.common.result.Result;
import org.dromara.billiards.domain.bo.OrderQueryRequest;
import org.dromara.billiards.domain.entity.Order;
import org.dromara.billiards.domain.vo.OrderVO;
import org.dromara.billiards.convert.OrderConvert;
import org.dromara.billiards.service.OrderService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 管理后台订单控制器
 */
@Slf4j
@RestController("adminOrderController")
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
@Tag(name = "订单接口", description = "管理端订单相关接口")
public class OrderController {

    private final OrderService orderService;
    private final OrderConvert orderConvert = OrderConvert.INSTANCE;

    /**
     * 订单列表
     */
    @GetMapping("/list")
    @Operation(summary = "订单列表", description = "分页查询订单列表")
    public Result<IPage<OrderVO>> list(OrderQueryRequest request) {
        IPage<Order> orderPage = orderService.pageAdminOrders(request);
        return Result.success(orderConvert.toVoPage(orderPage));
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/{orderId}")
    @Operation(summary = "订单详情", description = "获取订单详细信息")
    public Result<OrderVO> detail(@PathVariable("orderId") String orderId) {
        OrderVO order = orderService.getOrderDetail(orderId);
        return Result.success(order);
    }

    /**
     * 手动结束订单
     */
    @PostMapping("/{orderId}/end")
    @Operation(summary = "结束订单", description = "手动结束订单")
    public Result<Boolean> endOrder(@PathVariable("orderId") String orderId) {
        orderService.endAdminOrder(orderId);
        return Result.success(true);
    }

    /**
     * 手动结束订单
     */
    @PostMapping("/{orderId}/refund")
    @Operation(summary = "结束订单", description = "手动订单退款")
    public Result<Boolean> orderRefund(@PathVariable("orderId") String orderId) {
        orderService.orderRefundByAdmin(orderId);
        return Result.success(true);
    }

    /**
     * 取消订单
     */
    @PostMapping("/{orderId}/cancel")
    @Operation(summary = "取消订单", description = "取消订单")
    public Result<Boolean> cancelOrder( @PathVariable("orderId") String orderId) {
        orderService.cancelAdminOrder(orderId);
        return Result.success(true);
    }

    /**
     * 获取门店当日订单数据统计
     */
    @GetMapping("/statistics/store")
    @Operation(summary = "门店订单统计", description = "获取门店当日订单数据统计")
    public Result<Object> getStoreStatistics(@RequestParam String storeId) {
        // TODO: 实现门店订单统计功能
        return Result.success("需要实现门店订单统计功能");
    }

    /**
     * 获取系统订单数据统计
     */
    @GetMapping("/statistics/system")
    @Operation(summary = "系统订单统计", description = "获取系统级订单数据统计")
    public Result<Object> getSystemStatistics() {
        // TODO: 实现系统订单统计功能
        return Result.success("需要实现系统订单统计功能");
    }
}
