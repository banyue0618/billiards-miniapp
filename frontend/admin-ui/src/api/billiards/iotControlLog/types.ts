export interface IotControlLogVO {
  /**
   * 设备编号
   */
  deviceCode: string;

  /**
   * 控制命令
   */
  command: string;

  /**
   * 触发场景：open_table/close_table等
   */
  triggerScene: string;

  /**
   * 执行状态：success/failed/timeout/pending
   */
  status: string;

  /**
   * 执行时间
   */
  executeTime: string;

  /**
   * 响应耗时（毫秒）
   */
  responseTime: number;

}

export interface IotControlLogForm extends BaseEntity {
  /**
   * 主键
   */
  id?: string | number;

  /**
   * 设备编号
   */
  deviceCode?: string;

  /**
   * 控制命令
   */
  command?: string;

  /**
   * 命令参数
   */
  params?: string;

  /**
   * 触发来源：order/admin/system
   */
  triggerBy?: string;

  /**
   * 触发场景：open_table/close_table等
   */
  triggerScene?: string;

  /**
   * 关联订单ID
   */
  orderId?: string | number;

  /**
   * 关联台桌ID
   */
  tableId?: string | number;

  /**
   * 执行状态：success/failed/timeout/pending
   */
  status?: string;

  /**
   * 失败原因
   */
  errorMsg?: string;

  /**
   * 重试次数
   */
  retryCount?: number;

  /**
   * 执行时间
   */
  executeTime?: string;

  /**
   * 响应耗时（毫秒）
   */
  responseTime?: number;

}

export interface IotControlLogQuery extends PageQuery {

  /**
   * 设备编号
   */
  deviceCode?: string;

  /**
   * 触发场景：open_table/close_table等
   */
  triggerScene?: string;

  /**
   * 执行状态：success/failed/timeout/pending
   */
  status?: string;

    /**
     * 日期范围参数
     */
    params?: any;
}



