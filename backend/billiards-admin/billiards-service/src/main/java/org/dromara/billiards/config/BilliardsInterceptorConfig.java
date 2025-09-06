package org.dromara.billiards.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Description
 * @Author zhangsip
 * @Version V1.0.0
 * @Since 1.0
 * @Date 2025/8/29
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class BilliardsInterceptorConfig implements WebMvcConfigurer {

    private final MiniAppTenantInterceptor miniAppTenantInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 聚合小程序拦截器
        log.info("注册 MiniAppTenantInterceptor");
        registry.addInterceptor(miniAppTenantInterceptor).addPathPatterns("/api/miniapp/tables/qrcode/**");
    }
}
