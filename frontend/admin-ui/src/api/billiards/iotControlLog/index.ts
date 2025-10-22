import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { IotControlLogVO, IotControlLogForm, IotControlLogQuery } from '@/api/billiards/iotControlLog/types';

/**
 * 查询设备控制日志（记录执行命令历史）列表
 * @param query
 * @returns {*}
 */

export const listIotControlLog = (query?: IotControlLogQuery): AxiosPromise<IotControlLogVO[]> => {
  return request({
    url: '/billiards/iotControlLog/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询设备控制日志（记录执行命令历史）详细
 * @param id
 */
export const getIotControlLog = (id: string | number): AxiosPromise<IotControlLogVO> => {
  return request({
    url: '/billiards/iotControlLog/' + id,
    method: 'get'
  });
};

/**
 * 新增设备控制日志（记录执行命令历史）
 * @param data
 */
export const addIotControlLog = (data: IotControlLogForm) => {
  return request({
    url: '/billiards/iotControlLog',
    method: 'post',
    data: data
  });
};

/**
 * 修改设备控制日志（记录执行命令历史）
 * @param data
 */
export const updateIotControlLog = (data: IotControlLogForm) => {
  return request({
    url: '/billiards/iotControlLog',
    method: 'put',
    data: data
  });
};

/**
 * 删除设备控制日志（记录执行命令历史）
 * @param id
 */
export const delIotControlLog = (id: string | number | Array<string | number>) => {
  return request({
    url: '/billiards/iotControlLog/' + id,
    method: 'delete'
  });
};
