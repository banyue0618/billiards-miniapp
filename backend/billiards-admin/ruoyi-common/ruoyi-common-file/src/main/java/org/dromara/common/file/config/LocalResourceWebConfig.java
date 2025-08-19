package org.dromara.common.file.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

/**
 * 将 resource.storage.local.base-url 映射到 resource.storage.local.base-path，
 * 仅在本地存储模式启用时生效。
 */
@Configuration
@ConditionalOnProperty(prefix = "resource.storage", name = "type", havingValue = "local")
public class LocalResourceWebConfig implements WebMvcConfigurer {

    private static final Logger log = LoggerFactory.getLogger(LocalResourceWebConfig.class);

    private final ResourceStorageProperties storageProperties;

    public LocalResourceWebConfig(ResourceStorageProperties storageProperties) {
        this.storageProperties = storageProperties;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String baseUrl = storageProperties.getLocal().getBaseUrl();
        String basePath = storageProperties.getLocal().getBasePath();

        if (!StringUtils.hasText(baseUrl) || !StringUtils.hasText(basePath)) {
            log.warn("LocalResourceWebConfig 未启用：base-url 或 base-path 未配置。baseUrl={}, basePath={}", baseUrl, basePath);
            return;
        }

        String pattern = baseUrl.endsWith("/") ? baseUrl + "**" : baseUrl + "/**";
        String location = Paths.get(basePath).toUri().toString(); // 转为 file:/ URI

        log.info("注册本地静态资源映射: [{}] -> [{}]", pattern, location);
        registry.addResourceHandler(pattern)
                .addResourceLocations(location);
    }
}


