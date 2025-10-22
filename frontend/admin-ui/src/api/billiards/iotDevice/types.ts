export interface IotDeviceVO {
  /**
   * 设备唯一编号
   */
  code: string;

  /**
   * 设备名称
   */
  name: string;

  /**
   * 设备类型：light/lock/speaker/other
   */
  type: string;

  /**
   * 协议类型：mqtt/http/modbus
   */
  protocol: string;

  /**
   * 协议配置（topic/ip/port等）
   */
  protocolConfig: string;

  /**
   * 设备状态：online/offline/error
   */
  status: string;

  /**
   * 最后心跳时间
   */
  lastHeartbeat: string;

  /**
   * 备注信息
   */
  remark: string;

}

export interface IotDeviceForm extends BaseEntity {
  /**
   * 主键
   */
  id?: string | number;

  /**
   * 设备唯一编号
   */
  code?: string;

  /**
   * 设备名称
   */
  name?: string;

  /**
   * 设备类型：light/lock/speaker/other
   */
  type?: string;

  /**
   * 协议类型：mqtt/http/modbus
   */
  protocol?: string;

  /**
   * 协议配置（topic/ip/port等）
   */
  protocolConfig?: string;

  /**
   * 设备状态：online/offline/error
   */
  status?: string;

  /**
   * 最后心跳时间
   */
  lastHeartbeat?: string;

  /**
   * 备注信息
   */
  remark?: string;

}

export interface IotDeviceQuery extends PageQuery {

  /**
   * 设备唯一编号
   */
  code?: string;

  /**
   * 设备名称
   */
  name?: string;

  /**
   * 设备类型：light/lock/speaker/other
   */
  type?: string;

  /**
   * 协议类型：mqtt/http/modbus
   */
  protocol?: string;

  /**
   * 协议配置（topic/ip/port等）
   */
  protocolConfig?: string;

  /**
   * 设备状态：online/offline/error
   */
  status?: string;

  /**
   * 最后心跳时间
   */
  lastHeartbeat?: string;

    /**
     * 日期范围参数
     */
    params?: any;
}



