package org.dromara.billiards.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.apache.commons.lang3.StringUtils;
import org.dromara.common.tenant.helper.MerchantHolder;

/**
 * 通用的商户范围查询辅助类：
 * - 如当前会话绑定了商户，则在查询条件中追加 merchantId 收敛；
 * - 否则不追加，保持仅租户范围。
 */
public final class MerchantQueryHelper {

    private MerchantQueryHelper() {}

    public static <T> void apply(LambdaQueryWrapper<T> queryWrapper, SFunction<T, ?> merchantIdColumn) {
        String currentMerchantId = MerchantHolder.get();
        if (StringUtils.isNotBlank(currentMerchantId)) {
            queryWrapper.eq(merchantIdColumn, currentMerchantId);
        }
    }
}


