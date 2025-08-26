package org.dromara.common.file.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.utils.DateUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.core.utils.file.FileUtils;
import org.dromara.common.file.config.LocalStorageProperties;
// import org.dromara.common.file.config.ResourceStorageProperties; // Not directly needed here if LocalStorageProperties is injected
import org.dromara.common.file.config.ResourceStorageProperties;
import org.dromara.common.file.enums.ResourceType;
import org.dromara.common.file.service.ResourceService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

// import java.io.File; // Not used
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;

/**
 * 本地文件系统资源服务实现类。
 */
@Slf4j
@Service("localResourceService") // Bean name for explicit wiring if needed
@ConditionalOnProperty(prefix = "resource.storage", name = "type", havingValue = "local")
public class LocalResourceServiceImpl implements ResourceService {

    private final LocalStorageProperties localProperties;

    public LocalResourceServiceImpl(ResourceStorageProperties resourceStorageProperties) {
        this.localProperties = resourceStorageProperties.getLocal();
    }

    private String getDateStampedPath(ResourceType resourceType) {
        String datePath = DateUtils.datePath(); // yyyy/MM/dd
        return Paths.get(resourceType.getPath(), datePath).toString().replace("\\", "/");
    }

    private String buildUniqueFilename(String originalFilename) {
        String extension = FileUtils.getSuffix(originalFilename);
        // Ensure UUID is always appended, even if originalFilename is just an extension like ".jpg"
        String baseName = FileUtils.getName(originalFilename);
        if (StringUtils.isBlank(baseName) && StringUtils.isNotBlank(extension)) {
            // Handle cases like ".jpg" where baseName is empty
            baseName = UUID.randomUUID().toString().replace("-", "");
        } else if (StringUtils.isBlank(baseName)) {
            // Handle cases where originalFilename is completely blank or just an extension without dot
            baseName = UUID.randomUUID().toString().replace("-", "");
        }
        String uuid = UUID.randomUUID().toString().replace("-", "");
        // Suffix with UUID to ensure uniqueness if original name is kept, or just use UUID as name
        return baseName + "_" + uuid + (StringUtils.isNotBlank(extension) ? "." + extension : "");
        // Alternative: just use UUID as filename, simpler for resourceId management
        // return uuid + (StringUtils.isNotBlank(extension) ? "." + extension : "");
    }

