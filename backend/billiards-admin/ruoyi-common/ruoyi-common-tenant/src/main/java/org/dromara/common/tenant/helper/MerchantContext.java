package org.dromara.common.tenant.helper;

/**
 * @Description
 * @Author zhangsip
 * @Version V1.0.0
 * @Since 1.0
 * @Date 2025/8/28
 */
public final class MerchantContext {

    private static final ThreadLocal<String> HOLDER = new ThreadLocal<>();

    public static void set(String merchantId) { HOLDER.set(merchantId); }

    public static String get() { return HOLDER.get(); }

    public static void clear() { HOLDER.remove(); }

}
