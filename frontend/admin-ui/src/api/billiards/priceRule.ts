import request from '@/utils/request';

// 查询计费规则列表
export function listPriceRule(query: any) {
  return request({
    url: '/api/admin/priceRules/list',
    method: 'get',
    params: query
  });
}

// 查询计费规则详细
export function getPriceRule(ruleId: string) {
  return request({
    url: `/api/admin/priceRules/${ruleId}`,
    method: 'get'
  });
}

// 新增计费规则
export function addPriceRule(data: any) {
  return request({
    url: '/api/admin/priceRules',
    method: 'post',
    data: data
  });
}

// 修改计费规则
export function updatePriceRule(ruleId: string, data: any) {
  return request({
    url: `/api/admin/priceRules/${ruleId}`,
    method: 'put',
    data: data
  });
}

// 删除计费规则
export function deletePriceRule(ruleId: string) {
  return request({
    url: `/api/admin/priceRules/${ruleId}`,
    method: 'delete'
  });
}

// 更新计费规则状态
export function updatePriceRuleStatus(ruleId: string, status: number) {
  return request({
    url: `/api/admin/priceRules/${ruleId}/status`,
    method: 'put',
    params: { status }
  });
}

// 应用计费规则到桌台
export function applyPriceRule(ruleId: string, tableIds: string[]) {
  return request({
    url: `/api/admin/priceRules/${ruleId}/apply`,
    method: 'post',
    data: tableIds
  });
}

// 计费规则预览测算
export function previewPriceRule(data: any) {
  return request({
    url: '/api/admin/priceRules/preview',
    method: 'post',
    data: data
  });
}
