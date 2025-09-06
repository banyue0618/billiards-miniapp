export interface MemberLevelConfigVO {
  /**
   * 配置ID
   */
  id: string | number;

  /**
   * 等级编码
   */
  levelCode: number;

  /**
   * 等级名称
   */
  levelName: string;

  /**
   * 所需累计消费金额
   */
  requiredAmount: number;

  /**
   * 折扣率
   */
  discount: number;

  /**
   * 每月赠送时长（分钟）
   */
  monthlyFreeMinutes: number;

  /**
   * 积分获取倍率
   */
  pointsMultiplier: number;

  /**
   * 生日特权折扣率
   */
  birthdayDiscount: number;

  /**
   * 可带朋友享受会员价的人数
   */
  friendPrivilegeCount: number;

  /**
   * 专属客服服务 0-否 1-是
   */
  vipService: number;

  /**
   * 预约特权 0-否 1-是
   */
  reservationPrivilege: number;

  /**
   * 等级图标
   */
  levelIcon: string;

  /**
   * 等级背景图
   */
  levelBackground: string;

  /**
   * 等级描述
   */
  description: string;

  /**
   * 状态 0-启用 1-禁用
   */
  status: number;

}

export interface MemberLevelConfigForm extends BaseEntity {
  /**
   * 配置ID
   */
  id?: string | number;

  /**
   * 等级编码
   */
  levelCode?: number;

  /**
   * 等级名称
   */
  levelName?: string;

  /**
   * 所需累计消费金额
   */
  requiredAmount?: number;

  /**
   * 折扣率
   */
  discount?: number;

  /**
   * 每月赠送时长（分钟）
   */
  monthlyFreeMinutes?: number;

  /**
   * 积分获取倍率
   */
  pointsMultiplier?: number;

  /**
   * 生日特权折扣率
   */
  birthdayDiscount?: number;

  /**
   * 可带朋友享受会员价的人数
   */
  friendPrivilegeCount?: number;

  /**
   * 专属客服服务 0-否 1-是
   */
  vipService?: number;

  /**
   * 预约特权 0-否 1-是
   */
  reservationPrivilege?: number;

  /**
   * 等级图标
   */
  levelIcon?: string;

  /**
   * 等级背景图
   */
  levelBackground?: string;

  /**
   * 等级描述
   */
  description?: string;

  /**
   * 状态 0-启用 1-禁用
   */
  status?: number;

}

export interface MemberLevelConfigQuery extends PageQuery {

  /**
   * 等级编码
   */
  levelCode?: number;

  /**
   * 等级名称
   */
  levelName?: string;

  /**
   * 所需累计消费金额
   */
  requiredAmount?: number;

  /**
   * 折扣率
   */
  discount?: number;

  /**
   * 每月赠送时长（分钟）
   */
  monthlyFreeMinutes?: number;

  /**
   * 积分获取倍率
   */
  pointsMultiplier?: number;

  /**
   * 生日特权折扣率
   */
  birthdayDiscount?: number;

  /**
   * 可带朋友享受会员价的人数
   */
  friendPrivilegeCount?: number;

  /**
   * 专属客服服务 0-否 1-是
   */
  vipService?: number;

  /**
   * 预约特权 0-否 1-是
   */
  reservationPrivilege?: number;

  /**
   * 等级图标
   */
  levelIcon?: string;

  /**
   * 等级背景图
   */
  levelBackground?: string;

  /**
   * 等级描述
   */
  description?: string;

  /**
   * 状态 0-启用 1-禁用
   */
  status?: number;

    /**
     * 日期范围参数
     */
    params?: any;
}



