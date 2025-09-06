package org.dromara.common.file.config;

import lombok.Data;
import java.util.List;
import java.util.Collections;

/**
 * MinIO 对象存储配置
 */
@Data
public class MinioStorageProperties {
    /**
     * MinIO服务节点的URL。
     * 例如：http://localhost:9000
     */
    private String endpoint;

    /**
     * MinIO的Access Key。
     */
    private String accessKey;

    /**
     * MinIO的Secret Key。
     */
    private String secretKey;

    /**
     * 默认存储桶名称。
     * 如果存储桶不存在，某些实现可能会尝试自动创建它（如果配置允许）。
     */
    private String bucket = "billiards-app"; // 默认值参考了文档

    /**
     * 访问MinIO资源的基础URL（可选）。
     * 如果配置了此项，并且文件是公开的，则可以使用此URL拼接资源路径进行访问。
     * 对于需要签名的私有文件，此URL可能不直接使用。
     * 例如：http://minio.example.com:9000/billiards-app
     * 如果endpoint已经是可直接访问的域名且bucket名希望体现在路径中，可以配置成 endpoint + "/" + bucket
     */
    private String baseUrl;

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

    /**
     * 是否总是使用预签名URL来访问文件，默认为 false。
     * 如果为 true，则 getImageUrl 会总是返回预签名URL。
     * 如果为 false，则其行为可能取决于 AbstractObjectStorageService 的具体逻辑（例如，是否配置了 baseUrl）。
     */
    private boolean alwaysUsePresignedUrl = false;

    /**
     * 预签名URL的默认有效期（单位：分钟）。
     * 默认为 1440 分钟 (1 天)。
     */
    private int presignedUrlExpiryMinutes = 1440;
}
