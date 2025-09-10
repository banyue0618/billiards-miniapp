export interface WalletAccountVO {
  /**
   * 用户ID
   */
  userId: string | number;

  /**
   * 当前余额
   */
  balance: number;

  /**
   * 冻结金额
   */
  freezeAmount: number;

  /**
   * 累计充值
   */
  totalRecharge: number;

  /**
   * 累计退款
   */
  totalRefund: number;

  /**
   * 备注
   */
  remark: string;

}

export interface WalletAccountForm extends BaseEntity {
}

export interface WalletAccountQuery extends PageQuery {

  /**
   * 用户ID
   */
  userId?: string | number;

  /**
   * 累计充值
   */
  totalRecharge?: number;

  /**
   * 累计退款
   */
  totalRefund?: number;

    /**
     * 日期范围参数
     */
    params?: any;
}



