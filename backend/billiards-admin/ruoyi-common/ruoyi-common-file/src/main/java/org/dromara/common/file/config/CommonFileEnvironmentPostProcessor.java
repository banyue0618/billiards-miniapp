package org.dromara.common.file.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.util.List;

/**
 * 自动加载 ruoyi-common-file 模块下的 application-common-file.yml 到 Environment。
 * 这样无需在主应用显式 import，也能使用模块内的独立配置切换实现。
 */
public class CommonFileEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    private static final String RESOURCE_LOCATION = "classpath:application-common-file.yml";
    private static final String PROPERTY_SOURCE_NAME = "ruoyi-common-file";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource resource = resolver.getResource(RESOURCE_LOCATION);
            if (resource.exists()) {
                YamlPropertySourceLoader loader = new YamlPropertySourceLoader();
                List<PropertySource<?>> sources = loader.load(PROPERTY_SOURCE_NAME, resource);
                // 以较低优先级添加，便于主应用或运行环境覆盖
                for (PropertySource<?> ps : sources) {
                    environment.getPropertySources().addLast(ps);
                }
            }
        } catch (Exception ignored) {
            // 忽略加载失败，保持应用可启动；如需排查可在此打印日志
        }
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}


