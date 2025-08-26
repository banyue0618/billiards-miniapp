package org.dromara.common.file.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @Description
 * @Author zhangsip
 * @Version V1.0.0
 * @Since 1.0
 * @Date 2025/5/9
 */
@Configuration
@PropertySource(value = "classpath:application-common-file.yml", factory = org.dromara.common.core.factory.YmlPropertySourceFactory.class, ignoreResourceNotFound = true)
public class FileStorageAutoConfiguration {
}
