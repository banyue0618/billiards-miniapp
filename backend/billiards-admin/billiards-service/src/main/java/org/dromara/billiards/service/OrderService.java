package org.dromara.billiards.service;

import org.dromara.billiards.domain.entity.Order;
// import org.dromara.billiards.model.vo.OrderVO; // 旧的VO，如果不再使用则移除
import com.baomidou.mybatisplus.core.metadata.IPage;
// import com.baomidou.mybatisplus.extension.plugins.pagination.Page; // 通常不需要在接口中指定Page实现
import com.baomidou.mybatisplus.extension.service.IService;
import org.dromara.billiards.domain.bo.OrderQueryRequest; // 用于后台查询
import org.dromara.billiards.domain.vo.OrderVO;

import java.util.List;

/**
 * 订单服务接口
 */
public interface OrderService extends IService<Order> {

    /**
     * 分页查询后台订单列表
     * @param request 查询参数
     * @return 订单分页实体列表
     */
    IPage<Order> pageAdminOrders(OrderQueryRequest request);
    /**
     * (后台)手动结束订单,防止意外情况发生.
     * @param orderId 订单ID
     * @return 是否成功，如果操作失败或订单未找到则抛出异常
     */
    boolean endAdminOrder(String orderId);

    /**
     * (后台)取消订单, 此业务暂时不开放 todo
     * @param orderId 订单ID
     * @return 是否成功，如果操作失败或订单未找到则抛出异常
     */
    boolean cancelAdminOrder(String orderId);

    /**
     * 创建订单
     * @param tableId 桌台ID
     * @return 订单对象
     */
    Order createOrder(String tableId, String channel);

    /**
     * 获取用户当前进行中的订单
     * @param userId 用户ID
     * @return 订单对象，如果没有则返回null
     */
    List<OrderVO> getCurrentOrder(Long userId);

    /**
     * 计算订单实时金额
     * @param orderId 订单ID
     * @return 当前金额
     */
    Order calculateCurrentAmount(String orderId);

    /**
     * 判断桌台是否被占用
     * @param tableId 桌台ID
     * @return 是否被占用
     */
    boolean isTableOccupied(String tableId);

    // --- Methods for Miniapp specific use ---

    /**
     * (小程序) 获取用户订单详情
     * @param orderId 订单ID
     * @return 订单实体，如果找不到或不属于该用户则抛出异常
     */
    OrderVO getUserOrderDetail(String orderId);

    /**
     * (小程序) 用户结束订单
     * @param orderId 订单ID
     * @return 更新后的订单实体
     */
    Order endUserOrder(String orderId);

    /**
     * (小程序) 分页查询用户订单历史
     * @param queryRequest 查询参数 (包含userId和分页信息)
     * @return 订单分页实体列表
     */
    IPage<Order> listUserOrders(OrderQueryRequest queryRequest);

     /**
     * (小程序) 获取用户当前进行中的订单，并计算实时金额
     * @return 订单对象（已计算实时金额），如果没有则返回null或抛出异常
     */
     List<OrderVO> getCurrentOrderWithCalculation();

    /**
     * 获取当前用户下正在退款的订单
     * @return
     */
    Order getRefundingOrder();

    /**
     * 完成订单
     * @param orderId 订单ID
     * @return 完成后的订单实体
     */
    Order completeOrder(String orderId);

    /**
     * 获取所有进行中的订单
     * @return
     */
    List<Order> listOngoingOrders();


    /**
     * 检测订单状态，处理过期订单、未支付订单等
     */
    void detectOrders();

    /**
     * 获取用户订单详情（不校验订单所属人）
     * @param orderId
     * @return
     */
    OrderVO getOrderDetail(String orderId);


    /**
     * 结束订单
     * @param order
     * @return
     */
    Order endOrder(Order order);

    /**
     * 管理员手动处理退款失败的订单
     * @param orderId
     */
    void orderRefundByAdmin(String orderId);
}
