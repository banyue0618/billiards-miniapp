package org.dromara.common.file.config;

import lombok.Data;

/**
 * CDN加速配置
 */
@Data
public class CdnStorageProperties {
    /**
     * CDN域名。
     * 例如：https://static.example.com
     * 这是通过CDN访问资源的基础URL，通常配置为指向某个对象存储源站。
     */
    private String domain;

    /**
     * CDN后的实际存储后端类型，例如 minio 或 oss。
     * 这是必需的，因为CDN本身不存储文件，它只缓存和加速来自源站的文件。
     * 上传、删除等操作仍然需要针对实际的存储后端进行。
     * 可选值: "minio", "oss", "local" (尽管local作为CDN源站不常见且可能效率不高)
     */
    private String storageBackendType = "oss"; // 默认为oss，参考文档

    /**
     * 如果CDN需要特定的私钥或其他凭证来进行URL签名（防盗链），在此配置。
     * 具体配置项取决于CDN服务商的要求。
     * 例如，阿里云CDN的URL鉴权Key。
     */
    private String privateKey;

    // 根据不同的CDN服务商，可能还需要其他特定配置
    // 例如： boolean urlAuthEnabled = false;
    //       String authAlgorithm = "md5"; // or sha256 etc.
} 