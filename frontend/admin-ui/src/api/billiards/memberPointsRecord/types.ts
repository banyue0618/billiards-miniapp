export interface MemberPointsRecordVO {
  /**
   * 记录ID
   */
  id: string | number;

  /**
   * 用户ID
   */
  userId: string | number;

  /**
   * 积分数量（正数表示获取，负数表示消耗）
   */
  points: number;

  /**
   * 类型：1-获取 2-消耗
   */
  type: number;

  /**
   * 场景，与积分规则表场景对应
   */
  scene: number;

  /**
   * 对应的规则ID
   */
  ruleId: string | number;

  /**
   * 关联业务ID（如订单ID、活动ID等）
   */
  businessId: string | number;

  /**
   * 积分描述
   */
  description: string;

  /**
   * 过期时间
   */
  expireTime: string;

}

export interface MemberPointsRecordForm extends BaseEntity {
  /**
   * 记录ID
   */
  id?: string | number;

  /**
   * 用户ID
   */
  userId?: string | number;

  /**
   * 积分数量（正数表示获取，负数表示消耗）
   */
  points?: number;

  /**
   * 类型：1-获取 2-消耗
   */
  type?: number;

  /**
   * 场景，与积分规则表场景对应
   */
  scene?: number;

  /**
   * 对应的规则ID
   */
  ruleId?: string | number;

  /**
   * 关联业务ID（如订单ID、活动ID等）
   */
  businessId?: string | number;

  /**
   * 积分描述
   */
  description?: string;

  /**
   * 过期时间
   */
  expireTime?: string;

}

export interface MemberPointsRecordQuery extends PageQuery {

  /**
   * 用户ID
   */
  userId?: string | number;

  /**
   * 积分数量（正数表示获取，负数表示消耗）
   */
  points?: number;

  /**
   * 类型：1-获取 2-消耗
   */
  type?: number;

  /**
   * 场景，与积分规则表场景对应
   */
  scene?: number;

  /**
   * 对应的规则ID
   */
  ruleId?: string | number;

  /**
   * 关联业务ID（如订单ID、活动ID等）
   */
  businessId?: string | number;

  /**
   * 积分描述
   */
  description?: string;

  /**
   * 过期时间
   */
  expireTime?: string;

    /**
     * 日期范围参数
     */
    params?: any;
}



