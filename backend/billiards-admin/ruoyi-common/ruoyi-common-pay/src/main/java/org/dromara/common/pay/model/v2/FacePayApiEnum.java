package org.dromara.common.pay.model.v2;

import org.dromara.common.pay.enums.WxApiEnum;

public enum FacePayApiEnum implements WxApiEnum {

	/**
	 * 获取调用凭证
	 */
	GET_AUTH_INFO("/face/get_wxpayface_authinfo", "获取调用凭证"),

	/**
	 * 刷脸支付
	 */
	FACE_PAY("/pay/facepay", "刷脸支付"),

	/**
	 * 查询刷脸支付订单
	 */
	FACE_PAY_QUERY("/pay/facepayqueryy", "查询刷脸支付订单"),

	/**
	 * 撤销刷脸支付订单
	 */
	FACE_PAY_REVERSE("/secapi/pay/facepayreverse", "撤销刷脸支付订单"),
	;

	/**
	 * 接口URL
	 */
	private final String url;

	/**
	 * 接口描述
	 */
	private final String desc;

	FacePayApiEnum(String url, String desc) {
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
