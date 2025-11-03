import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { ReservationVO, ReservationForm, ReservationQuery } from '@/api/billiards/reservation/types';

/**
 * 查询用户预约记录列表
 * @param query
 * @returns {*}
 */

export const listReservation = (query?: ReservationQuery): AxiosPromise<ReservationVO[]> => {
  return request({
    url: '/billiards/reservation/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询用户预约记录详细
 * @param id
 */
export const getReservation = (id: string | number): AxiosPromise<ReservationVO> => {
  return request({
    url: '/billiards/reservation/' + id,
    method: 'get'
  });
};

/**
 * 新增用户预约记录
 * @param data
 */
export const addReservation = (data: ReservationForm) => {
  return request({
    url: '/billiards/reservation',
    method: 'post',
    data: data
  });
};

/**
 * 修改用户预约记录
 * @param data
 */
export const updateReservation = (data: ReservationForm) => {
  return request({
    url: '/billiards/reservation',
    method: 'put',
    data: data
  });
};

/**
 * 删除用户预约记录
 * @param id
 */
export const delReservation = (id: string | number | Array<string | number>) => {
  return request({
    url: '/billiards/reservation/' + id,
    method: 'delete'
  });
};
