package org.dromara.system.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.file.enums.ResourceType;
import org.dromara.common.file.service.ResourceService;
import org.dromara.common.file.service.factory.ResourceServiceFactory;
import org.dromara.system.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;

import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传服务实现
 */
@Service
@Slf4j
public class FileServiceImpl implements FileService {

    private final ResourceServiceFactory resourceServiceFactory;
    private ResourceService activeResourceService;

    @Autowired
    public FileServiceImpl(ResourceServiceFactory resourceServiceFactory) {
        this.resourceServiceFactory = resourceServiceFactory;
    }

    @PostConstruct
    public void init() {
        this.activeResourceService = resourceServiceFactory.getService();
    }

    /**
     * 文件上传
     *
     * @param file         上传的文件
     * @param resourceType 资源类型
     * @return 访问地址
     * @throws Exception 上传异常
     */
    @Override
    public Map<String, String> uploadFile(MultipartFile file, ResourceType resourceType) {
        Map<String, String> result = new HashMap<>();
        try {
            String resourceId = activeResourceService.uploadImage(file, resourceType);
            String previewUrl = activeResourceService.getResourceUrl(resourceId, resourceType);
            result.put("previewUrl", previewUrl);
            result.put("resourceId", resourceId); // 使用 resourceId 作为文件路径
        } catch (Exception e) {
            log.error("上传文件失败：{}", e.getMessage());
        }
        result.put("fileName", file.getOriginalFilename());
        return result;
    }

    /**
     * 上传字节数组文件
     *
     * @param imageBytes  字节数组数据
     * @param fileName    文件名
     * @param resourceType 资源类型
     * @return 包含预览URL和资源ID的Map
     */
    @Override
    public Map<String, String> uploadBytes(byte[] imageBytes, String fileName, ResourceType resourceType) {
        Map<String, String> result = new HashMap<>();
        try {
            String resourceId = activeResourceService.uploadBytes(imageBytes, fileName, resourceType);
            String previewUrl = activeResourceService.getResourceUrl(resourceId, resourceType);
            result.put("previewUrl", previewUrl);
            result.put("resourceId", resourceId);
            result.put("fileName", fileName);
        } catch (Exception e) {
            log.error("上传字节数组文件失败：{}", e.getMessage(), e);
        }
        return result;
    }

    /**
     * 删除文件资源
     *
     * @param resourceId 资源ID
     * @param resourceType 资源类型
     * @return 是否删除成功
     */
    @Override
    public boolean deleteResource(String resourceId, ResourceType resourceType) {
        if (StringUtils.isEmpty(resourceId)) {
            log.warn("尝试删除资源，但资源ID为空");
            return false;
        }
        try {
            return activeResourceService.deleteResource(resourceId, resourceType);
        } catch (Exception e) {
            log.error("删除资源时发生异常，ID: {}, 类型: {}, 异常: {}", resourceId, resourceType, e.getMessage(), e);
            return false;
        }
    }
}
