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
public class StoreInfo {
	/**
	 * 门店编号
	 */
	private String id;
	/**
	 * 门店名称
	 */
	private String name;
	/**
	 * 地区编码
	 */
	private String area_code;
	/**
	 * 详细地址
	 */
	private String address;
}
