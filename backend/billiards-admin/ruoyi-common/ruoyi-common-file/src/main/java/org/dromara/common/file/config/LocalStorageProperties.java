package org.dromara.common.file.config;

import lombok.Data;

/**
 * 本地文件系统存储配置
 */
@Data
public class LocalStorageProperties {
    /**
     * 本地文件存储的基础物理路径。
     * 例如：/data/upload, D:/upload_files
     * 必须配置，否则本地存储无法工作。
     */
    private String basePath;

    /**
     * 访问本地存储资源的基础URL。
     * 用于拼接成可公开访问的文件URL，需要配合Web服务器（如Nginx）配置静态资源映射。
     * 例如：http://localhost:8080/static/upload, https://cdn.example.com/files
     * 如果为空，则可能无法直接通过URL访问文件，或需要其他方式生成访问路径。
     */
    private String baseUrl;
} 