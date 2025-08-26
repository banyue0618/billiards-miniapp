package org.dromara.common.file.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 统一资源存储配置属性
 * 通过 "resource.storage.type" 来决定激活哪种存储方式。
 */
@Data
@Component
@ConfigurationProperties(prefix = "resource.storage")
public class ResourceStorageProperties {

    /**
     * 存储类型：local(本地文件系统), minio(MinIO对象存储), oss(阿里云OSS对象存储), cdn(CDN加速)
     * 默认为 local
     */
    private String type = "local";

    /**
     * 本地存储配置
     */
    private LocalStorageProperties local = new LocalStorageProperties();

    /**
     * MinIO对象存储配置
     */
    private MinioStorageProperties minio = new MinioStorageProperties();

    /**
     * 阿里云OSS对象存储配置
     */
    private OssStorageProperties oss = new OssStorageProperties();

    /**
     * CDN加速配置
     */
    private CdnStorageProperties cdn = new CdnStorageProperties();

} 