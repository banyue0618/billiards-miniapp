package org.dromara.common.pay.model.v2;


import org.dromara.common.pay.enums.WxApiEnum;

public enum EntrustPayApiEnum implements WxApiEnum {

	/**
	 * 公众号纯签约
	 */
	ENTRUST_WEB("/papay/entrustweb", "公众号纯签约"),

	/**
	 * 公众号纯签约（服务商模式）
	 */
	PARTNER_ENTRUST_WEB("/papay/partner/entrustweb", "公众号纯签约（服务商模式）"),

	/**
	 * APP纯签约
	 */
	PRE_ENTRUST_WEB("/papay/preentrustweb", "APP纯签约"),

	/**
	 * APP纯签约（服务商模式）
	 */
	PARTNER_PRE_ENTRUST_WEB("/papay/partner/preentrustweb", "APP纯签约（服务商模式）"),

	/**
	 * H5纯签约
	 */
	H5_ENTRUST_WEB("/papay/h5entrustweb", "H5纯签约"),
	/**
	 * H5纯签约（服务商模式）
	 */
	PARTNER_H5_ENTRUST_WEB("/papay/partner/h5entrustweb", "H5纯签约（服务商模式"),

	/**
	 * 支付中签约
	 */
	PAY_CONTRACT_ORDER("/pay/contractorder", "支付中签约"),

	/**
	 * 查询签约关系
	 */
	QUERY_ENTRUST_CONTRACT("/papay/querycontract", "查询签约关系"),

	/**
	 * 查询签约关系（服务商模式）
	 */
	PARTNER_QUERY_ENTRUST_CONTRACT("/papay/partner/querycontract", "查询签约关系（服务商模式）"),

	/**
	 * 申请扣款
	 */
	PAP_PAY_APPLY("/pay/pappayapply", "申请扣款"),

	/**
	 * 申请扣款（服务商模式）
	 */
	PARTNER_PAP_PAY_APPLY("/pay/partner/pappayapply", "申请扣款（服务商模式）"),

	/**
	 * 代扣申请解约
	 */
	DELETE_ENTRUST_CONTRACT("/papay/deletecontract", "代扣申请解约"),
	/**
	 * 代扣申请解约（服务商模式）
	 */
	PARTNER_DELETE_ENTRUST_CONTRACT("/papay/partner/deletecontract", "代扣申请解约（服务商模式）"),
	;

	/**
	 * 接口URL
	 */
	private final String url;

	/**
	 * 接口描述
	 */
	private final String desc;

	EntrustPayApiEnum(String url, String desc) {
		this.url = url;
		this.desc = desc;
	}

	/**
	 * 获取枚举URL
	 *
	 * @return 枚举编码
	 */
	@Override
	public String getUrl() {
		return url;
	}

	/**
	 * 获取详细的描述信息
	 *
	 * @return 描述信息
	 */
	@Override
	public String getDesc() {
		return desc;
	}

	@Override
	public String toString() {
		return url;
	}
}
