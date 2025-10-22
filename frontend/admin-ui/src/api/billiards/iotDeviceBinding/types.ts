export interface IotDeviceBindingVO {
  /**
   * 主键
   */
  id: string | number;

  /**
   * 业务场景：open_table/close_table/timeout等
   */
  scene: string;

  /**
   * 设备编号（外键）
   */
  deviceCode: string;

  /**
   * 控制命令：turn_on/turn_off/play_audio等
   */
  command: string;

  /**
   * 命令参数（如音量、文件名等）
   */
  params: string;

  /**
   * 是否启用：1启用，0禁用
   */
  enabled: number;

}

export interface IotDeviceBindingForm extends BaseEntity {
  /**
   * 主键
   */
  id?: string | number;

  /**
   * 台桌ID（外键）
   */
  tableId?: string | number;

  /**
   * 门店ID
   */
  storeId?: string | number;

  /**
   * 业务场景：open_table/close_table/timeout等
   */
  scene?: string;

  /**
   * 设备编号（外键）
   */
  deviceCode?: string;

  /**
   * 控制命令：turn_on/turn_off/play_audio等
   */
  command?: string;

  /**
   * 命令参数（如音量、文件名等）
   */
  params?: string;

  /**
   * 执行顺序（同场景多个设备时）
   */
  executeOrder?: number;

  /**
   * 是否启用：1启用，0禁用
   */
  enabled?: number;

}

export interface IotDeviceBindingQuery extends PageQuery {

  /**
   * 业务场景：open_table/close_table/timeout等
   */
  scene?: string;

  /**
   * 设备编号（外键）
   */
  deviceCode?: string;

  /**
   * 控制命令：turn_on/turn_off/play_audio等
   */
  command?: string;

  /**
   * 是否启用：1启用，0禁用
   */
  enabled?: number;

    /**
     * 日期范围参数
     */
    params?: any;
}



