import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { WalletAccountVO, WalletAccountForm, WalletAccountQuery } from '@/api/billiards/walletAccount/types';

/**
 * 查询用户钱包账户列表
 * @param query
 * @returns {*}
 */

export const listWalletAccount = (query?: WalletAccountQuery): AxiosPromise<WalletAccountVO[]> => {
  return request({
    url: '/billiards/walletAccount/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询用户钱包账户详细
 * @param id
 */
export const getWalletAccount = (id: string | number): AxiosPromise<WalletAccountVO> => {
  return request({
    url: '/billiards/walletAccount/' + id,
    method: 'get'
  });
};

/**
 * 新增用户钱包账户
 * @param data
 */
export const addWalletAccount = (data: WalletAccountForm) => {
  return request({
    url: '/billiards/walletAccount',
    method: 'post',
    data: data
  });
};

/**
 * 修改用户钱包账户
 * @param data
 */
export const updateWalletAccount = (data: WalletAccountForm) => {
  return request({
    url: '/billiards/walletAccount',
    method: 'put',
    data: data
  });
};

/**
 * 删除用户钱包账户
 * @param id
 */
export const delWalletAccount = (id: string | number | Array<string | number>) => {
  return request({
    url: '/billiards/walletAccount/' + id,
    method: 'delete'
  });
};
