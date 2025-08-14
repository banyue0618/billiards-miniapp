package org.dromara.common.pay.model.v2;

import org.dromara.common.pay.enums.WxApiEnum;

public enum ProfitSharingApiEnum implements WxApiEnum {

	/**
	 * 请求单次分账
	 */
	PROFIT_SHARING("/secapi/pay/profitsharing", "请求单次分账"),

	/**
	 * 请求多次分账
	 */
	MULTI_PROFIT_SHARING("/secapi/pay/multiprofitsharing", "请求多次分账"),

	/**
	 * 查询分账结果
	 */
	PROFIT_SHARING_QUERY("/pay/profitsharingquery", "查询分账结果"),

	/**
	 * 添加分账接收方
	 */
	PROFIT_SHARING_ADD_RECEIVER("/pay/profitsharingaddreceiver", "添加分账接收方"),

	/**
	 * 删除分账接收方
	 */
	PROFIT_SHARING_REMOVE_RECEIVER("/pay/profitsharingremovereceiver", "删除分账接收方"),

	/**
	 * 完结分账
	 */
	PROFIT_SHARING_FINISH("/secapi/pay/profitsharingfinish", "完结分账"),

	/**
	 * 查询订单待分账金额
	 */
	PROFIT_SHARING_ORDER_AMOUNT_QUERY("/pay/profitsharingorderamountquery", "查询订单待分账金额"),

	/**
	 * 分账回退
	 */
	PROFIT_SHARING_RETURN("/secapi/pay/profitsharingreturn", "分账回退"),

	/**
	 * 分账回退结果查询
	 */
	PROFIT_SHARING_RETURN_QUERY("/pay/profitsharingreturnquery", "分账回退结果查询"),
	;

	/**
	 * 接口URL
	 */
	private final String url;

	/**
	 * 接口描述
	 */
	private final String desc;

	ProfitSharingApiEnum(String url, String desc) {
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
