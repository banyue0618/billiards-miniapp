package org.dromara.common.tenant.helper;

import cn.dev33.satoken.stp.StpUtil;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.satoken.utils.LoginHelper;

/**
 * @Description
 * @Author zhangsip
 * @Version V1.0.0
 * @Since 1.0
 * @Date 2025/8/28
 */
public class MerchantHolder {

    public static final String KEY = "merchantId";

    public static String get() {
        // 1) 小程序/线程上下文
        String mid = MerchantContext.get();
        if (StringUtils.isNotBlank(mid)) return mid;

        if(!LoginHelper.isLogin()){
            return null;
        }

        // 2) 后台会话
        var session = StpUtil.getSession(false);
        if (session != null) {
            mid = (String) session.get(KEY);
            if (StringUtils.isNotBlank(mid)) return mid;
        }
        return null;
    }

    public static void set(String merchantId) {
        if(StringUtils.isNotEmpty(merchantId)){
            StpUtil.getSession(false).set(KEY, merchantId);
        }
    }

    public static void clearSession() {
        var session = StpUtil.getSession(false);
        if (session != null) {
            session.delete(KEY);
        }
    }

    public static void clearThread() { MerchantContext.clear(); }
}
