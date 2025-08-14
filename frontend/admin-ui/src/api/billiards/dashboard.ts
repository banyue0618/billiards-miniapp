import request from '@/utils/request';

/**
 * 获取仪表盘概览数据
 * @param params 查询参数
 * @returns 返回Promise
 */
export function getDashboardData(params?: any) {
  return request({
    url: '/api/admin/dashboard/overview',
    method: 'get',
    params
  });
}

/**
 * 获取营收趋势图表数据
 * @param params 查询参数
 * @returns 返回Promise
 */
export function getRevenueChart(params?: any) {
  return request({
    url: '/api/admin/dashboard/revenue-chart',
    method: 'get',
    params
  });
}

/**
 * 获取桌台使用率图表数据(暂不需要)
 * @param params 查询参数
 * @returns 返回Promise
 */
export function getTableUsageChart(params?: any) {
  return request({
    url: '/api/admin/dashboard/usage-chart',
    method: 'get',
    params
  });
}

/**
 * 获取每日时段分析数据
 * @param params 查询参数
 * @returns 返回Promise
 */
export function getHourlyAnalysis(params?: any) {
  return request({
    url: '/api/admin/dashboard/hourly-analysis',
    method: 'get',
    params
  });
}

/**
 * 获取门店营收排行
 * @param params 查询参数
 * @returns 返回Promise
 */
export function getStoreRanking(params?: any) {
  return request({
    url: '/api/admin/dashboard/store-ranking',
    method: 'get',
    params
  });
}

/**
 * 获取热门桌台数据
 * @param params 查询参数
 * @returns 返回Promise
 */
export function getTopTables(params?: any) {
  return request({
    url: '/api/admin/dashboard/top-tables',
    method: 'get',
    params
  });
}

/**
 * 获取计费规则分析数据
 * @param params 查询参数
 * @returns 返回Promise
 */
export function getPriceRuleAnalysis(params?: any) {
  return request({
    url: '/api/admin/dashboard/price-rule-analysis',
    method: 'get',
    params
  });
}

/**
 * 导出仪表盘数据
 * @param params 查询参数
 * @returns 返回Promise
 */
export function exportDashboardData(params?: any) {
  return request({
    url: '/api/admin/dashboard/export',
    method: 'get',
    params,
    responseType: 'blob'
  });
}
