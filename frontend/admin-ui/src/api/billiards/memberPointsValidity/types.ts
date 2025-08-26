export interface MemberPointsValidityVO {
  /**
   * 记录ID
   */
  id: string | number;

  /**
   * 用户ID
   */
  userId: string | number;

  /**
   * 积分数量
   */
  points: number;

  /**
   * 剩余积分数量
   */
  remainingPoints: number;

  /**
   * 过期时间
   */
  expireTime: string;

}

export interface MemberPointsValidityForm extends BaseEntity {
  /**
   * 记录ID
   */
  id?: string | number;

  /**
   * 用户ID
   */
  userId?: string | number;

  /**
   * 积分数量
   */
  points?: number;

  /**
   * 剩余积分数量
   */
  remainingPoints?: number;

  /**
   * 过期时间
   */
  expireTime?: string;

}

export interface MemberPointsValidityQuery extends PageQuery {

  /**
   * 用户ID
   */
  userId?: string | number;

  /**
   * 积分数量
   */
  points?: number;

  /**
   * 剩余积分数量
   */
  remainingPoints?: number;

  /**
   * 过期时间
   */
  expireTime?: string;

    /**
     * 日期范围参数
     */
    params?: any;
}



