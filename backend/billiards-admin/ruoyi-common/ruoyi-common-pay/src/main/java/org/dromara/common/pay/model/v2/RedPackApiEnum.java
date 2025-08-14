package org.dromara.common.pay.model.v2;

import org.dromara.common.pay.enums.WxApiEnum;

public enum RedPackApiEnum implements WxApiEnum {

	/**
	 * 发放红包
	 */
	SEND_RED_PACK("/mmpaymkttransfers/sendredpack", "发放红包"),

	/**
	 * 发放裂变红包
	 */
	SEND_GROUP_RED_PACK("/mmpaymkttransfers/sendgroupredpack", "发放裂变红包"),

	/**
	 * 查询红包记录
	 */
	GET_HB_INFO("/mmpaymkttransfers/gethbinfo", "查询红包记录"),

	/**
	 * 小程序发红包
	 */
	SEND_MINI_PROGRAM_HB("/mmpaymkttransfers/sendminiprogramhb", "小程序发红包"),

	/**
	 * 发放企业红包
	 */
	SEND_WORK_WX_RED_PACK("/mmpaymkttransfers/sendworkwxredpack", "发放企业红包"),

	/**
	 * 查询企业红包记录
	 */
	QUERY_WORK_WX_RED_PACK("/mmpaymkttransfers/queryworkwxredpack", "查询企业红包记录"),

	;

	/**
	 * 接口URL
	 */
	private final String url;

	/**
	 * 接口描述
	 */
	private final String desc;

	RedPackApiEnum(String url, String desc) {
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
