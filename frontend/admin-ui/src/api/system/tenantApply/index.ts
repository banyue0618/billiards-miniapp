import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { TenantApplyVO, TenantApplyForm, TenantApplyQuery } from '@/api/system/tenantApply/types';

/**
 * 查询租户注册申请列表
 * @param query
 * @returns {*}
 */

export const listTenantApply = (query?: TenantApplyQuery): AxiosPromise<TenantApplyVO[]> => {
  return request({
    url: '/system/tenantApply/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询租户注册申请详细
 * @param id
 */
export const getTenantApply = (id: string | number): AxiosPromise<TenantApplyVO> => {
  return request({
    url: '/system/tenantApply/' + id,
    method: 'get'
  });
};

/**
 * 新增租户注册申请
 * @param data
 */
export const addTenantApply = (data: TenantApplyForm) => {
  return request({
    url: '/system/tenantApply',
    method: 'post',
    data: data
  });
};

/**
 * 修改租户注册申请
 * @param data
 */
export const updateTenantApply = (data: TenantApplyForm) => {
  return request({
    url: '/system/tenantApply',
    method: 'put',
    data: data
  });
};

/**
 * 删除租户注册申请
 * @param id
 */
export const delTenantApply = (id: string | number | Array<string | number>) => {
  return request({
    url: '/system/tenantApply/' + id,
    method: 'delete'
  });
};

/**
 * 审批通过
 * @param id
 * @param data 可选备注
 */
export const approveTenantApply = (id: string | number, data?: Partial<TenantApplyForm>) => {
  return request({
    url: `/system/tenantApply/approve/${id}`,
    method: 'post',
    data
  });
};

/**
 * 审批驳回
 * @param id
 * @param data 需包含审核原因 auditReason
 */
export const rejectTenantApply = (id: string | number, data: { auditReason: string } & Partial<TenantApplyForm>) => {
  return request({
    url: `/system/tenantApply/reject/${id}`,
    method: 'post',
    data
  });
};