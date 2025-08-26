import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { PointsRuleVO, PointsRuleForm, PointsRuleQuery } from '@/api/billiards/pointsRule/types';

/**
 * 查询积分规则列表
 * @param query
 * @returns {*}
 */

export const listPointsRule = (query?: PointsRuleQuery): AxiosPromise<PointsRuleVO[]> => {
  return request({
    url: '/billiards/pointsRule/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询积分规则详细
 * @param id
 */
export const getPointsRule = (id: string | number): AxiosPromise<PointsRuleVO> => {
  return request({
    url: '/billiards/pointsRule/' + id,
    method: 'get'
  });
};

/**
 * 新增积分规则
 * @param data
 */
export const addPointsRule = (data: PointsRuleForm) => {
  return request({
    url: '/billiards/pointsRule',
    method: 'post',
    data: data
  });
};

/**
 * 修改积分规则
 * @param data
 */
export const updatePointsRule = (data: PointsRuleForm) => {
  return request({
    url: '/billiards/pointsRule',
    method: 'put',
    data: data
  });
};

/**
 * 删除积分规则
 * @param id
 */
export const delPointsRule = (id: string | number | Array<string | number>) => {
  return request({
    url: '/billiards/pointsRule/' + id,
    method: 'delete'
  });
};
