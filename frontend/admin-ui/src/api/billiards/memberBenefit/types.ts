export interface MemberBenefitVO {
  /**
   * 权益ID
   */
  id: string | number;

  /**
   * 权益名称
   */
  name: string;

  /**
   * 权益类型：1-折扣 2-赠送 3-积分 4-特权
   */
  type: number;

  /**
   * 适用等级编码，多个用逗号分隔
   */
  applicableLevels: string;

  /**
   * 权益值（如折扣率、赠送时长、积分倍率等）
   */
  benefitValue: string;

  /**
   * 权益规则（JSON格式，存储具体规则配置）
   */
  benefitRules: string;

  /**
   * 生效时间
   */
  effectiveTime: string;

  /**
   * 失效时间
   */
  expireTime: string;

  /**
   * 权益图标
   */
  icon: string;

  /**
   * 权益描述
   */
  description: string;

  /**
   * 使用说明
   */
  instructions: string;

  /**
   * 状态：0-启用 1-禁用
   */
  status: number;

  /**
   * 排序号
   */
  sortOrder: number;

  /**
   * 是否限时权益：0-永久 1-限时
   */
  isLimited: number;

  /**
   * 是否节日特权：0-否 1-是
   */
  isHoliday: string | number;

  /**
   * 权益标签，多个用逗号分隔
   */
  tags: string;

}

export interface MemberBenefitForm extends BaseEntity {
  /**
   * 权益ID
   */
  id?: string | number;

  /**
   * 权益名称
   */
  name?: string;

  /**
   * 权益类型：1-折扣 2-赠送 3-积分 4-特权
   */
  type?: number;

  /**
   * 适用等级编码，多个用逗号分隔
   */
  applicableLevels?: string;

  /**
   * 权益值（如折扣率、赠送时长、积分倍率等）
   */
  benefitValue?: string;

  /**
   * 权益规则（JSON格式，存储具体规则配置）
   */
  benefitRules?: string;

  /**
   * 生效时间
   */
  effectiveTime?: string;

  /**
   * 失效时间
   */
  expireTime?: string;

  /**
   * 权益图标
   */
  icon?: string;

  /**
   * 权益描述
   */
  description?: string;

  /**
   * 使用说明
   */
  instructions?: string;

  /**
   * 状态：0-启用 1-禁用
   */
  status?: number;

  /**
   * 排序号
   */
  sortOrder?: number;

  /**
   * 是否限时权益：0-永久 1-限时
   */
  isLimited?: number;

  /**
   * 是否节日特权：0-否 1-是
   */
  isHoliday?: string | number;

  /**
   * 权益标签，多个用逗号分隔
   */
  tags?: string;

}

export interface MemberBenefitQuery extends PageQuery {

  /**
   * 权益名称
   */
  name?: string;

  /**
   * 权益类型：1-折扣 2-赠送 3-积分 4-特权
   */
  type?: number;

  /**
   * 适用等级编码，多个用逗号分隔
   */
  applicableLevels?: string;

  /**
   * 权益值（如折扣率、赠送时长、积分倍率等）
   */
  benefitValue?: string;

  /**
   * 权益规则（JSON格式，存储具体规则配置）
   */
  benefitRules?: string;

  /**
   * 生效时间
   */
  effectiveTime?: string;

  /**
   * 失效时间
   */
  expireTime?: string;

  /**
   * 权益图标
   */
  icon?: string;

  /**
   * 权益描述
   */
  description?: string;

  /**
   * 使用说明
   */
  instructions?: string;

  /**
   * 状态：0-启用 1-禁用
   */
  status?: number;

  /**
   * 排序号
   */
  sortOrder?: number;

  /**
   * 是否限时权益：0-永久 1-限时
   */
  isLimited?: number;

  /**
   * 是否节日特权：0-否 1-是
   */
  isHoliday?: string | number;

  /**
   * 权益标签，多个用逗号分隔
   */
  tags?: string;

    /**
     * 日期范围参数
     */
    params?: any;
}



