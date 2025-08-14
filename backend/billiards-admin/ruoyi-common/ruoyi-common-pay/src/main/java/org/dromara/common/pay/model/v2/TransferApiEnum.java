package org.dromara.common.pay.model.v2;


import org.dromara.common.pay.enums.WxApiEnum;

public enum TransferApiEnum implements WxApiEnum {

	/**
	 * 企业付款
	 */
	TRANSFER("/mmpaymkttransfers/promotion/transfers", "企业付款"),

	/**
	 * 查询企业付款
	 */
	GET_TRANSFER_INFO("/mmpaymkttransfers/gettransferinfo", "查询企业付款"),

	/**
	 * 付款到银行卡
	 */
	TRANSFER_BANK("/mmpaysptrans/pay_bank", "付款到银行卡"),

	/**
	 * 查询付款到银行卡
	 */
	GET_TRANSFER_BANK_INFO("/mmpaysptrans/query_bank", "查询付款到银行卡"),

	/**
	 * 获取 RSA 加密公钥
	 */
	GET_PUBLIC_KEY("/risk/getpublickey", "获取 RSA 加密公钥"),

	/**
	 * 向员工付款
	 */
	PAY_WWS_TRANS_2_POCKET("/mmpaymkttransfers/promotion/paywwsptrans2pocket", "向员工付款"),

	/**
	 * 查询向员工付款记录
	 */
	QUERY_WWS_TRANS_2_POCKET("/mmpaymkttransfers/promotion/querywwsptrans2pocket", "查询向员工付款记录"),
	;

	/**
	 * 接口URL
	 */
	private final String url;

	/**
	 * 接口描述
	 */
	private final String desc;

	TransferApiEnum(String url, String desc) {
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
