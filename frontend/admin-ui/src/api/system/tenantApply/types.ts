export interface TenantApplyVO {
  /**
   * 主键
   */
  id: string | number;

  /**
   * 申请单号
   */
  applyNo: string;

  /**
   * 公司/机构名称
   */
  companyName: string;

  /**
   * 统一社会信用代码
   */
  creditCode: string;

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
   * 省
   */
  province: string;

  /**
   * 市
   */
  city: string;

  /**
   * 区/县
   */
  district: string;

  /**
   * 详细地址
   */
  address: string;

  /**
   * 营业执照附件(资源ID/URL)
   */
  businessLicense: string;

  /**
   * 租户套餐ID（可选）
   */
  packageId: string | number;

  /**
   * 预计账户数(可选，用于套餐评估)
   */
  expectedUsers: number;

  /**
   * 备注/补充说明
   */
  remark: string;

  /**
   * 状态:0待审 1通过 2驳回
   */
  status: number;

  /**
   * 审核人ID（sys_user）
   */
  auditBy: number;

  /**
   * 审核时间
   */
  auditTime: string;

}

export interface TenantApplyForm extends BaseEntity {
  /**
   * 主键
   */
  id?: string | number;

  /**
   * 申请单号
   */
  applyNo?: string;

  /**
   * 公司/机构名称
   */
  companyName?: string;

  /**
   * 统一社会信用代码
   */
  creditCode?: string;

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
   * 省
   */
  province?: string;

  /**
   * 市
   */
  city?: string;

  /**
   * 区/县
   */
  district?: string;

  /**
   * 详细地址
   */
  address?: string;

  /**
   * 营业执照附件(资源ID/URL)
   */
  businessLicense?: string;

    /**
   * 营业执照附件(资源ID/URL)
   */
    businessLicenseUrl?: string;

  /**
   * 租户套餐ID
   */
  packageId?: string | number;

  /**
   * 备注/补充说明
   */
  remark?: string;

}

export interface TenantApplyQuery extends PageQuery {

  /**
   * 申请单号
   */
  applyNo?: string;

  /**
   * 公司/机构名称
   */
  companyName?: string;

  /**
   * 统一社会信用代码
   */
  creditCode?: string;

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
   * 状态:0待审 1通过 2驳回
   */
  status?: number;

    /**
     * 日期范围参数
     */
    params?: any;
}



