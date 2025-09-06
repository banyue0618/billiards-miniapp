import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { MemberLevelConfigVO, MemberLevelConfigForm, MemberLevelConfigQuery } from '@/api/billiards/memberLevelConfig/types';

/**
 * 查询会员等级配置列表
 * @param query
 * @returns {*}
 */

export const listMemberLevelConfig = (query?: MemberLevelConfigQuery): AxiosPromise<MemberLevelConfigVO[]> => {
  return request({
    url: '/billiards/memberLevelConfig/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询会员等级配置详细
 * @param id
 */
export const getMemberLevelConfig = (id: string | number): AxiosPromise<MemberLevelConfigVO> => {
  return request({
    url: '/billiards/memberLevelConfig/' + id,
    method: 'get'
  });
};

/**
 * 新增会员等级配置
 * @param data
 */
export const addMemberLevelConfig = (data: MemberLevelConfigForm) => {
  return request({
    url: '/billiards/memberLevelConfig',
    method: 'post',
    data: data
  });
};

/**
 * 修改会员等级配置
 * @param data
 */
export const updateMemberLevelConfig = (data: MemberLevelConfigForm) => {
  return request({
    url: '/billiards/memberLevelConfig',
    method: 'put',
    data: data
  });
};

/**
 * 删除会员等级配置
 * @param id
 */
export const delMemberLevelConfig = (id: string | number | Array<string | number>) => {
  return request({
    url: '/billiards/memberLevelConfig/' + id,
    method: 'delete'
  });
};
