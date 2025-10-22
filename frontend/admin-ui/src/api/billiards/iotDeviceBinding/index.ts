import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { IotDeviceBindingVO, IotDeviceBindingForm, IotDeviceBindingQuery } from '@/api/billiards/iotDeviceBinding/types';

/**
 * 查询设备业务绑定（定义场景与设备动作映射）列表
 * @param query
 * @returns {*}
 */

export const listIotDeviceBinding = (query?: IotDeviceBindingQuery): AxiosPromise<IotDeviceBindingVO[]> => {
  return request({
    url: '/billiards/iotDeviceBinding/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询设备业务绑定（定义场景与设备动作映射）详细
 * @param id
 */
export const getIotDeviceBinding = (id: string | number): AxiosPromise<IotDeviceBindingVO> => {
  return request({
    url: '/billiards/iotDeviceBinding/' + id,
    method: 'get'
  });
};

/**
 * 新增设备业务绑定（定义场景与设备动作映射）
 * @param data
 */
export const addIotDeviceBinding = (data: IotDeviceBindingForm) => {
  return request({
    url: '/billiards/iotDeviceBinding',
    method: 'post',
    data: data
  });
};

/**
 * 修改设备业务绑定（定义场景与设备动作映射）
 * @param data
 */
export const updateIotDeviceBinding = (data: IotDeviceBindingForm) => {
  return request({
    url: '/billiards/iotDeviceBinding',
    method: 'put',
    data: data
  });
};

/**
 * 删除设备业务绑定（定义场景与设备动作映射）
 * @param id
 */
export const delIotDeviceBinding = (id: string | number | Array<string | number>) => {
  return request({
    url: '/billiards/iotDeviceBinding/' + id,
    method: 'delete'
  });
};
