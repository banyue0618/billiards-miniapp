package org.dromara.system.service;

import org.dromara.common.file.enums.ResourceType;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 文件上传接口
 */
public interface FileService {

    /**
     * 文件上传
     *
     * @param file 资源文件
     * @param resourceType 资源类型
     * @return 包含预览URL和文件路径的Map (key: "previewUrl", "filePath")
     * @throws Exception 上传异常
     */
    Map<String, String> uploadFile(MultipartFile file, ResourceType resourceType);

    /**
     * 上传字节数组文件
     *
     * @param imageBytes 字节数组数据
     * @param fileName 文件名
     * @param resourceType 资源类型
     * @return 包含预览URL和资源ID的Map (key: "previewUrl", "resourceId")
     */
    Map<String, String> uploadBytes(byte[] imageBytes, String fileName, ResourceType resourceType);
    
    /**
     * 删除文件资源
     *
     * @param resourceId 资源ID
     * @param resourceType 资源类型
     * @return 是否删除成功
     */
    boolean deleteResource(String resourceId, ResourceType resourceType);
}
