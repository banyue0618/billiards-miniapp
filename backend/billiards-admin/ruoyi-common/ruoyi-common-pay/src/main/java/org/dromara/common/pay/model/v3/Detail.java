package org.dromara.common.pay.model.v3;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>V3 统一下单-优惠功能</p>
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(chain = true)
public class Detail {
	/**
	 * 订单原价
	 */
	private BigDecimal cost_price;
	/**
	 * 商品小票ID
	 */
	private String invoice_id;
	/**
	 * 单品列表
	 */
	private List<GoodsDetail> goods_detail;
}
