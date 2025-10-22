import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { IotDeviceVO, IotDeviceForm, IotDeviceQuery } from '@/api/billiards/iotDevice/types';

/**
 * 查询IoT设备列表
 * @param query
 * @returns {*}
 */

export const listIotDevice = (query?: IotDeviceQuery): AxiosPromise<IotDeviceVO[]> => {
  return request({
    url: '/billiards/iotDevice/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询IoT设备详细
 * @param id
 */
export const getIotDevice = (id: string | number): AxiosPromise<IotDeviceVO> => {
  return request({
    url: '/billiards/iotDevice/' + id,
    method: 'get'
  });
};

/**
 * 新增IoT设备
 * @param data
 */
export const addIotDevice = (data: IotDeviceForm) => {
  return request({
    url: '/billiards/iotDevice',
    method: 'post',
    data: data
  });
};

/**
 * 修改IoT设备
 * @param data
 */
export const updateIotDevice = (data: IotDeviceForm) => {
  return request({
    url: '/billiards/iotDevice',
    method: 'put',
    data: data
  });
};

/**
 * 删除IoT设备
 * @param id
 */
export const delIotDevice = (id: string | number | Array<string | number>) => {
  return request({
    url: '/billiards/iotDevice/' + id,
    method: 'delete'
  });
};
