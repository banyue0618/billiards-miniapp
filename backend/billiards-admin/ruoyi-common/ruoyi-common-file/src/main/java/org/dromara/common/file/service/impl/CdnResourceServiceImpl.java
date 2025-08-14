package org.dromara.common.file.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.file.config.CdnStorageProperties;
import org.dromara.common.file.config.ResourceStorageProperties;
import org.dromara.common.file.enums.ResourceType;
import org.dromara.common.file.service.ResourceService;
import org.dromara.common.file.service.factory.ResourceServiceFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.util.Map;

/**
 * CDN 资源服务实现类。
 * CDN 本身不存储文件，它作为代理和缓存层，加速对后端存储（如OSS, MinIO）的访问。
 * 因此，上传、删除等写操作会直接透传到后端存储服务。
 * 获取URL操作会返回CDN域名拼接的URL。
 * 生成预签名URL通常也由后端存储服务处理，CDN可能支持也可能不支持基于签名的访问，取决于CDN配置。
 */
@Slf4j
@Service("cdnResourceService")
@ConditionalOnProperty(name = "resource.storage.type", havingValue = "cdn")
public class CdnResourceServiceImpl implements ResourceService {

    private final CdnStorageProperties cdnProperties;
    // private final ResourceStorageProperties storageProperties; // No longer needed directly
    private final ResourceServiceFactory resourceServiceFactory; // Used to get the backend service

    private ResourceService backendStorageService; // Actual backend storage service

    // Use @Lazy on ResourceServiceFactory to break potential circular dependencies
    // CdnResourceServiceImpl -> ResourceServiceFactory -> (potentially back to CdnResourceServiceImpl if factory initializes all eagerly)
    public CdnResourceServiceImpl(ResourceStorageProperties resourceStorageProperties,
                                  @Lazy ResourceServiceFactory resourceServiceFactory) {
        this.cdnProperties = resourceStorageProperties.getCdn();
        this.resourceServiceFactory = resourceServiceFactory;
    }

    @PostConstruct
    public void init() {
        if (StringUtils.isBlank(cdnProperties.getDomain())) {
            log.warn("CDN域名(resource.storage.cdn.domain)未配置，CDN getResourceUrl可能无法按预期工作。");
        }
        String backendType = cdnProperties.getStorageBackendType();
        if (StringUtils.isBlank(backendType)) {
            log.error("CDN后端存储类型(resource.storage.cdn.storage-backend-type)未配置，CDN服务无法工作。");
            return; // Cannot proceed without a backend type
        }
        try {
            // Fetch the specific backend service instance from the factory
            this.backendStorageService = resourceServiceFactory.getService(backendType);
            log.info("CDN服务初始化成功，使用后端存储类型: '{}', CDN域名: {}", backendType, cdnProperties.getDomain());
        } catch (Exception e) {
            log.error("CDN服务初始化失败：无法获取后端存储服务 '{}'. Error: {}", backendType, e.getMessage(), e);
            this.backendStorageService = null; // Ensure it's null if init fails
        }
    }

    private void ensureBackendServiceAvailable() {
        if (backendStorageService == null) {
            log.error("CDN后端存储服务 ('{}') 未初始化或配置错误。请检查CDN配置中的storage-backend-type及对应后端的配置。", cdnProperties.getStorageBackendType());
            throw new IllegalStateException("CDN后端存储服务未初始化或配置错误，无法执行操作。");
        }
    }

    @Override
    public String uploadResource(MultipartFile file, ResourceType resourceType) throws Exception {
        ensureBackendServiceAvailable();
        return backendStorageService.uploadResource(file, resourceType);
    }

    @Override
    public String uploadResource(InputStream inputStream, String originalFilename, ResourceType resourceType) throws Exception {
        ensureBackendServiceAvailable();
        return backendStorageService.uploadResource(inputStream, originalFilename, resourceType);
    }

