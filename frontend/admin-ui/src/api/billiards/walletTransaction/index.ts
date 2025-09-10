import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { WalletTransactionVO, WalletTransactionForm, WalletTransactionQuery } from '@/api/billiards/walletTransaction/types';

/**
 * 查询用户钱包流水列表
 * @param query
 * @returns {*}
 */

export const listWalletTransaction = (query?: WalletTransactionQuery): AxiosPromise<WalletTransactionVO[]> => {
  return request({
    url: '/billiards/walletTransaction/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询用户钱包流水详细
 * @param id
 */
export const getWalletTransaction = (id: string | number): AxiosPromise<WalletTransactionVO> => {
  return request({
    url: '/billiards/walletTransaction/' + id,
    method: 'get'
  });
};

/**
 * 新增用户钱包流水
 * @param data
 */
export const addWalletTransaction = (data: WalletTransactionForm) => {
  return request({
    url: '/billiards/walletTransaction',
    method: 'post',
    data: data
  });
};

/**
 * 修改用户钱包流水
 * @param data
 */
export const updateWalletTransaction = (data: WalletTransactionForm) => {
  return request({
    url: '/billiards/walletTransaction',
    method: 'put',
    data: data
  });
};

/**
 * 删除用户钱包流水
 * @param id
 */
export const delWalletTransaction = (id: string | number | Array<string | number>) => {
  return request({
    url: '/billiards/walletTransaction/' + id,
    method: 'delete'
  });
};
