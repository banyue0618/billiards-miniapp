export interface MerchantApplyVO {
  /**
   * 主键
   */
  id: string | number;

  /**
   * 申请单号
   */
  applyNo: string;

  /**
   * 商户名称
   */
  name: string;

  /**
   * 联系人姓名
   */
  contactName: string;

  /**
   * 联系人手机号
   */
  contactPhone: string;

  /**
   * 联系人邮箱
   */
  contactEmail: string;

  /**
   * Logo 资源ID/URL
   */
  logo: string;

  /**
   * 营业执照附件(资源ID/URL)
   */
  businessLicense: string;

  /**
   * 备注/补充说明
   */
  remark: string;

  /**
   * 状态:0待审 1通过 2驳回
   */
  status: number;

}

export interface MerchantApplyForm extends BaseEntity {
  /**
   * 主键
   */
  id?: string | number;

  /**
   * 商户名称
   */
  name?: string;

  /**
   * 微信商户号(选填或后置提交)
   */
  wxMchId?: string | number;

  /**
   * 联系人姓名
   */
  contactName?: string;

  /**
   * 联系人手机号
   */
  contactPhone?: string;

  /**
   * 联系人邮箱
   */
  contactEmail?: string;

  /**
   * 
   */
  province?: string;

  /**
   * 
   */
  city?: string;

  /**
   * 
   */
  district?: string;

  /**
   * 
   */
  address?: string;

  /**
   * Logo 资源ID/URL
   */
  logo?: string;

  /**
   * 营业执照附件(资源ID/URL)
   */
  businessLicense?: string;

  /**
   * 结算-账户名
   */
  bankAccountName?: string;

  /**
   * 结算-账号
   */
  bankAccountNo?: string;

  /**
   * 结算-开户行
   */
  bankName?: string;

  /**
   * 备注/补充说明
   */
  remark?: string;

}

export interface MerchantApplyQuery extends PageQuery {

  /**
   * 申请单号
   */
  applyNo?: string;

  /**
   * 商户名称
   */
  name?: string;

  /**
   * 联系人姓名
   */
  contactName?: string;

  /**
   * 联系人手机号
   */
  contactPhone?: string;

  /**
   * 状态:0待审 1通过 2驳回
   */
  status?: number;

    /**
     * 日期范围参数
     */
    params?: any;
}



