package org.dromara.common.file.service.impl;

import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.constant.Constants;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.file.config.OssStorageProperties;
import org.dromara.common.file.config.ResourceStorageProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import jakarta.annotation.PreDestroy;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 阿里云OSS对象存储资源服务实现类。
 */
@Slf4j
@Service("ossResourceService")
@ConditionalOnProperty(prefix = "resource.storage", name = "type", havingValue = "oss")
public class OssResourceServiceImpl extends AbstractObjectStorageService {

    private final OssStorageProperties ossProperties;
    private OSS ossClient;
    private boolean clientAvailable = false;

    public OssResourceServiceImpl(ResourceStorageProperties resourceStorageProperties) {
        this.ossProperties = resourceStorageProperties.getOss();
    }

    @Override
    protected boolean isConfigurationMissing() {
        return StringUtils.isAnyBlank(ossProperties.getEndpoint(), ossProperties.getAccessKeyId(), ossProperties.getAccessKeySecret(), ossProperties.getBucket());
    }

    @Override
    protected void sdkInitializeClient() throws Exception {
        this.ossClient = new OSSClientBuilder().build(ossProperties.getEndpoint(), ossProperties.getAccessKeyId(), ossProperties.getAccessKeySecret());
        this.clientAvailable = true;
    }

    @Override
    protected void sdkEnsureBucketExists(String bucketName) throws Exception {
        if (!isClientAvailable()) {
            log.warn("OSS client is not available in sdkEnsureBucketExists.");
            return;
        }
        boolean exists = ossClient.doesBucketExist(bucketName);
        if (!exists) {
            ossClient.createBucket(bucketName);
            log.info("阿里云OSS存储桶 '{} ' 创建成功。", bucketName);
        } else {
            log.debug("阿里云OSS存储桶 '{} ' 已存在。", bucketName);
        }
    }

    @PreDestroy
    public void shutdownClient() {
        if (this.ossClient != null) {
            this.ossClient.shutdown();
            log.info("阿里云OSS客户端已关闭。");
            this.clientAvailable = false;
        }
    }

    @Override
    protected void sdkUploadFile(String bucketName, String objectName, InputStream inputStream, long size, String contentType) throws Exception {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(size);
        metadata.setContentType(contentType);
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, inputStream, metadata);
        ossClient.putObject(putObjectRequest);
    }

    @Override
    protected void sdkUploadInputStream(String bucketName, String objectName, InputStream inputStream, String contentType) throws Exception {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType);
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, inputStream, metadata);
        ossClient.putObject(putObjectRequest);
    }

    @Override
    protected boolean sdkDeleteObject(String bucketName, String objectName) throws Exception {
        ossClient.deleteObject(bucketName, objectName);
        return true;
    }

    @Override
    protected String sdkGeneratePresignedUrl(String bucketName, String objectName, int expireTimeInMinutes) throws Exception {
        Date expiration = new Date(new Date().getTime() + (long)expireTimeInMinutes * 60 * 1000);
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, objectName, HttpMethod.GET);
        request.setExpiration(expiration);
        URL signedUrl = ossClient.generatePresignedUrl(request);
        return signedUrl.toString();
    }

    @Override
    protected String getConfiguredBaseUrl() {
        if (StringUtils.isNotBlank(ossProperties.getCustomDomain())) {
            String customDomain = ossProperties.getCustomDomain();
            if (!customDomain.startsWith(Constants.HTTP) && !customDomain.startsWith(Constants.HTTPS)) {
                return Constants.HTTPS + customDomain;
            }
            return customDomain;
        }
        if (StringUtils.isNotBlank(ossProperties.getBaseUrl())) {
            String baseUrl = ossProperties.getBaseUrl();
            if (!baseUrl.startsWith(Constants.HTTP) && !baseUrl.startsWith(Constants.HTTPS)) {
                return Constants.HTTPS + baseUrl;
            }
            return baseUrl;
        }
        String endpoint = ossProperties.getEndpoint();
        if (endpoint.startsWith(Constants.HTTPS)) endpoint = endpoint.substring(8);
        else if (endpoint.startsWith(Constants.HTTP)) endpoint = endpoint.substring(7);
        return String.format("https://%s.%s", ossProperties.getBucket(), endpoint);
    }

    @Override
    protected String getBucketName() {
        return ossProperties.getBucket();
    }

    @Override
    protected String getStorageType() {
        return "OSS";
    }

    @Override
    protected boolean isClientAvailable() {
        return this.ossClient != null && this.clientAvailable;
    }

    @Override
    protected void markClientAsUnavailable(Exception e) {
        this.clientAvailable = false;
        if (this.ossClient != null) {
            try {
                this.ossClient.shutdown();
            } catch (Exception ex) {
                log.warn("Error shutting down OSS client during markClientAsUnavailable: {}", ex.getMessage());
            }
        }
        this.ossClient = null;
        log.error("OSS client marked as unavailable due to initialization error.", e);
    }

    @Override
    protected List<String> getThumbnailSizes() {
        if (this.ossProperties != null) {
            return this.ossProperties.getThumbnailSizes();
        }
        return super.getThumbnailSizes();
    }

    @Override
    protected String getThumbnailSubDir() {
        if (this.ossProperties != null && StringUtils.isNotBlank(this.ossProperties.getThumbnailSubDir())) {
            return this.ossProperties.getThumbnailSubDir();
        }
        return super.getThumbnailSubDir();
    }

    @Override
    protected boolean getConfiguredAlwaysUsePresignedUrl() {
        if (this.ossProperties == null) {
            log.warn("OSS properties are null, defaulting getConfiguredAlwaysUsePresignedUrl to false.");
            return false;
        }
        return this.ossProperties.isAlwaysUsePresignedUrl();
    }

    @Override
    protected int getConfiguredPresignedUrlExpiryMinutes() {
        if (this.ossProperties == null) {
            log.warn("OSS properties are null, defaulting getConfiguredPresignedUrlExpiryMinutes to 15 minutes.");
            return 15;
        }
        return this.ossProperties.getPresignedUrlExpiryMinutes();
    }

    @Override
    protected void sdkUpdateObjectTags(String bucketName, String objectName, Map<String, String> tags) throws Exception {
        if (!isClientAvailable()) {
            log.warn("OSS client is not available in sdkUpdateObjectTags.");
            throw new IllegalStateException("OSS client not available.");
        }

        com.aliyun.oss.model.TagSet tagSet = new com.aliyun.oss.model.TagSet();
        if (tags != null && !tags.isEmpty()) {
            for (Map.Entry<String, String> entry : tags.entrySet()) {
                tagSet.setTag(entry.getKey(), entry.getValue());
            }
        }
        ossClient.setObjectTagging(bucketName, objectName, tagSet);
        log.debug("OSS object {} in bucket {} tags updated.", objectName, bucketName);
    }
}
