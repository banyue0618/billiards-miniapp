package org.dromara.common.pay.model.v2;

import org.dromara.common.pay.enums.WxApiEnum;

public enum MicroMchApiEnum implements WxApiEnum {

	/**
	 * 申请入驻
	 */
	SUBMIT("/applyment/micro/submit", "申请入驻"),

	/**
	 * 查询申请状态
	 */
	GET_SUBMIT_STATE("/applyment/micro/getstate", "查询申请状态"),

	/**
	 * 查询提现状态
	 */
	QUERY_AUTO_WITH_DRAW_BY_DATE("/fund/queryautowithdrawbydate", "查询提现状态"),

	/**
	 * 修改结算银行卡
	 */
	MODIFY_ARCHIVES("/applyment/micro/modifyarchives", "修改结算银行卡"),

	/**
	 * 修改联系信息
	 */
	MODIFY_CONTACT_INFO("/applyment/micro/modifycontactinfo", "修改联系信息"),

	/**
	 * 关注配置
	 */
	ADD_RECOMMEND_CONF("/secapi/mkt/addrecommendconf", "关注配置"),

	/**
	 * 添加开发配置
	 */
	ADD_SUB_DEV_CONFIG("/secapi/mch/addsubdevconfig", "开发配置"),

	/**
	 * 开发配置查询
	 */
	QUERY_SUB_DEV_CONFIG("/secapi/mch/querysubdevconfig", "开发配置查询"),

	/**
	 * 提交升级申请
	 */
	SUBMIT_UPGRADE("/applyment/micro/submitupgrade", "提交升级申请"),

	/**
	 * 查询升级申请单状态
	 */
	GET_UPGRADE_STATE("/applyment/micro/getupgradestate", "查询升级申请单状态"),

	/**
	 * 服务商帮小微商户重新发起自动提现
	 */
	RE_AUTO_WITH_DRAW_BY_DATE("/fund/reautowithdrawbydate", "服务商帮小微商户重新发起自动提现"),
	;

	/**
	 * 接口URL
	 */
	private final String url;

	/**
	 * 接口描述
	 */
	private final String desc;

	MicroMchApiEnum(String url, String desc) {
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
