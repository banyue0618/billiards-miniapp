package org.dromara.common.pay.model.v3;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * <p>V3 统一下单-支付者</p>
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(chain = true)
public class Payer {
	/**
	 * 用户标识
	 */
	private String openid;
	/**
	 * 用户服务标识
	 */
	private String sp_openid;
	/**
	 * 用户子标识
	 */
	private String sub_openid;
}
