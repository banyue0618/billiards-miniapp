package org.dromara.common.file.config;

import lombok.Data;
import java.util.Collections;
import java.util.List;

/**
 * 阿里云OSS对象存储配置
 */
@Data
public class OssStorageProperties {
    /**
     * OSS服务的Endpoint。
     * 例如：https://oss-cn-hangzhou.aliyuncs.com
     */
    private String endpoint;

    /**
     * Access Key ID。
     */
    private String accessKeyId;

    /**
     * Access Key Secret。
     */
    private String accessKeySecret;

    /**
     * 默认存储桶名称。
     */
    private String bucket = "billiards-app"; // 默认值参考了文档

    /**
     * 访问OSS资源的基础URL (可选)。
     * 例如：https://your-bucket.oss-cn-hangzhou.aliyuncs.com
     * 如果配置了customDomain，则baseUrl通常基于customDomain。
     */
    private String baseUrl;

    /**
     * 自定义域名 (CName) (可选)。
     * 例如：oss.yourdomain.com
     */
    private String customDomain;

    /**
     * 是否总是使用预签名URL来访问OSS文件，默认为 false。
     */
    private boolean alwaysUsePresignedUrl = false;

    /**
     * OSS预签名URL的默认有效期（单位：分钟）。
     * 默认为 1440 分钟 (1 天)。
     */
    private int presignedUrlExpiryMinutes = 1440;

    /**
     * 需要生成的缩略图尺寸列表。
     * 格式："宽x高"，例如 ["150x150", "300x300"]
     * 如果为空列表，则不生成缩略图。
     */
    private List<String> thumbnailSizes = Collections.emptyList();

    /**
     * 缩略图存储的子目录名称。
     * 例如 "thumbs"，生成的路径会是 "原目录/thumbs/文件名_尺寸.后缀"
     */
    private String thumbnailSubDir = "thumbs";
} 