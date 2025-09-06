package org.dromara.billiards.security;

import org.apache.commons.lang3.StringUtils;
import org.dromara.billiards.common.exception.BilliardsException;
import org.dromara.billiards.common.result.ResultCode;
import org.dromara.common.tenant.helper.MerchantHolder;

/**
 * 写操作越权防护：
 * - 如当前会话绑定了商户，则要求目标记录的 merchantId 必须一致；
 * - 否则放行（租户管理员/超管场景）。
 */
public final class MerchantWriteGuard {

    private MerchantWriteGuard() {}

    public static void assertWritable(String recordMerchantId) {
        String currentMerchantId = MerchantHolder.get();
        if (StringUtils.isNotBlank(currentMerchantId) && !StringUtils.equals(currentMerchantId, recordMerchantId)) {
            throw BilliardsException.of(ResultCode.FORBIDDEN, "无权操作其他商户的数据");
        }
    }
}


