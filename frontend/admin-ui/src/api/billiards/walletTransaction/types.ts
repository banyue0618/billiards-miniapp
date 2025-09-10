export interface WalletTransactionVO {
  /**
   * 用户ID
   */
  userId: string | number;

  /**
   * 交易类型:RECHARGE/CONSUME/REFUND
   */
  transType: string;

  /**
   * 交易金额
   */
  amount: number;

  /**
   * 微信交易id
   */
  transactionId: string | number;

  /**
   * 备注
   */
  remark: string;

}

export interface WalletTransactionForm extends BaseEntity {
}

export interface WalletTransactionQuery extends PageQuery {

  /**
   * 用户ID
   */
  userId?: string | number;

  /**
   * 交易类型:RECHARGE/CONSUME/REFUND
   */
  transType?: string;

  /**
   * 交易金额
   */
  amount?: number;

    /**
     * 日期范围参数
     */
    params?: any;
}