    @Override
    public String getResourceUrl(String resourceId, ResourceType resourceType) {
        if (StringUtils.isBlank(resourceId)) {
            return null;
        }
        // Always prioritize CDN domain if available for URL generation
        if (StringUtils.isNotBlank(cdnProperties.getDomain())) {
            String domain = cdnProperties.getDomain();
            String cleanDomain = domain.endsWith("/") ? domain.substring(0, domain.length() - 1) : domain;
            String cleanResourceId = resourceId.startsWith("/") ? resourceId.substring(1) : resourceId;
            return cleanDomain + "/" + cleanResourceId;
        } else {
            log.warn("CDN域名未配置 for resourceId: {}. 将尝试从后端存储 '{}' 获取URL (如果后端支持)。", resourceId, cdnProperties.getStorageBackendType());
            ensureBackendServiceAvailable(); // ensure backend is available before calling it
            return backendStorageService.getResourceUrl(resourceId, resourceType);
        }
    }

    @Override
    public String uploadImage(MultipartFile file, ResourceType resourceType) throws Exception {
        ensureBackendServiceAvailable();
        return backendStorageService.uploadImage(file, resourceType);
    }

    @Override
    public String uploadImage(InputStream inputStream, String originalFilename, ResourceType resourceType) throws Exception {
        ensureBackendServiceAvailable();
        return backendStorageService.uploadImage(inputStream, originalFilename, resourceType);
    }

    @Override
    public String uploadBytes(byte[] imageBytes, String fileName, ResourceType resourceType) throws Exception {
        ensureBackendServiceAvailable();
        return backendStorageService.uploadBytes(imageBytes, fileName, resourceType);
    }

    @Override
    public String getImageUrl(String resourceId, ResourceType resourceType, String imageProcessingParams) {
        if (StringUtils.isBlank(resourceId)) {
            return null;
        }

        if (StringUtils.isNotBlank(cdnProperties.getDomain()) && StringUtils.isNotBlank(imageProcessingParams)) {
            // 如果配置了CDN域名且有图片处理参数，构建CDN URL
            String domain = cdnProperties.getDomain();
            String cleanDomain = domain.endsWith("/") ? domain.substring(0, domain.length() - 1) : domain;
            String cleanResourceId = resourceId.startsWith("/") ? resourceId.substring(1) : resourceId;
            String url = cleanDomain + "/" + cleanResourceId;

            // 添加图片处理参数
            if (url.contains("?")) {
                url += "&" + (imageProcessingParams.startsWith("?") ? imageProcessingParams.substring(1) : imageProcessingParams);
            } else {
                url += (imageProcessingParams.startsWith("?") ? "" : "?") + imageProcessingParams;
            }

            return url;
        } else {
            // 如果没有CDN域名或没有图片处理参数，尝试从后端获取
            ensureBackendServiceAvailable();
            return backendStorageService.getImageUrl(resourceId, resourceType, imageProcessingParams);
        }
    }

    @Override
    public boolean deleteResource(String resourceId, ResourceType resourceType) throws Exception {
        ensureBackendServiceAvailable();
        // Deletion always happens on the backend/origin server
        boolean result = backendStorageService.deleteResource(resourceId, resourceType);
        if (result) {
            log.info("源站文件 {} (类型: {}) 已成功删除。CDN缓存的刷新/失效取决于您的CDN配置策略。", resourceId, resourceType.name());
            // Consider adding a hook or event for CDN cache invalidation if an API is available
        }
        return result;
    }

    @Override
    public String generatePresignedUrl(String resourceId, ResourceType resourceType, int expireTimeInMinutes, String imageProcessingParams) throws Exception {
        return null;
    }

    @Override
    public String generatePresignedUrl(String resourceId, ResourceType resourceType, int expireTimeInMinutes) throws Exception {
        ensureBackendServiceAvailable();
        // Pre-signed URLs are generally for the origin store.
        // If CDN has its own signing mechanism for private content, that would be a separate logic here.
        // For now, delegate to backend.
        log.debug("CDN服务正在调用后端存储 '{}' 生成预签名URL for resourceId: {}.", cdnProperties.getStorageBackendType(), resourceId);
        return backendStorageService.generatePresignedUrl(resourceId, resourceType, expireTimeInMinutes);
    }

    @Override
    public void updateObjectTags(String resourceId, Map<String, String> tags) throws Exception {
        ensureBackendServiceAvailable();
        backendStorageService.updateObjectTags(resourceId, tags);
        log.debug("通过CDN后端存储服务更新了对象标签，resourceId: {}", resourceId);
    }
}
