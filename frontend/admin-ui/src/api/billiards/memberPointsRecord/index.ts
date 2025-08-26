import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { MemberPointsRecordVO, MemberPointsRecordForm, MemberPointsRecordQuery } from '@/api/billiards/memberPointsRecord/types';

/**
 * 查询会员积分记录列表
 * @param query
 * @returns {*}
 */

export const listMemberPointsRecord = (query?: MemberPointsRecordQuery): AxiosPromise<MemberPointsRecordVO[]> => {
  return request({
    url: '/billiards/memberPointsRecord/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询会员积分记录详细
 * @param id
 */
export const getMemberPointsRecord = (id: string | number): AxiosPromise<MemberPointsRecordVO> => {
  return request({
    url: '/billiards/memberPointsRecord/' + id,
    method: 'get'
  });
};

/**
 * 新增会员积分记录
 * @param data
 */
export const addMemberPointsRecord = (data: MemberPointsRecordForm) => {
  return request({
    url: '/billiards/memberPointsRecord',
    method: 'post',
    data: data
  });
};

/**
 * 修改会员积分记录
 * @param data
 */
export const updateMemberPointsRecord = (data: MemberPointsRecordForm) => {
  return request({
    url: '/billiards/memberPointsRecord',
    method: 'put',
    data: data
  });
};

/**
 * 删除会员积分记录
 * @param id
 */
export const delMemberPointsRecord = (id: string | number | Array<string | number>) => {
  return request({
    url: '/billiards/memberPointsRecord/' + id,
    method: 'delete'
  });
};
