export interface MemberUserVO {
  /**
   * 会员ID
   */
  id: string | number;

  /**
   * 用户ID
   */
  userId: string | number;

  /**
   * 当前等级编码
   */
  levelCode: number;

  /**
   * 累计消费金额
   */
  totalAmount: number;

  /**
   * 当前积分
   */
  points: number;

  /**
   * 本月已使用免费时长（分钟）
   */
  monthlyUsedMinutes: number;

  /**
   * 等级有效期
   */
  levelExpireTime: string;

  /**
   * 最近消费时间
   */
  lastConsumeTime: string;

  /**
   * 状态：0-正常 1-禁用
   */
  status: number;

}

export interface MemberUserForm extends BaseEntity {
  /**
   * 会员ID
   */
  id?: string | number;

  /**
   * 用户ID
   */
  userId?: string | number;

  /**
   * 当前等级编码
   */
  levelCode?: number;

  /**
   * 累计消费金额
   */
  totalAmount?: number;

  /**
   * 当前积分
   */
  points?: number;

  /**
   * 本月已使用免费时长（分钟）
   */
  monthlyUsedMinutes?: number;

  /**
   * 等级有效期
   */
  levelExpireTime?: string;

  /**
   * 最近消费时间
   */
  lastConsumeTime?: string;

  /**
   * 状态：0-正常 1-禁用
   */
  status?: number;

}

export interface MemberUserQuery extends PageQuery {

  /**
   * 用户ID
   */
  userId?: string | number;

  /**
   * 当前等级编码
   */
  levelCode?: number;

  /**
   * 累计消费金额
   */
  totalAmount?: number;

  /**
   * 当前积分
   */
  points?: number;

  /**
   * 本月已使用免费时长（分钟）
   */
  monthlyUsedMinutes?: number;

  /**
   * 等级有效期
   */
  levelExpireTime?: string;

  /**
   * 最近消费时间
   */
  lastConsumeTime?: string;

  /**
   * 状态：0-正常 1-禁用
   */
  status?: number;

    /**
     * 日期范围参数
     */
    params?: any;
}



