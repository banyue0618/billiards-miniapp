package org.dromara.billiards.mapper;

import org.dromara.billiards.domain.entity.Order;
import org.dromara.billiards.domain.vo.OrderVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 订单Mapper接口
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    /**
     * 查询用户进行中的订单
     */
    List<OrderVO> selectInProgressByUserId(@Param("userId") Long userId);

    /**
     * 查询桌台进行中的订单
     */
    OrderVO selectInProgressByTableId(@Param("tableId") String tableId);

    /**
     * 分页查询用户订单列表
     */
    IPage<OrderVO> selectUserOrderPage(Page<?> page, @Param("userId") Long userId);

    /**
     * 查询订单的完整信息
     */
    OrderVO selectOrderDetail(@Param("orderId") String orderId);
}
