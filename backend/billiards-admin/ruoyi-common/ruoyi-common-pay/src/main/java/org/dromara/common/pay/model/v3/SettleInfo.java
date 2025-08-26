package org.dromara.common.pay.model.v3;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(chain = true)
public class SettleInfo {
	/**
	 * 是否指定分账
	 */
	private boolean profit_sharing;
	/**
	 * 补差金额
	 */
	private Integer subsidy_amount;
}
