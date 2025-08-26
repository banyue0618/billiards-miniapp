package org.dromara.common.pay.model.v2;

import org.dromara.common.pay.enums.WxApiEnum;

public enum CustomDeclareApiEnum implements WxApiEnum {
	/**
	 * 订单附加信息提交接口
	 */
	CUSTOM_DECLARE_ORDER("/cgi-bin/mch/customs/customdeclareorder", "订单附加信息提交接口"),

	/**
	 * 订单附加信息查询接口
	 */
	CUSTOM_DECLARE_QUERY("/cgi-bin/mch/customs/customdeclarequery", "订单附加信息查询接口"),

	/**
	 * 订单附加信息重推接口
	 */
	CUSTOM_DECLARE_RE_DECLARE("/cgi-bin/mch/newcustoms/customdeclareredeclare", "订单附加信息重推接口"),
	;

	/**
	 * 接口URL
	 */
	private final String url;

	/**
	 * 接口描述
	 */
	private final String desc;

	CustomDeclareApiEnum(String url, String desc) {
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
