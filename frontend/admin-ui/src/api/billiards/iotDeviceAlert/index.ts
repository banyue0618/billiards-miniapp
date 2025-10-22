import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { IotDeviceAlertVO, IotDeviceAlertForm, IotDeviceAlertQuery } from '@/api/billiards/iotDeviceAlert/types';

/**
 * 查询设备告警（记录设备异常信息）列表
 * @param query
 * @returns {*}
 */

export const listIotDeviceAlert = (query?: IotDeviceAlertQuery): AxiosPromise<IotDeviceAlertVO[]> => {
  return request({
    url: '/billiards/iotDeviceAlert/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询设备告警（记录设备异常信息）详细
 * @param id
 */
export const getIotDeviceAlert = (id: string | number): AxiosPromise<IotDeviceAlertVO> => {
  return request({
    url: '/billiards/iotDeviceAlert/' + id,
    method: 'get'
  });
};

/**
 * 新增设备告警（记录设备异常信息）
 * @param data
 */
export const addIotDeviceAlert = (data: IotDeviceAlertForm) => {
  return request({
    url: '/billiards/iotDeviceAlert',
    method: 'post',
    data: data
  });
};

/**
 * 修改设备告警（记录设备异常信息）
 * @param data
 */
export const updateIotDeviceAlert = (data: IotDeviceAlertForm) => {
  return request({
    url: '/billiards/iotDeviceAlert',
    method: 'put',
    data: data
  });
};

/**
 * 删除设备告警（记录设备异常信息）
 * @param id
 */
export const delIotDeviceAlert = (id: string | number | Array<string | number>) => {
  return request({
    url: '/billiards/iotDeviceAlert/' + id,
    method: 'delete'
  });
};
