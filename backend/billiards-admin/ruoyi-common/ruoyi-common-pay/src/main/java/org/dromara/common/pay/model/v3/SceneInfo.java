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
public class SceneInfo {
	/**
	 * 用户终端IP
	 */
	private String payer_client_ip;
	/**
	 * 商户端设备号
	 */
	private String device_id;
	/**
	 * 商户门店信息
	 */
	private StoreInfo store_info;
	/**
	 * H5 场景信息
	 */
	private H5Info h5_info;
}
