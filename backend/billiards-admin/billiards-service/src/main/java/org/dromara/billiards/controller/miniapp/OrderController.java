package org.dromara.billiards.controller.miniapp;

import org.dromara.billiards.common.result.ApiResult;
import org.dromara.billiards.convert.OrderConvert;
import org.dromara.billiards.domain.bo.CreateOrderRequest;
import org.dromara.billiards.domain.bo.OrderQueryRequest;
import org.dromara.billiards.domain.entity.Order;
import org.dromara.billiards.domain.vo.OrderVO;
import org.dromara.billiards.service.OrderService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.R;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 小程序订单控制器
 */
@Slf4j
@RestController("miniappOrderController")
@RequestMapping("/api/miniapp/orders")
@RequiredArgsConstructor
@Tag(name = "订单接口", description = "小程序订单相关接口")
public class OrderController {

    private final OrderService orderService;
    private final OrderConvert orderConvert = OrderConvert.INSTANCE;

    /**
     * 创建订单（开始使用桌台）
     */
    @PostMapping("/create")
    @Operation(summary = "创建订单", description = "开始使用桌台，创建新订单")
    public R<OrderVO> createOrder(@Validated @RequestBody CreateOrderRequest request) {
        Order order = orderService.createOrder(request.getTableId(), request.getChannel());
        return ApiResult.success(orderConvert.toVo(order));
    }

    /**
     * 获取当前进行中的订单
     */
    @GetMapping("/current")
    @Operation(summary = "获取当前订单", description = "获取用户当前进行中的订单，并计算实时金额")
    public R<List<OrderVO>> getCurrentOrder() {
        return ApiResult.success(orderService.getCurrentOrderWithCalculation());
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/{orderId}")
    @Operation(summary = "订单详情", description = "获取订单详细信息")
    public R<OrderVO> getOrderDetail(@PathVariable String orderId) {
        OrderVO order = orderService.getUserOrderDetail(orderId);
        return ApiResult.success(order);
    }

    /**
     * 结束订单,小程序用户是否允许一个人开多台，应该是允许的，但是这样就要考虑多台的情况了。
     */
    @PostMapping("/{orderId}/end")
    @Operation(summary = "结束订单", description = "结束使用，生成最终费用")
    public R<OrderVO> endOrder(@PathVariable String orderId) {
        Order endedOrder = orderService.endUserOrder(orderId);
        return ApiResult.success(orderConvert.toVo(endedOrder));
    }

    /**
     * 计算当前订单实时金额
     */
    @GetMapping("/calculate")
    @Operation(summary = "计算实时金额", description = "计算订单当前实时金额")
    public R<OrderVO> calculateAmount(@Parameter(description = "订单ID", required = true) @RequestParam String orderId) {
        Order order = orderService.calculateCurrentAmount(orderId);
        return ApiResult.success(orderConvert.toVo(order));
    }

    /**
     * 获取用户订单历史
     */
    @GetMapping("/list")
    @Operation(summary = "订单历史", description = "分页查询用户订单历史")
    public R<IPage<OrderVO>> getOrderList(@Validated OrderQueryRequest request) {
        return ApiResult.success(orderConvert.toVoPage(orderService.listUserOrders(request)));
    }

    /**
     * 检查桌台是否可用
     */
    @GetMapping("/check-table")
    @Operation(summary = "检查桌台状态", description = "检查桌台是否被占用")
    public R<Boolean> checkTableStatus(@Parameter(description = "桌台ID", required = true) @RequestParam String tableId) {
        boolean isOccupied = orderService.isTableOccupied(tableId);
        return ApiResult.success(!isOccupied);
    }
}
