import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { MerchantApplyVO, MerchantApplyForm, MerchantApplyQuery } from '@/api/system/merchantApply/types';

/**
 * 查询商户注册申请列表
 * @param query
 * @returns {*}
 */

export const listMerchantApply = (query?: MerchantApplyQuery): AxiosPromise<MerchantApplyVO[]> => {
  return request({
    url: '/system/merchantApply/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询商户注册申请详细
 * @param id
 */
export const getMerchantApply = (id: string | number): AxiosPromise<MerchantApplyVO> => {
  return request({
    url: '/system/merchantApply/' + id,
    method: 'get'
  });
};

/**
 * 新增商户注册申请
 * @param data
 */
export const addMerchantApply = (data: MerchantApplyForm) => {
  return request({
    url: '/system/merchantApply',
    method: 'post',
    data: data
  });
};

/**
 * 修改商户注册申请
 * @param data
 */
export const updateMerchantApply = (data: MerchantApplyForm) => {
  return request({
    url: '/system/merchantApply',
    method: 'put',
    data: data
  });
};

/**
 * 删除商户注册申请
 * @param id
 */
export const delMerchantApply = (id: string | number | Array<string | number>) => {
  return request({
    url: '/system/merchantApply/' + id,
    method: 'delete'
  });
};

/**
 * 审批通过
 * @param id
 * @param data 可选备注
 */
export const approveMerchantApply = (id: string | number, data?: Partial<MerchantApplyForm>) => {
  return request({
    url: `/system/merchantApply/approve/${id}`,
    method: 'post',
    data
  });
};

/**
 * 审批驳回
 * @param id
 * @param data 需包含审核原因 auditReason
 */
export const rejectMerchantApply = (id: string | number, data: { auditReason: string } & Partial<MerchantApplyForm>) => {
  return request({
    url: `/system/merchantApply/reject/${id}`,
    method: 'post',
    data
  });
};