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
   * 规则类型
   */
  type: number;

  /**
   * 积分场景
   */
  scene: number;

  /**
   * 积分值类型
   */
  valueType: number;

  /**
   * 生效时间
   */
  effectiveTime: string;

  /**
   * 失效时间
   */
  expireTime: string;

  /**
   * 状态
   */
  status: number;

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
   * 规则类型
   */
  type?: number;

  /**
   * 积分场景
   */
  scene?: number;

  /**
   * 积分值类型
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
   * 是否参与活动加成
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
   * 规则类型
   */
  type?: number;

  /**
   * 积分场景
   */
  scene?: number;

  /**
   * 生效时间
   */
  effectiveTime?: string;

  /**
   * 失效时间
   */
  expireTime?: string;

  /**
   * 状态
   */
  status?: number;

  /**
   * 是否参与活动加成
   */
  enableActivityBonus?: number;

    /**
     * 日期范围参数
     */
    params?: any;
}



