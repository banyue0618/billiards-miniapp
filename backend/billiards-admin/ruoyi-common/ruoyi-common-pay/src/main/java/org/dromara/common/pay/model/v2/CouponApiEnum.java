package org.dromara.common.pay.model.v2;

import org.dromara.common.pay.enums.WxApiEnum;

public enum CouponApiEnum implements WxApiEnum {

	/**
	 * 发放代金券
	 */
	SEND_COUPON("/mmpaymkttransfers/send_coupon", "发放代金券"),

	/**
	 * 查询代金券批次
	 */
	QUERY_COUPON_STOCK("/mmpaymkttransfers/query_coupon_stock", "查询代金券批次"),

	/**
	 * 查询代金券信息
	 */
	QUERY_COUPONS_INFO("/mmpaymkttransfers/querycouponsinfo", "查询代金券信息"),
	;

	/**
	 * 接口URL
	 */
	private final String url;

	/**
	 * 接口描述
	 */
	private final String desc;

	CouponApiEnum(String url, String desc) {
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
