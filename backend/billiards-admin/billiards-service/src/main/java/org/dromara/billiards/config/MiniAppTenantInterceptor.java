package org.dromara.billiards.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.dromara.common.tenant.helper.TenantHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @Description (暂未使用)
 * @Author zhangsip
 * @Version V1.0.0
 * @Since 1.0
 * @Date 2025/8/20
 */
@Component
@RequiredArgsConstructor
public class MiniAppTenantInterceptor implements HandlerInterceptor {

    private final TenantResolver tenantResolver;

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) {
        if (StringUtils.isNotBlank(TenantHelper.getTenantId())) return true;

        String storeId = req.getHeader("X-Store-Id");
        String appId = req.getHeader("X-App-Id");

        String tenantId = tenantResolver.resolveTenantId(storeId, appId);
        if (StringUtils.isNotBlank(tenantId)) {
            TenantHelper.setDynamic(tenantId); // 未登录线程内也生效
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest req, HttpServletResponse res, Object handler, Exception ex) {
        TenantHelper.clearDynamic();
    }

}
