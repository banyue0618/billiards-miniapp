package org.dromara.common.file.service.impl;

import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Tags;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.file.config.MinioStorageProperties;
import org.dromara.common.file.config.ResourceStorageProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.Map;

/**
 * MinIO 对象存储资源服务实现类。
 */
@Slf4j
@Service("minioResourceService")
@ConditionalOnProperty(name = "resource.storage.type", havingValue = "minio", matchIfMissing = true)
public class MinioResourceServiceImpl extends AbstractObjectStorageService {

    private final MinioStorageProperties minioProperties;
    private MinioClient minioClient;
    private boolean clientAvailable = false;

    public MinioResourceServiceImpl(ResourceStorageProperties resourceStorageProperties) {
        this.minioProperties = resourceStorageProperties.getMinio();
    }

    @Override
    protected boolean isConfigurationMissing() {
        return StringUtils.isAnyBlank(minioProperties.getEndpoint(), minioProperties.getAccessKey(), minioProperties.getSecretKey(), minioProperties.getBucket());
    }

    @Override
    protected void sdkInitializeClient() {
        this.minioClient = MinioClient.builder()
            .endpoint(minioProperties.getEndpoint())
            .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
            .build();
        this.clientAvailable = true; // Mark as available after successful initialization
    }

    @Override
    protected void sdkEnsureBucketExists(String bucketName) throws Exception {
        if (!isClientAvailable()) {
            log.warn("MinIO client is not available in sdkEnsureBucketExists.");
            return;
        }
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            log.info("MinIO存储桶 '{} ' 创建成功。", bucketName);
        } else {
            log.debug("MinIO存储桶 '{} ' 已存在。", bucketName);
        }
    }

    @Override
    protected void sdkUploadFile(String bucketName, String objectName, InputStream inputStream, long size, String contentType) throws Exception {
        minioClient.putObject(
            PutObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .stream(inputStream, size, -1)
                .contentType(contentType)
                .build());
    }

    @Override
    protected void sdkUploadInputStream(String bucketName, String objectName, InputStream inputStream, String contentType) throws Exception {
        minioClient.putObject(
            PutObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .stream(inputStream, -1, PutObjectArgs.MIN_MULTIPART_SIZE)
                .contentType(contentType)
                .build());
    }

    @Override
    protected boolean sdkDeleteObject(String bucketName, String objectName) throws Exception {
        minioClient.removeObject(
            RemoveObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .build());
        return true;
    }

    @Override
    protected String sdkGeneratePresignedUrl(String bucketName, String objectName, int expireTimeInMinutes) throws Exception {
        return minioClient.getPresignedObjectUrl(
            GetPresignedObjectUrlArgs.builder()
                .method(Method.GET)
                .bucket(bucketName)
                .object(objectName)
                .expiry(expireTimeInMinutes, TimeUnit.MINUTES)
                .build());
    }

    @Override
    protected String getConfiguredBaseUrl() {
        return minioProperties.getBaseUrl();
    }

    @Override
    protected String getBucketName() {
        return minioProperties.getBucket();
    }

    @Override
    protected String getStorageType() {
        return "MinIO";
    }

    @Override
    protected boolean isClientAvailable() {
        return this.minioClient != null && this.clientAvailable;
    }

    @Override
    protected void markClientAsUnavailable(Exception e) {
        this.clientAvailable = false;
        this.minioClient = null;
        log.error("MinIO client marked as unavailable due to initialization error.", e);
    }

    @Override
    protected List<String> getThumbnailSizes() {
        if (this.minioProperties != null) {
            return this.minioProperties.getThumbnailSizes();
        }
        return super.getThumbnailSizes(); // 或 Collections.emptyList();
    }

    @Override
    protected String getThumbnailSubDir() {
        if (this.minioProperties != null && StringUtils.isNotBlank(this.minioProperties.getThumbnailSubDir())) {
            return this.minioProperties.getThumbnailSubDir();
        }
        return super.getThumbnailSubDir(); // 或 "thumbs";
    }

    @Override
    protected boolean getConfiguredAlwaysUsePresignedUrl() {
        return this.minioProperties.isAlwaysUsePresignedUrl();
    }

    @Override
    protected int getConfiguredPresignedUrlExpiryMinutes() {
        return this.minioProperties.getPresignedUrlExpiryMinutes();
    }

    @Override
    protected void sdkUpdateObjectTags(String bucketName, String objectName, Map<String, String> tags) throws Exception {
        if (!isClientAvailable()) {
            log.warn("MinIO client is not available in sdkUpdateObjectTags.");
            throw new IllegalStateException("MinIO client not available.");
        }
        // MinIO SDK的Tags类需要Map<String, String>
        // 如果tags参数为空或null，MinIO的setObjectTags会移除所有现有标签
        Tags minioTags = (tags == null || tags.isEmpty()) ? null : Tags.newObjectTags(tags);

        minioClient.setObjectTags(
            SetObjectTagsArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .tags(minioTags) // 直接传递 Map<String, String> 给 builder
                .build());
        log.debug("MinIO object {} in bucket {} tags updated.", objectName, bucketName);
    }
}