    @Override
    public String uploadResource(MultipartFile file, ResourceType resourceType) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("上传文件不能为空");
        }
        if (StringUtils.isBlank(localProperties.getBasePath())) {
            log.error("本地存储基础路径(resource.storage.local.base-path)未配置，无法上传文件。");
            throw new IllegalStateException("本地存储路径(resource.storage.local.base-path)未配置");
        }

        String originalFilename = file.getOriginalFilename();
        String uniqueFilename = buildUniqueFilename(originalFilename);
        String relativeTypeAndDatePath = getDateStampedPath(resourceType);

        Path fullDirectoryPath = Paths.get(localProperties.getBasePath(), relativeTypeAndDatePath);
        if (!Files.exists(fullDirectoryPath)) {
            Files.createDirectories(fullDirectoryPath);
            log.info("本地存储目录已创建: {}", fullDirectoryPath);
        }

        Path targetFilePath = fullDirectoryPath.resolve(uniqueFilename);
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, targetFilePath, StandardCopyOption.REPLACE_EXISTING);
        }

        String resourceId = Paths.get(relativeTypeAndDatePath, uniqueFilename).toString().replace("\\", "/");
        log.info("文件已本地上传: {}, 目标路径: {}, 资源ID: {}", originalFilename, targetFilePath, resourceId);
        return resourceId;
    }

    @Override
    public String uploadResource(InputStream inputStream, String originalFilename, ResourceType resourceType) throws IOException {
        if (inputStream == null) {
            throw new IllegalArgumentException("上传文件流不能为空");
        }
        if (StringUtils.isBlank(originalFilename)) {
            throw new IllegalArgumentException("原始文件名不能为空");
        }
        if (StringUtils.isBlank(localProperties.getBasePath())) {
            log.error("本地存储基础路径(resource.storage.local.base-path)未配置，无法上传文件。");
            throw new IllegalStateException("本地存储路径(resource.storage.local.base-path)未配置");
        }

        String uniqueFilename = buildUniqueFilename(originalFilename);
        String relativeTypeAndDatePath = getDateStampedPath(resourceType);

        Path fullDirectoryPath = Paths.get(localProperties.getBasePath(), relativeTypeAndDatePath);
        if (!Files.exists(fullDirectoryPath)) {
            Files.createDirectories(fullDirectoryPath);
            log.info("本地存储目录已创建: {}", fullDirectoryPath);
        }

        Path targetFilePath = fullDirectoryPath.resolve(uniqueFilename);
        Files.copy(inputStream, targetFilePath, StandardCopyOption.REPLACE_EXISTING);

        String resourceId = Paths.get(relativeTypeAndDatePath, uniqueFilename).toString().replace("\\", "/");
        log.info("文件流已本地上传: {}, 目标路径: {}, 资源ID: {}", originalFilename, targetFilePath, resourceId);
        return resourceId;
    }

    @Override
    public String getResourceUrl(String resourceId, ResourceType resourceType) {
        if (StringUtils.isBlank(resourceId)) {
            return null;
        }
        if (StringUtils.isBlank(localProperties.getBaseUrl())) {
            log.warn("本地存储基础URL(resource.storage.local.base-url)未配置，无法生成公开URL for resourceId: {}", resourceId);
            return null;
        }
        String baseUrl = localProperties.getBaseUrl();
        // Ensure no double slashes and correct joining
        String cleanBaseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        String cleanResourceId = resourceId.startsWith("/") ? resourceId.substring(1) : resourceId;
        return cleanBaseUrl + "/" + cleanResourceId;
    }

    @Override
    public String uploadImage(MultipartFile file, ResourceType resourceType) throws Exception {
        return null;
    }

    @Override
    public String uploadImage(InputStream inputStream, String originalFilename, ResourceType resourceType) throws Exception {
        return null;
    }

    @Override
    public String getImageUrl(String resourceId, ResourceType resourceType, String imageProcessingParams) {
        return null;
    }

    @Override
    public boolean deleteResource(String resourceId, ResourceType resourceType) throws IOException {
        if (StringUtils.isBlank(resourceId)) {
            return false;
        }
        if (StringUtils.isBlank(localProperties.getBasePath())) {
            log.error("本地存储基础路径(resource.storage.local.base-path)未配置，无法删除文件: {}", resourceId);
            return false;
        }
        // resourceId is already relative path like "stores/covers/2023/12/21/xxxx.jpg"
        Path targetFilePath = Paths.get(localProperties.getBasePath(), resourceId);
        if (Files.exists(targetFilePath)) {
            Files.delete(targetFilePath);
            log.info("本地文件已删除: {}", targetFilePath);
            return true;
        }
        log.warn("尝试删除的本地文件不存在: {}", targetFilePath);
        return false;
    }

    @Override
    public String generatePresignedUrl(String resourceId, ResourceType resourceType, int expireTimeInMinutes, String imageProcessingParams) throws Exception {
        return null;
    }

    @Override
    public String generatePresignedUrl(String resourceId, ResourceType resourceType, int expireTimeInMinutes) {
        log.warn("本地存储不支持生成预签名URL for resourceId: {}. 将返回普通可访问URL (如果baseUrl已配置)。", resourceId);
        return getResourceUrl(resourceId, resourceType);
    }

    @Override
    public String uploadBytes(byte[] imageBytes, String fileName, ResourceType resourceType) throws Exception {
        if (imageBytes == null || imageBytes.length == 0) {
            throw new IllegalArgumentException("上传的字节数组不能为空");
        }
        if (StringUtils.isBlank(fileName)) {
            throw new IllegalArgumentException("文件名不能为空");
        }
        if (StringUtils.isBlank(localProperties.getBasePath())) {
            log.error("本地存储基础路径(resource.storage.local.base-path)未配置，无法上传文件。");
            throw new IllegalStateException("本地存储路径(resource.storage.local.base-path)未配置");
        }

        String uniqueFilename = buildUniqueFilename(fileName);
        String relativeTypeAndDatePath = getDateStampedPath(resourceType);

        Path fullDirectoryPath = Paths.get(localProperties.getBasePath(), relativeTypeAndDatePath);
        if (!Files.exists(fullDirectoryPath)) {
            Files.createDirectories(fullDirectoryPath);
            log.info("本地存储目录已创建: {}", fullDirectoryPath);
        }

        Path targetFilePath = fullDirectoryPath.resolve(uniqueFilename);
        Files.write(targetFilePath, imageBytes);

        String resourceId = Paths.get(relativeTypeAndDatePath, uniqueFilename).toString().replace("\\", "/");
        log.info("字节数组已本地上传: {}, 目标路径: {}, 资源ID: {}", fileName, targetFilePath, resourceId);
        return resourceId;
    }

    @Override
    public void updateObjectTags(String resourceId, Map<String, String> tags) throws Exception {
        // 本地文件系统不支持标签，此方法为空实现
        log.warn("本地存储不支持对象标签功能，resourceId: {}", resourceId);
    }
}
