import request from '@/utils/request';

// 查询门店列表
export function listStore(query: any) {
  return request({
    url: '/api/admin/stores/list',
    method: 'get',
    params: query
  });
}

// 查询门店详细
export function getStore(storeId: string) {
  return request({
    url: `/api/admin/stores/${storeId}`,
    method: 'get'
  });
}

// 新增门店
export function addStore(data: any) {
  return request({
    url: '/api/admin/stores',
    method: 'post',
    data: data
  });
}

// 修改门店
export function updateStore(storeId: string, data: any) {
  return request({
    url: `/api/admin/stores/${storeId}`,
    method: 'put',
    data: data
  });
}

// 删除门店
export function deleteStore(storeId: string) {
  return request({
    url: `/api/admin/stores/${storeId}`,
    method: 'delete'
  });
}

// 更新门店状态
export function updateStoreStatus(storeId: string, status: number, reason?: string) {
  return request({
    url: `/api/admin/stores/${storeId}/status`,
    method: 'patch',
    data: { status, reason }
  });
}

// 切换门店运营状态（支持描述）
export function changeStoreStatus(data: { id: string, status: number, announcement: string }) {
  return request({
    url: '/api/admin/stores/status',
    method: 'put',
    data
  });
}
