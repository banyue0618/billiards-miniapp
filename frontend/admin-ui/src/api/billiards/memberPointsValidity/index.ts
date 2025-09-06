import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { MemberPointsValidityVO, MemberPointsValidityForm, MemberPointsValidityQuery } from '@/api/billiards/memberPointsValidity/types';

/**
 * 查询积分有效期列表
 * @param query
 * @returns {*}
 */

export const listMemberPointsValidity = (query?: MemberPointsValidityQuery): AxiosPromise<MemberPointsValidityVO[]> => {
  return request({
    url: '/billiards/memberPointsValidity/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询积分有效期详细
 * @param id
 */
export const getMemberPointsValidity = (id: string | number): AxiosPromise<MemberPointsValidityVO> => {
  return request({
    url: '/billiards/memberPointsValidity/' + id,
    method: 'get'
  });
};

/**
 * 新增积分有效期
 * @param data
 */
export const addMemberPointsValidity = (data: MemberPointsValidityForm) => {
  return request({
    url: '/billiards/memberPointsValidity',
    method: 'post',
    data: data
  });
};

/**
 * 修改积分有效期
 * @param data
 */
export const updateMemberPointsValidity = (data: MemberPointsValidityForm) => {
  return request({
    url: '/billiards/memberPointsValidity',
    method: 'put',
    data: data
  });
};

/**
 * 删除积分有效期
 * @param id
 */
export const delMemberPointsValidity = (id: string | number | Array<string | number>) => {
  return request({
    url: '/billiards/memberPointsValidity/' + id,
    method: 'delete'
  });
};
