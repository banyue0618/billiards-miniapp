package org.dromara.common.pay.model.v2;

import org.dromara.common.pay.enums.WxApiEnum;

public enum DepositApiEnum implements WxApiEnum {

	/**
	 * 支付押金（JSAPI、APP 下单）
	 */
	PAY("/deposit/unifiedorder", "支付押金（JSAPI、APP 下单）"),

	/**
	 * 支付押金（人脸支付）
	 */
	FACE_PAY("/deposit/facepay", "支付押金（人脸支付）"),

	/**
	 * 支付押金（付款码支付）
	 */
	MICRO_PAY("/deposit/micropay", "支付押金（付款码支付）"),

	/**
	 * 查询订单
	 */
	ORDER_QUERY("/deposit/orderquery", "查询订单"),

	/**
	 * 撤销订单
	 */
	REVERSE("/deposit/reverse", "撤销订单"),

	/**
	 * 消费押金
	 */
	CONSUME("/deposit/consume", "消费押金"),

	/**
	 * 申请退款
	 */
	REFUND("/deposit/refund", "申请退款"),

	/**
	 * 查询退款
	 */
	REFUND_QUERY("deposit/refundquery", "查询退款"),
	;

	/**
	 * 接口URL
	 */
	private final String url;

	/**
	 * 接口描述
	 */
	private final String desc;

	DepositApiEnum(String url, String desc) {
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
