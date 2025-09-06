package org.dromara.billiards.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.common.tenant.helper.MerchantHolder;
import org.dromara.common.tenant.helper.TenantHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @Description
 * @Author zhangsip
 * @Version V1.0.0
 * @Since 1.0
 * @Date 2025/8/20
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class MiniAppTenantInterceptor implements HandlerInterceptor {

    private final TenantResolver tenantResolver;

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) {
        if (StringUtils.isNotBlank(TenantHelper.getTenantId())) return true;

        String storeId = req.getHeader("X-Store-Id");
        log.debug("X-Store-Id: {}", storeId);
        TenantResolver.TenantMerchant tm = tenantResolver.resolveTenantMerchant(storeId);
        if (tm != null && StringUtils.isNotBlank(tm.tenantId())) {
            log.debug("MiniAppTenantInterceptor set tenantId: {}, merchantId: {}", tm.tenantId(), tm.merchantId());
            TenantHelper.setDynamic(tm.tenantId(), LoginHelper.isLogin());
            if (StringUtils.isNotBlank(tm.merchantId())) {
                MerchantHolder.set(tm.merchantId());
            }
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest req, HttpServletResponse res, Object handler, Exception ex) {
        if (!LoginHelper.isLogin()) {
            TenantHelper.clearDynamic();    // 清 ThreadLocal 动态租户
        }
        MerchantHolder.clearThread();       // 清商户线程上下文（会话值仍保留）
    }

}
