package org.dromara.common.file.service.factory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.file.config.ResourceStorageProperties;
import org.dromara.common.file.service.ResourceService;
import org.dromara.common.file.service.impl.CdnResourceServiceImpl;
import org.dromara.common.file.service.impl.LocalResourceServiceImpl;
import org.dromara.common.file.service.impl.MinioResourceServiceImpl;
import org.dromara.common.file.service.impl.OssResourceServiceImpl;
// import org.springframework.beans.factory.annotation.Qualifier; // Not strictly needed with constructor injection if beans have names
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 资源服务工厂类
 * 根据配置动态选择并提供相应的ResourceService实例。
 */
@Slf4j
@Service
// @RequiredArgsConstructor // We need to handle optional injection more carefully for conditional beans
public class ResourceServiceFactory {

    private final ResourceStorageProperties storageProperties;

    // Use Optional for conditional beans to avoid startup issues if a bean is not created
    private final Optional<LocalResourceServiceImpl> localResourceServiceOpt;
    private final Optional<MinioResourceServiceImpl> minioResourceServiceOpt;
    private final Optional<OssResourceServiceImpl> ossResourceServiceOpt;
    private final Optional<CdnResourceServiceImpl> cdnResourceServiceOpt;

    private final Map<String, ResourceService> serviceMap = new HashMap<>();

    // Constructor injection with Optional for conditional beans
    public ResourceServiceFactory(ResourceStorageProperties storageProperties,
                                  Optional<LocalResourceServiceImpl> localResourceServiceOpt,
                                  Optional<MinioResourceServiceImpl> minioResourceServiceOpt,
                                  Optional<OssResourceServiceImpl> ossResourceServiceOpt,
                                  @Lazy Optional<CdnResourceServiceImpl> cdnResourceServiceOpt) { // CDN might depend on this factory
        this.storageProperties = storageProperties;
        this.localResourceServiceOpt = localResourceServiceOpt;
        this.minioResourceServiceOpt = minioResourceServiceOpt;
        this.ossResourceServiceOpt = ossResourceServiceOpt;
        this.cdnResourceServiceOpt = cdnResourceServiceOpt;
    }

    @PostConstruct
    public void init() {
        String configuredType = storageProperties.getType().toLowerCase();
        log.info("ResourceServiceFactory initializing with configured storage type: {}", configuredType);

        localResourceServiceOpt.ifPresent(service -> serviceMap.put("local", service));
        minioResourceServiceOpt.ifPresent(service -> serviceMap.put("minio", service));
        ossResourceServiceOpt.ifPresent(service -> serviceMap.put("oss", service));
        cdnResourceServiceOpt.ifPresent(service -> serviceMap.put("cdn", service));

        if (serviceMap.containsKey(configuredType)) {
            log.info("ResourceServiceFactory: {} service selected based on configuration.", configuredType);
        } else {
            log.warn("ResourceServiceFactory: No suitable ResourceService bean found or active for configured type '{}'. Available beans in map: {}. Factory might not work as expected.", configuredType, serviceMap.keySet());
        }
    }

    /**
     * 根据配置文件中的 resource.storage.type 获取对应的 ResourceService 实例。
     *
     * @return 激活的 ResourceService 实例。
     * @throws IllegalArgumentException 如果配置的 storage-type 不支持或对应的服务未初始化/激活。
     */
    public ResourceService getService() {
        String storageType = storageProperties.getType().toLowerCase();
        ResourceService service = serviceMap.get(storageType);
        if (service == null) {
            log.error("Unsupported or uninitialized/inactive storage type: '{}'. Check your configuration and ensure the corresponding bean (e.g., LocalResourceServiceImpl for 'local') is active. Available services in map: {}", storageType, serviceMap.keySet());
            throw new IllegalArgumentException("Unsupported or uninitialized/inactive storage type: " + storageProperties.getType());
        }
        return service;
    }

    /**
     * 根据指定的类型获取 ResourceService 实例。
     * 注意：这通常用于CDN服务需要获取其后端存储服务实例的场景。
     * 普通业务调用应使用 getService() 来获取当前配置的全局服务。
     *
     * @param type 存储类型 ("local", "minio", "oss")
     * @return 对应类型的 ResourceService 实例。
     * @throws IllegalArgumentException 如果指定的类型不支持或对应的服务未初始化/激活。
     */
    public ResourceService getService(String type) {
        if (type == null) {
            throw new IllegalArgumentException("Storage type cannot be null when directly requesting a service instance.");
        }
        String normalizedType = type.toLowerCase();
        ResourceService specificService = null;

        switch (normalizedType) {
            case "local":
                specificService = localResourceServiceOpt.orElse(null);
                break;
            case "minio":
                specificService = minioResourceServiceOpt.orElse(null);
                break;
            case "oss":
                specificService = ossResourceServiceOpt.orElse(null);
                break;
            // CDN is not a backend type to be fetched this way, it uses one of these.
            default:
                log.error("Unsupported storage backend type requested directly: {}", type);
                throw new IllegalArgumentException("Unsupported storage backend type for direct retrieval: " + type);
        }

        if (specificService == null) {
            log.error("Requested storage service of type '{}' is not available or not initialized/active.", type);
            throw new IllegalArgumentException("Storage service of type '" + type + "' is not available/initialized/active.");
        }
        return specificService;
    }
} 