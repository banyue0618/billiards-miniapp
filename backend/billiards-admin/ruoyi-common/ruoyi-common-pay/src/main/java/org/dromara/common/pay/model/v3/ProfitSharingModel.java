package org.dromara.common.pay.model.v3;

import ch.qos.logback.classic.model.ReceiverModel;
import lombok.*;
import lombok.experimental.Accessors;
import org.dromara.common.pay.model.BaseModel;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ProfitSharingModel extends BaseModel {
	/**
	 * 微信支付分配的服务商商户号,兼容V2接口
	 */
	private String mch_id;
	/**
	 * 微信支付分配的子商户号，即分账的出资商户号
	 */
	private String sub_mchid;
	/**
	 * 微信分配的服务商appid
	 */
	private String appid;
	/**
	 * 微信分配的子商户公众账号ID，分账接收方类型包含PERSONAL_SUB_OPENID时必填
	 */
	private String sub_appid;
	/**
	 * 微信支付订单号
	 */
	private String transaction_id;
	/**
	 * 服务商系统内部的分账单号，在服务商系统内部唯一，同一分账单号多次请求等同一次。只能是数字、大小写字母_-|*@
	 */
	private String out_order_no;
	/**
	 * 分账接收方列表，可以设置出资商户作为分账接受方，最多可有50个分账接收方
	 */
	private List<ReceiverModel> receivers;
	/**
	 * 是否解冻剩余未分资金
	 * 1、如果为true，该笔订单剩余未分账的金额会解冻回分账方商户；
	 * 2、如果为false，该笔订单剩余未分账的金额不会解冻回分账方商户，可以对该笔订单再次进行分账。
	 */
	private boolean unfreeze_unsplit;
}
