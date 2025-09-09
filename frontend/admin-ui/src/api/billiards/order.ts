import request from '@/utils/request';

// 查询订单列表
export function listOrder(query: any) {
  return request({
    url: '/api/admin/orders/list',
    method: 'get',
    params: query
  });
}

// 查询订单详细
export function getOrder(orderId: string) {
  return request({
    url: `/api/admin/orders/${orderId}`,
    method: 'get'
  });
}

// 强制结束订单
export function endOrder(orderId: string, reason: string) {
  return request({
    url: `/api/admin/orders/${orderId}/end`,
    method: 'post',
    data: { reason }
  });
}

// 修改订单金额
export function changeOrderAmount(orderId: string, amount: number, reason: string) {
  return request({
    url: `/api/admin/orders/${orderId}/changeOrderAmount`,
    method: 'post',
    data: { amount, reason }
  });
}

// 取消订单
export function cancelOrder(orderId: string, reason: string) {
  return request({
    url: `/api/admin/orders/${orderId}/cancel`,
    method: 'post',
    data: { reason }
  });
}

// 获取进行中订单列表
export function listOngoingOrders(storeId?: string) {
  return request({
    url: `/api/admin/orders/listOngoingOrders`,
    method: 'get',
    params: { storeId: storeId }
  });
}

// 导出订单数据
export function exportOrders(query: any) {
  return request({
    url: '/api/admin/orders/export',
    method: 'get',
    params: query,
    responseType: 'blob'
  });
}
