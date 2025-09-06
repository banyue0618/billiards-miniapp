import request from '@/utils/request';

// 查询门店桌台列表
export function listTable(params: any) {
  const { storeId, ...query } = params;
  return request({
    url: `/api/admin/tables/${storeId}/tables`,
    method: 'get',
    params: query
  });
}

// 查询桌台详细
export function getTable(tableId: string) {
  return request({
    url: `/api/admin/tables/${tableId}`,
    method: 'get'
  });
}

// 新增桌台
export function addTable(data: any) {
  return request({
    url: '/api/admin/tables',
    method: 'post',
    data: data
  });
}

// 批量创建桌台
export function batchAddTable(data: any) {
  return request({
    url: '/api/admin/tables/batch/add',
    method: 'post',
    data: data
  });
}

// 修改桌台
export function updateTable(tableId: string, data: any) {
  return request({
    url: `/api/admin/tables/${tableId}`,
    method: 'put',
    data: data
  });
}

// 删除桌台
export function deleteTable(tableId: string) {
  return request({
    url: `/api/admin/tables/${tableId}`,
    method: 'delete'
  });
}

// 批量删除桌台
export function batchDeleteTable(ids: string[]) {
  return request({
    url: '/api/admin/tables/batch',
    method: 'delete',
    data: ids
  });
}

// 更新桌台状态
export function updateTableStatus(tableId: string, status: number, reason?: string) {
  return request({
    url: `/api/admin/tables/${tableId}/status`,
    method: 'put',
    params: { status, reason }
  });
}

// 重新生成桌台二维码
export function regenerateQrcode(tableId: string) {
  return request({
    url: `/api/admin/tables/${tableId}/qrcode`,
    method: 'post'
  });
}

// 强制结束桌台使用
export function endTableUsage(tableId: string, reason: string) {
  return request({
    url: `/api/admin/tables/${tableId}/end-usage`,
    method: 'post',
    data: { reason }
  });
} 