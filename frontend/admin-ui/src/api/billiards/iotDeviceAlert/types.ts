export interface IotDeviceAlertVO {
  /**
   * 告警类型：offline/control_failed等
   */
  alertType: string;

  /**
   * 告警级别：info/warning/error/critical
   */
  alertLevel: string;

  /**
   * 设备编号
   */
  deviceCode: string;

  /**
   * 设备名称
   */
  deviceName: string;

  /**
   * 处理状态：pending/resolved/ignored
   */
  status: string;

  /**
   * 处理人
   */
  handler: string;

  /**
   * 处理时间
   */
  handleTime: string;

}

export interface IotDeviceAlertForm extends BaseEntity {
  /**
   * 主键
   */
  id?: string | number;

  /**
   * 告警类型：offline/control_failed等
   */
  alertType?: string;

  /**
   * 告警级别：info/warning/error/critical
   */
  alertLevel?: string;

  /**
   * 设备编号
   */
  deviceCode?: string;

  /**
   * 设备名称
   */
  deviceName?: string;

  /**
   * 告警内容描述
   */
  alertContent?: string;

  /**
   * 告警详细数据（如失败次数、成功率等）
   */
  alertData?: string;

  /**
   * 处理状态：pending/resolved/ignored
   */
  status?: string;

  /**
   * 处理人
   */
  handler?: string;

  /**
   * 处理时间
   */
  handleTime?: string;

  /**
   * 处理备注
   */
  handleRemark?: string;

}

export interface IotDeviceAlertQuery extends PageQuery {

  /**
   * 告警类型：offline/control_failed等
   */
  alertType?: string;

  /**
   * 告警级别：info/warning/error/critical
   */
  alertLevel?: string;

  /**
   * 设备编号
   */
  deviceCode?: string;

  /**
   * 设备名称
   */
  deviceName?: string;

  /**
   * 处理状态：pending/resolved/ignored
   */
  status?: string;

    /**
     * 日期范围参数
     */
    params?: any;
}



