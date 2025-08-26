export interface PointsRuleVO {
  /**
   * 规则ID
   */
  id: string | number;

  /**
   * 规则名称
   */
  name: string;

  /**
   * 规则类型：1-获取 2-消耗
   */
  type: number;

  /**
   * 积分场景：获取场景：1-消费 2-签到 3-活动 4-评价 5-首次绑定 6-邀请好友；消耗场景：1-抵扣 2-兑换商品 3-兑换优惠券
   */
  scene: number;

  /**
   * 积分值类型：1-固定值 2-比例值
   */
  valueType: number;

  /**
   * 积分值
   */
  pointsValue: number;

  /**
   * 封顶积分值（0表示不封顶）
   */
  maxPoints: number;

  /**
   * 规则配置（JSON格式，存储具体规则）
   */
  ruleConfig: string;

  /**
   * 等级加成配置（JSON格式，存储各等级的加成比例）
   */
  levelBonus: string;

  /**
   * 时段加成配置（JSON格式，存储特殊时段的加成比例）
   */
  timeBonus: string;

  /**
   * 生效时间
   */
  effectiveTime: string;

  /**
   * 失效时间
   */
  expireTime: string;

  /**
   * 规则描述
   */
  description: string;

  /**
   * 状态：0-启用 1-禁用
   */
  status: number;

  /**
   * 是否参与活动加成：0-否 1-是
   */
  enableActivityBonus: number;

  /**
   * 积分有效期（天）：0表示永久有效
   */
  validityDays: string | number;

}

export interface PointsRuleForm extends BaseEntity {
  /**
   * 规则ID
   */
  id?: string | number;

  /**
   * 规则名称
   */
  name?: string;

  /**
   * 规则类型：1-获取 2-消耗
   */
  type?: number;

  /**
   * 积分场景：获取场景：1-消费 2-签到 3-活动 4-评价 5-首次绑定 6-邀请好友；消耗场景：1-抵扣 2-兑换商品 3-兑换优惠券
   */
  scene?: number;

  /**
   * 积分值类型：1-固定值 2-比例值
   */
  valueType?: number;

  /**
   * 积分值
   */
  pointsValue?: number;

  /**
   * 封顶积分值（0表示不封顶）
   */
  maxPoints?: number;

  /**
   * 规则配置（JSON格式，存储具体规则）
   */
  ruleConfig?: string;

  /**
   * 等级加成配置（JSON格式，存储各等级的加成比例）
   */
  levelBonus?: string;

  /**
   * 时段加成配置（JSON格式，存储特殊时段的加成比例）
   */
  timeBonus?: string;

  /**
   * 生效时间
   */
  effectiveTime?: string;

  /**
   * 失效时间
   */
  expireTime?: string;

  /**
   * 规则描述
   */
  description?: string;

  /**
   * 状态：0-启用 1-禁用
   */
  status?: number;

  /**
   * 是否参与活动加成：0-否 1-是
   */
  enableActivityBonus?: number;

  /**
   * 积分有效期（天）：0表示永久有效
   */
  validityDays?: string | number;

}

export interface PointsRuleQuery extends PageQuery {

  /**
   * 规则名称
   */
  name?: string;

  /**
   * 规则类型：1-获取 2-消耗
   */
  type?: number;

  /**
   * 积分场景：获取场景：1-消费 2-签到 3-活动 4-评价 5-首次绑定 6-邀请好友；消耗场景：1-抵扣 2-兑换商品 3-兑换优惠券
   */
  scene?: number;

  /**
   * 积分值类型：1-固定值 2-比例值
   */
  valueType?: number;

  /**
   * 积分值
   */
  pointsValue?: number;

  /**
   * 封顶积分值（0表示不封顶）
   */
  maxPoints?: number;

  /**
   * 规则配置（JSON格式，存储具体规则）
   */
  ruleConfig?: string;

  /**
   * 等级加成配置（JSON格式，存储各等级的加成比例）
   */
  levelBonus?: string;

  /**
   * 时段加成配置（JSON格式，存储特殊时段的加成比例）
   */
  timeBonus?: string;

  /**
   * 生效时间
   */
  effectiveTime?: string;

  /**
   * 失效时间
   */
  expireTime?: string;

  /**
   * 规则描述
   */
  description?: string;

  /**
   * 状态：0-启用 1-禁用
   */
  status?: number;

  /**
   * 是否参与活动加成：0-否 1-是
   */
  enableActivityBonus?: number;

  /**
   * 积分有效期（天）：0表示永久有效
   */
  validityDays?: string | number;

    /**
     * 日期范围参数
     */
    params?: any;
}



