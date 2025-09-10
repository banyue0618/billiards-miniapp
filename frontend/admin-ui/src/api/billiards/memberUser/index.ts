import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { MemberUserVO, MemberUserForm, MemberUserQuery } from '@/api/billiards/memberUser/types';

/**
 * 查询会员用户列表
 * @param query
 * @returns {*}
 */

export const listMemberUser = (query?: MemberUserQuery): AxiosPromise<MemberUserVO[]> => {
  return request({
    url: '/api/memberUser/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询会员用户详细
 * @param id
 */
export const getMemberUser = (id: string | number): AxiosPromise<MemberUserVO> => {
  return request({
    url: '/api/memberUser/' + id,
    method: 'get'
  });
};

/**
 * 新增会员用户
 * @param data
 */
export const addMemberUser = (data: MemberUserForm) => {
  return request({
    url: '/api/memberUser',
    method: 'post',
    data: data
  });
};

/**
 * 修改会员用户
 * @param data
 */
export const updateMemberUser = (data: MemberUserForm) => {
  return request({
    url: '/api/memberUser',
    method: 'put',
    data: data
  });
};

/**
 * 删除会员用户
 * @param id
 */
export const delMemberUser = (id: string | number | Array<string | number>) => {
  return request({
    url: '/api/memberUser/' + id,
    method: 'delete'
  });
};
