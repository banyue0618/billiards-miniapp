export interface ReservationVO {
  /**
   * 预约编号（可用于展示或查询）
   */
  reservationNo: string;

  /**
   * 预约开始时间
   */
  startTime: string;

  /**
   * 预约结束时间
   */
  endTime: string;

  /**
   * 状态：0=预约中,1=已到店,2=已完成,3=已取消,4=已过期
   */
  status: number;

  /**
   * 支付状态：0=未支付,1=已支付,2=已退款
   */
  payStatus: number;

  /**
   * 支付金额
   */
  payAmount: number;

  /**
   * 支付时间
   */
  payTime: string;

  /**
   * 备注（如包厢号、特殊说明）
   */
  remark: string;

}

export interface ReservationForm extends BaseEntity {
  /**
   * 主键ID
   */
  id?: string | number;

  /**
   * 预约编号（可用于展示或查询）
   */
  reservationNo?: string;

  /**
   * 预约开始时间
   */
  startTime?: string;

  /**
   * 预约结束时间
   */
  endTime?: string;

  /**
   * 取消时间
   */
  cancelTime?: string;

  /**
   * 备注（如包厢号、特殊说明）
   */
  remark?: string;

}

export interface ReservationQuery extends PageQuery {

  /**
   * 预约编号（可用于展示或查询）
   */
  reservationNo?: string;

  /**
   * 预约开始时间
   */
  startTime?: string;

  /**
   * 预约结束时间
   */
  endTime?: string;

  /**
   * 状态：0=预约中,1=已到店,2=已完成,3=已取消,4=已过期
   */
  status?: number;

  /**
   * 支付状态：0=未支付,1=已支付,2=已退款
   */
  payStatus?: number;

    /**
     * 日期范围参数
     */
    params?: any;
}



