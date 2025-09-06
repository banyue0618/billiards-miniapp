package org.dromara.common.file.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.dromara.common.core.utils.DateUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.core.utils.file.FileUtils;
import org.dromara.common.file.enums.ResourceType;
import org.dromara.common.file.service.ResourceService;
import org.springframework.web.multipart.MultipartFile;
import org.dromara.common.file.enums.MimeTypeEnum;

import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 对象存储服务的抽象基类，提供通用的逻辑实现。
 */
@Slf4j
public abstract class AbstractObjectStorageService implements ResourceService {

    /**
     * 初始化客户端和存储桶。
     * 此方法会在Bean构造完成后由Spring调用。
     */
    @PostConstruct
    public void init() {
        try {
            if (isConfigurationMissing()) {
                log.warn("{} 服务配置不完整，将跳过初始化。服务可能不可用。", getStorageType());
                return;
            }
            sdkInitializeClient();
            log.info("{} 客户端初始化成功。", getStorageType());
            sdkEnsureBucketExists(getBucketName());
        } catch (Exception e) {
            log.error("{} 客户端初始化失败: {}", getStorageType(), e.getMessage(), e);
            // 在这里标记客户端为不可用状态，例如设置一个boolean标志位
            markClientAsUnavailable(e);
        }
    }

    /**
     * 检查必要的配置是否缺失。
     * @return 如果配置缺失则返回 true，否则 false。
     */
    protected abstract boolean isConfigurationMissing();

    /**
     * 初始化特定SDK的客户端。
     * @throws Exception 初始化过程中可能发生的任何异常。
     */
    protected abstract void sdkInitializeClient() throws Exception;

    /**
     * 确保指定的存储桶存在，如果不存在则尝试创建。
     * @param bucketName 存储桶名称。
     * @throws Exception 操作过程中可能发生的任何异常。
     */
    protected abstract void sdkEnsureBucketExists(String bucketName) throws Exception;

    /**
     * 使用特定SDK上传文件流 (通常来自 MultipartFile)。
     * @param bucketName 存储桶名称。
     * @param objectName 在存储桶中的对象名称/路径。
     * @param inputStream 文件输入流。
     * @param size 文件大小。
     * @param contentType 文件类型。
     * @throws Exception 上传过程中可能发生的任何异常。
     */
    protected abstract void sdkUploadFile(String bucketName, String objectName, InputStream inputStream, long size, String contentType) throws Exception;

    /**
     * 使用特定SDK上传原始输入流。
     * @param bucketName 存储桶名称。
     * @param objectName 在存储桶中的对象名称/路径。
     * @param inputStream 输入流。
     * @param contentType 文件类型。
     * @throws Exception 上传过程中可能发生的任何异常。
     */
    protected abstract void sdkUploadInputStream(String bucketName, String objectName, InputStream inputStream, String contentType) throws Exception;

    /**
     * 使用特定SDK删除对象。
     * @param bucketName 存储桶名称。
     * @param objectName 对象名称/路径。
     * @return 如果删除成功或对象不存在则返回 true，如果因错误失败则返回 false 或抛出异常。
     * @throws Exception 删除过程中可能发生的任何异常。
     */
    protected abstract boolean sdkDeleteObject(String bucketName, String objectName) throws Exception;

    /**
     * 使用特定SDK生成对象的预签名URL。
     * @param bucketName 存储桶名称。
     * @param objectName 对象名称/路径。
     * @param expireTimeInMinutes URL的有效时间（分钟）。
     * @return 生成的预签名URL字符串。
     * @throws Exception 生成URL过程中可能发生的任何异常。
     */
    protected abstract String sdkGeneratePresignedUrl(String bucketName, String objectName, int expireTimeInMinutes) throws Exception;

    /**
     * 获取客户端配置的基础URL (如果有)。
     * @return 配置的基础URL，可能为null。
     */
    protected abstract String getConfiguredBaseUrl();

    /**
     * 获取客户端配置的存储桶名称。
     * @return 配置的存储桶名称。
     */
    protected abstract String getBucketName();

    /**
     * 获取存储服务的类型名称（用于日志等）。
     * @return 例如 "MinIO", "OSS"。
     */
    protected abstract String getStorageType();

    /**
     * 检查SDK客户端是否已成功初始化且可用。
     * @return 如果客户端可用则返回 true。
     */
    protected abstract boolean isClientAvailable();

    /**
     * 当客户端初始化失败时，标记服务为不可用。
     * @param e 初始化时发生的异常。
     */
    protected abstract void markClientAsUnavailable(Exception e);

    /**
     * 获取是否应始终使用预签名URL的配置。
     * @return 如果配置为总是使用预签名URL，则返回 true。
     */
    protected abstract boolean getConfiguredAlwaysUsePresignedUrl();

    /**
     * 获取配置的预签名URL的默认有效期（分钟）。
     * @return 预签名URL的有效期（分钟）。
     */
    protected abstract int getConfiguredPresignedUrlExpiryMinutes();

    /**
     * 使用特定SDK更新对象的标签。
     * @param bucketName 存储桶名称。
     * @param objectName 对象名称/路径。
     * @param tags       要设置的标签。
     * @throws Exception 操作过程中可能发生的任何异常。
     */
    protected abstract void sdkUpdateObjectTags(String bucketName, String objectName, Map<String, String> tags) throws Exception;

    /**
     * 获取对象在存储桶中的完整路径，包含日期和类型路径。
     * @param resourceType 资源类型。
     * @param uniqueFilename 文件在当日该类型下的唯一名称。
     * @return 对象的完整存储路径。
     */
    protected String getObjectPath(ResourceType resourceType, String uniqueFilename) {
        String datePath = DateUtils.datePath(); // 格式: yyyy/MM/dd
        String typePath = resourceType.getPath();
        // 如果typePath为空或为根路径"/"，则路径不包含类型前缀，直接使用日期和文件名
        if (StringUtils.isBlank(typePath) || "/".equals(typePath)) {
            return Paths.get(datePath, uniqueFilename).toString().replace("\\", "/");
        }
        return Paths.get(typePath, datePath, uniqueFilename).toString().replace("\\", "/");
    }

    /**
     * 根据原始文件名生成一个唯一的文件名 (通常使用UUID + 后缀)。
     * @param originalFilename 原始文件名。
     * @return 唯一的、适合存储的文件名。
     */
    protected String buildUniqueFilename(String originalFilename) {
        String extension = FileUtils.getSuffix(originalFilename);
        String uuid = UUID.randomUUID().toString().replace("-", "");
        // 优先使用UUID作为文件名主体，避免原始文件名中的特殊字符问题
        return uuid + (StringUtils.isNotBlank(extension) ? "." + extension : "");
    }

    @Override
    public String uploadResource(MultipartFile file, ResourceType resourceType) throws Exception {
        if (!isClientAvailable()) {
            throw new IllegalStateException(getStorageType() + " 服务未初始化或配置错误。");
        }
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("上传文件不能为空。");
        }

        String originalFilename = file.getOriginalFilename();
        String uniqueFilename = buildUniqueFilename(originalFilename);
        String objectName = getObjectPath(resourceType, uniqueFilename);

        // 优先使用 MultipartFile 自带的 contentType
        String contentType = file.getContentType();
        if (StringUtils.isBlank(contentType)) {
            // 如果 MultipartFile 未提供，则尝试从文件名推断
            try {
                Path tempPath = Paths.get(uniqueFilename); // uniqueFilename 包含后缀
                contentType = Files.probeContentType(tempPath);
                if (StringUtils.isNotBlank(contentType)) {
                     log.debug("ProbeContentType for filename '{}' (from MultipartFile fallback) determined: {}", uniqueFilename, contentType);
                } else {
                     log.debug("ProbeContentType for filename '{}' (from MultipartFile fallback) returned null/blank.", uniqueFilename);
                }
            } catch (IOException e) {
                log.warn("使用 Files.probeContentType 获取文件 '{}' 的类型失败 (MultipartFile fallback): {}. 将回退到默认类型。", uniqueFilename, e.getMessage());
            }
        }
        // 最终回退
        if (StringUtils.isBlank(contentType)) {
            contentType = "application/octet-stream";
            log.debug("ContentType for '{}' (from MultipartFile) set to default: {}", uniqueFilename, contentType);
        }

        sdkUploadFile(getBucketName(), objectName, file.getInputStream(), file.getSize(), contentType);

        // +++ 上传成功后，立即打上临时标签 +++
        try {
            Map<String, String> temporaryTags = Collections.singletonMap("status", "temporary");
            sdkUpdateObjectTags(getBucketName(), objectName, temporaryTags);
            log.info("文件 '{}' 已标记为临时: objectName={}", originalFilename, objectName);
        } catch (Exception e) {
            // 即使打标签失败，文件也已上传，记录警告但不应让整个上传失败
            log.warn("为文件 '{}' (objectName={}) 打临时标签失败: {}", originalFilename, objectName, e.getMessage());
        }
        // --- 临时标签结束 ---

        log.info("文件 '{}' 已上传至 {}: bucket={}, objectName={}", originalFilename, getStorageType(), getBucketName(), objectName);
        return objectName;
    }

    @Override
    public String uploadResource(InputStream inputStream, String originalFilename, ResourceType resourceType) throws Exception {
        if (!isClientAvailable()) {
            throw new IllegalStateException(getStorageType() + " 服务未初始化或配置错误。");
        }
        if (inputStream == null) {
            throw new IllegalArgumentException("上传文件流不能为空。");
        }
        if (StringUtils.isBlank(originalFilename)) {
            throw new IllegalArgumentException("原始文件名不能为空。");
        }

        String uniqueFilename = buildUniqueFilename(originalFilename);
        String objectName = getObjectPath(resourceType, uniqueFilename);

        String contentType = null;
        try {
            // 尝试使用NIO Files.probeContentType，它通常基于文件名后缀
            // 这个方法需要一个实际的Path对象，但文件名本身（uniqueFilename）已包含后缀
            Path tempPath = Paths.get(uniqueFilename);
            contentType = Files.probeContentType(tempPath);
            if (StringUtils.isNotBlank(contentType)){
                 log.debug("ProbeContentType for filename '{}' determined: {}", uniqueFilename, contentType);
            } else {
                 log.debug("ProbeContentType for filename '{}' returned null/blank.", uniqueFilename);
            }
        } catch (IOException e) {
            log.warn("使用 Files.probeContentType 获取文件 '{}' 的类型失败: {}. 将回退到默认类型。", uniqueFilename, e.getMessage());
        }

        // 最终回退
        if (StringUtils.isBlank(contentType)) {
            contentType = "application/octet-stream";
            log.debug("ContentType for '{}' set to default: {}", uniqueFilename, contentType);
        }

        sdkUploadInputStream(getBucketName(), objectName, inputStream, contentType);

        // +++ 上传成功后，立即打上临时标签 +++
        try {
            Map<String, String> temporaryTags = Collections.singletonMap("status", "temporary");
            sdkUpdateObjectTags(getBucketName(), objectName, temporaryTags);
            log.info("文件流 '{}' 已标记为临时: objectName={}", originalFilename, objectName);
        } catch (Exception e) {
            log.warn("为文件流 '{}' (objectName={}) 打临时标签失败: {}", originalFilename, objectName, e.getMessage());
        }
        // --- 临时标签结束 ---

        log.info("文件流 '{}' 已上传至 {}: bucket={}, objectName={}", originalFilename, getStorageType(), getBucketName(), objectName);
        return objectName;
    }

    @Override
    public String getResourceUrl(String resourceId, ResourceType resourceType) {
        if (!isClientAvailable()) {
            log.error("{} 服务未初始化，无法获取资源ID {} 的URL。", getStorageType(), resourceId);
            return null;
        }
        if (StringUtils.isBlank(resourceId)) {
            return null;
        }

        boolean alwaysUsePresigned = getConfiguredAlwaysUsePresignedUrl();

        if (alwaysUsePresigned) {
            try {
                int expiryMinutes = getConfiguredPresignedUrlExpiryMinutes();
                return sdkGeneratePresignedUrl(getBucketName(), resourceId, expiryMinutes);
            } catch (Exception e) {
                log.error("{} 生成资源ID {} 的预签名URL失败: {}", getStorageType(), resourceId, e.getMessage(), e);
                return null; // 预签名URL生成失败，返回null
            }
        } else {
            // 保持原有逻辑：如果未强制使用预签名URL，则尝试构建公共URL
            String configuredBaseUrl = getConfiguredBaseUrl();
            if (StringUtils.isNotBlank(configuredBaseUrl)) {
                String cleanBaseUrl = configuredBaseUrl.endsWith("/") ? configuredBaseUrl.substring(0, configuredBaseUrl.length() - 1) : configuredBaseUrl;
                String cleanResourceId = resourceId.startsWith("/") ? resourceId.substring(1) : resourceId;
                return cleanBaseUrl + "/" + cleanResourceId;
            }
            log.warn("{} 存储的baseUrl未配置，且未强制使用预签名URL，无法为资源ID {} 生成URL。", getStorageType(), resourceId);
            return null;
        }
    }

    @Override
    public boolean deleteResource(String resourceId, ResourceType resourceType) throws Exception {
        if (!isClientAvailable()) {
            throw new IllegalStateException(getStorageType() + " 服务未初始化或配置错误。");
        }
        if (StringUtils.isBlank(resourceId)) {
            return false;
        }

        boolean deleted = sdkDeleteObject(getBucketName(), resourceId);
        if (deleted) {
            log.info("{} 对象已删除: bucket={}, objectName={}", getStorageType(), getBucketName(), resourceId);
        } else {
            // SdkDeleteObject 应该在失败时抛异常，或者返回false如果对象不存在
            log.warn("{} 对象删除失败或不存在: bucket={}, objectName={}", getStorageType(), getBucketName(), resourceId);
        }
        return deleted;
    }

    // ======== 图片处理相关方法 ========

    @Override
    public String uploadImage(MultipartFile file, ResourceType resourceType) throws Exception {
        // 基础实现直接调用 uploadResource。子类或装饰器可以覆盖以添加特定图片处理。
        // 在调用前可以增加图片校验逻辑
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("上传的图片文件不能为空。");
        }
        String originalFilename = file.getOriginalFilename();
        String contentType = file.getContentType();

        // 校验是否为图片类型
        boolean isImage = false;
        if (StringUtils.isNotBlank(contentType) && contentType.startsWith("image/")) {
            isImage = true;
        } else {
            try {
                Path tempPath = Paths.get(originalFilename); // originalFilename 可能为null或不含后缀
                String probedContentType = Files.probeContentType(tempPath);
                if (StringUtils.isNotBlank(probedContentType) && probedContentType.startsWith("image/")) {
                    isImage = true;
                    log.debug("uploadImage: MultipartFile content type '{}' is blank or not image, but probed as '{}' from filename '{}'", contentType, probedContentType, originalFilename);
                    if (StringUtils.isBlank(contentType)) contentType = probedContentType; // 使用探测到的类型
                }
            } catch (IOException | NullPointerException e) { // originalFilename 可能为 null
                log.warn("无法通过文件名探测 '{}' 的图片类型: {}", originalFilename, e.getMessage());
            }
        }

        if (!isImage) {
             throw new IllegalArgumentException("上传的文件不是有效的图片格式 (基于Content-Type和文件名探测)，或无法识别类型: " + originalFilename);
        }

        log.info("上传图片 (uploadImage) for file: {}", originalFilename);
        // 1. 上传原图
        String originalObjectId = uploadResource(file, resourceType); // 使用父类或当前类的uploadResource

        // 2. 生成并上传缩略图
        List<String> thumbnailSizes = getThumbnailSizes();
        String thumbnailSubDir = getThumbnailSubDir();

        if (thumbnailSizes != null && !thumbnailSizes.isEmpty() && StringUtils.isNotBlank(thumbnailSubDir)) {
            try (InputStream originalInputStream = file.getInputStream()) { // 重新获取输入流
                 BufferedImage originalImage = ImageIO.read(originalInputStream);
                 if (originalImage == null) {
                     log.warn("无法读取原始图片流来生成缩略图: {}", originalFilename);
                     return originalObjectId; // 即使缩略图失败，原图也已上传
                 }

                String originalFileExtension = FileUtils.getSuffix(originalObjectId); // 从objectId获取后缀，更可靠

                for (String sizeStr : thumbnailSizes) {
                    try {
                        String[] dimensions = sizeStr.split("x");
                        if (dimensions.length != 2) {
                            log.warn("无效的缩略图尺寸格式: {}，跳过。", sizeStr);
                            continue;
                        }
                        int width = Integer.parseInt(dimensions[0]);
                        int height = Integer.parseInt(dimensions[1]);

                        if (width <= 0 || height <= 0) {
                            log.warn("无效的缩略图尺寸值 (<=0): {}，跳过。", sizeStr);
                            continue;
                        }

                        // 构建缩略图对象名称
                        String thumbObjectName = buildThumbnailObjectName(originalObjectId, width, height, thumbnailSubDir);

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        Thumbnails.of(originalImage)
                            .size(width, height)
                            .outputFormat(originalFileExtension) // 保持原格式
                            .toOutputStream(baos);

                        try (InputStream thumbInputStream = new ByteArrayInputStream(baos.toByteArray())) {
                           String actualContentType = StringUtils.isNotBlank(contentType) ?
                                contentType : MimeTypeEnum.getMimeType(originalFileExtension);

                           sdkUploadInputStream(getBucketName(), thumbObjectName, thumbInputStream, actualContentType);
                           log.info("已生成并上传缩略图: {}, 尺寸: {}x{}", thumbObjectName, width, height);
                        }
                    } catch (NumberFormatException e) {
                        log.warn("解析缩略图尺寸失败: {}，错误: {}，跳过。", sizeStr, e.getMessage());
                    } catch (IOException e) {
                        log.error("生成或上传缩略图失败 (尺寸: {}): {}", sizeStr, e.getMessage(), e);
                        // 不抛出异常，确保主流程继续，原图上传不受影响
                    }
                }
            } catch (IOException e) {
                log.error("读取原图用于生成缩略图失败 ({}): {}", originalFilename, e.getMessage(), e);
                // 原图已上传，记录错误，但不影响主流程
            }
        }
        return originalObjectId; // 返回原图的ID
    }

    /**
     * 构建缩略图的对象名称/路径。
     * @param originalObjectId 原图的对象ID (例如: type/path/filename.ext)
     * @param width 缩略图宽度
     * @param height 缩略图高度
     * @param thumbnailSubDir 缩略图子目录名
     * @return 缩略图的对象ID (例如: type/path/thumbnailSubDir/filename_WxH.ext)
     */
    private String buildThumbnailObjectName(String originalObjectId, int width, int height, String thumbnailSubDir) {
        Path originalPath = Paths.get(originalObjectId);
        Path parentDir = originalPath.getParent(); // 可能为 null 如果 originalObjectId 是根路径下的文件
        String filename = originalPath.getFileName().toString();

        String prefix = FileUtils.getPrefix(filename);
        String suffix = FileUtils.getSuffix(filename);

        String thumbnailFilename = prefix + "_" + width + "x" + height + (StringUtils.isNotBlank(suffix) ? "." + suffix : "");

        Path thumbnailPath;
        if (parentDir != null) {
            thumbnailPath = parentDir.resolve(thumbnailSubDir).resolve(thumbnailFilename);
        } else {
            // 如果原图在根目录，则缩略图在 thumbnailSubDir/thumbnailFilename
            thumbnailPath = Paths.get(thumbnailSubDir, thumbnailFilename);
        }
        return thumbnailPath.toString().replace("\\", "/"); // 统一路径分隔符
    }

    @Override
    public String uploadImage(InputStream inputStream, String originalFilename, ResourceType resourceType) throws Exception {
        // 基础实现直接调用 uploadResource。子类或装饰器可以覆盖以添加特定图片处理。
        // 强调：对于流式上传，强烈建议通过文件名后缀或显式参数指明是图片，此处仅做基础委托
        if (inputStream == null) {
            throw new IllegalArgumentException("上传的图片文件流不能为空。");
        }
        if (StringUtils.isBlank(originalFilename)) {
            throw new IllegalArgumentException("原始文件名不能为空，无法确定图片类型和后缀。");
        }

        // 校验是否为图片类型 (基于文件名后缀)
        String fileExtension = FileUtils.getSuffix(originalFilename);
        boolean isImage = false;
        String contentType = null; // 稍后确定

        if (StringUtils.isNotBlank(fileExtension)) {
            String mimeType = MimeTypeEnum.getMimeType(fileExtension);
            if (mimeType.startsWith("image/")) {
                isImage = true;
                contentType = mimeType;
            }
        }

        if (!isImage) {
             // 尝试用NIO探测，如果能读到image类型也可以 (注意：这会消耗流)
             // 对于流式上传，如果传入的流不支持 mark/reset, Files.probeContentType 可能会导致问题
             // 此处为简化，如果后缀不明确，则认为不是图片，或依赖调用方保证流是图片流
             // 如果需要更严格的探测，可能需要先将流读入内存或临时文件
            log.warn("上传的文件流原始文件名 '{}' 未指示明确的图片类型 (基于后缀)。将尝试作为普通资源上传。", originalFilename);
             // throw new IllegalArgumentException("上传的文件流原始文件名 '" + originalFilename + "' 未指示明确的图片类型 (基于后缀)。");
        }

        log.info("上传图片流 (uploadImage via stream) for: {}", originalFilename);

        // 由于InputStream只能读取一次，我们需要先将其读取到内存中，以便既可以上传原图，也可以生成缩略图
        ByteArrayOutputStream baosOriginal = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            baosOriginal.write(buffer, 0, bytesRead);
        }
        byte[] originalImageData = baosOriginal.toByteArray();

        // 1. 上传原图
        String originalObjectId;
        try (InputStream originalImageStreamForUpload = new ByteArrayInputStream(originalImageData)) {
            originalObjectId = uploadResource(originalImageStreamForUpload, originalFilename, resourceType); // 使用父类或当前类的uploadResource
        }

        // 2. 生成并上传缩略图
        List<String> thumbnailSizes = getThumbnailSizes();
        String thumbnailSubDir = getThumbnailSubDir();

        if (isImage && thumbnailSizes != null && !thumbnailSizes.isEmpty() && StringUtils.isNotBlank(thumbnailSubDir)) {
            try (InputStream originalImageStreamForThumb = new ByteArrayInputStream(originalImageData)) {
                 BufferedImage originalImage = ImageIO.read(originalImageStreamForThumb);
                 if (originalImage == null) {
                     log.warn("无法读取原始图片流来生成缩略图: {}", originalFilename);
                     return originalObjectId;
                 }

                String originalFileExtFromId = FileUtils.getSuffix(originalObjectId);

                for (String sizeStr : thumbnailSizes) {
                    try {
                        String[] dimensions = sizeStr.split("x");
                        if (dimensions.length != 2) {
                            log.warn("无效的缩略图尺寸格式: {}，跳过。", sizeStr);
                            continue;
                        }
                        int width = Integer.parseInt(dimensions[0]);
                        int height = Integer.parseInt(dimensions[1]);

                        if (width <= 0 || height <= 0) {
                            log.warn("无效的缩略图尺寸值 (<=0): {}，跳过。", sizeStr);
                            continue;
                        }

                        String thumbObjectName = buildThumbnailObjectName(originalObjectId, width, height, thumbnailSubDir);

                        ByteArrayOutputStream baosThumb = new ByteArrayOutputStream();
                        Thumbnails.of(originalImage)
                            .size(width, height)
                            .outputFormat(originalFileExtFromId) // 保持原格式
                            .toOutputStream(baosThumb);

                        try (InputStream thumbInputStream = new ByteArrayInputStream(baosThumb.toByteArray())) {
                            // ContentType for thumbnail
                            String thumbContentType = MimeTypeEnum.getMimeType(originalFileExtFromId);
                            sdkUploadInputStream(getBucketName(), thumbObjectName, thumbInputStream, thumbContentType);
                            log.info("已生成并上传缩略图 (来自流): {}, 尺寸: {}x{}", thumbObjectName, width, height);
                        }
                    } catch (NumberFormatException e) {
                        log.warn("解析缩略图尺寸失败: {}，错误: {}，跳过。", sizeStr, e.getMessage());
                    } catch (IOException e) {
                        log.error("生成或上传缩略图失败 (来自流, 尺寸: {}): {}", sizeStr, e.getMessage(), e);
                    }
                }
            } catch (IOException e) {
                log.error("读取原图用于生成缩略图失败 (来自流 {}): {}", originalFilename, e.getMessage(), e);
            }
        }
        return originalObjectId;
    }

    @Override
    public String getImageUrl(String resourceId, ResourceType resourceType, String imageProcessingParams) {
        if (!isClientAvailable()) {
            log.error("{} 服务未初始化，无法获取图片资源ID {} 的URL。", getStorageType(), resourceId);
            return null;
        }
        if (StringUtils.isBlank(resourceId)) {
            return null;
        }

        // 尝试解析 imageProcessingParams 获取缩略图请求
        // 例如: "thumb_size=150x150"
        String targetResourceId = resourceId; // 默认为原图ID
        List<String> configuredThumbnailSizes = getThumbnailSizes(); // 获取配置的尺寸用于校验

        if (StringUtils.isNotBlank(imageProcessingParams) && configuredThumbnailSizes != null && !configuredThumbnailSizes.isEmpty()) {
            String[] params = imageProcessingParams.split("&");
            for (String param : params) {
                if (param.startsWith("thumb_size=")) {
                    String requestedSizeStr = param.substring("thumb_size=".length()); // "150x150"
                    // 校验请求的尺寸是否在配置中 (可选，但推荐)
                    boolean isValidRequestedSize = false;
                    int requestedWidth = 0;
                    int requestedHeight = 0;
                    try {
                        String[] dimensions = requestedSizeStr.split("x");
                        if (dimensions.length == 2) {
                            requestedWidth = Integer.parseInt(dimensions[0]);
                            requestedHeight = Integer.parseInt(dimensions[1]);
                            // 检查该尺寸是否是预先生成的尺寸之一
                            for (String configuredSize : configuredThumbnailSizes) {
                                if (configuredSize.equals(requestedSizeStr)) {
                                    isValidRequestedSize = true;
                                    break;
                                }
                            }
                        }
                    } catch (NumberFormatException e) {
                        log.warn("解析请求的缩略图尺寸参数失败: {}", requestedSizeStr);
                    }

                    if (isValidRequestedSize) {
                        String thumbnailSubDir = getThumbnailSubDir();
                        if (StringUtils.isNotBlank(thumbnailSubDir)) {
                            targetResourceId = buildThumbnailObjectName(resourceId, requestedWidth, requestedHeight, thumbnailSubDir);
                            log.debug("请求缩略图URL，尺寸: {}, 目标对象ID: {}", requestedSizeStr, targetResourceId);
                        }
                        break;
                    } else {
                        log.warn("请求的缩略图尺寸 {} 未配置或格式无效，将返回原图URL。", requestedSizeStr);
                    }
                }
            }
        }

        // 获取基础URL，这可能已经是包含bucket和endpoint的完整域名前缀
        String baseUrl = getConfiguredBaseUrl();

        if (StringUtils.isBlank(baseUrl)) {
            log.warn("{} 存储的baseUrl未配置，无法为资源ID {} 生成公开URL。请检查配置或使用预签名URL。", getStorageType(), targetResourceId);
            return null;
        }

        String cleanBaseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        String cleanResourceId = targetResourceId.startsWith("/") ? targetResourceId.substring(1) : targetResourceId;

        String finalUrl = cleanBaseUrl + "/" + cleanResourceId;

        // 如果是原图请求，并且仍有其他 imageProcessingParams (非 thumb_size)，则附加它们
        // 注意：对于已生成的缩略图，通常不应再附加额外的处理参数
        if (targetResourceId.equals(resourceId) && StringUtils.isNotBlank(imageProcessingParams)) {
            // 清理掉我们处理过的 thumb_size 参数，只附加其他参数
            String remainingParams = imageProcessingParams.replaceAll("thumb_size=[^&]*&?", "").replaceAll("&thumb_size=[^&]*", "");
            if (remainingParams.startsWith("&")) remainingParams = remainingParams.substring(1);

            if (StringUtils.isNotBlank(remainingParams)) {
                if (finalUrl.contains("?")) {
                    finalUrl += "&" + (remainingParams.startsWith("&") || remainingParams.startsWith("?") ? remainingParams.substring(1) : remainingParams);
                } else {
                    finalUrl += (remainingParams.startsWith("?") ? "" : "?") + remainingParams;
                }
            }
        }
        log.debug("Generated image URL for original resourceId '{}', effective resourceId '{}', params '{}': {}", resourceId, targetResourceId, imageProcessingParams, finalUrl);
        return finalUrl;
    }

    // 实现新的 generatePresignedUrl 重载方法
    @Override
    public String generatePresignedUrl(String resourceId, ResourceType resourceType, int expireTimeInMinutes, String imageProcessingParams) throws Exception {
        if (!isClientAvailable()) {
            throw new IllegalStateException(getStorageType() + " 服务未初始化或配置错误。");
        }
        if (StringUtils.isBlank(resourceId)) {
            return null;
        }
        if (expireTimeInMinutes <= 0) {
            expireTimeInMinutes = 15;
            log.debug("预签名URL的过期时间无效，已重置为默认值 {} 分钟。", 15);
        }

        // 如果存在 imageProcessingParams，需要确保存储服务支持对带参数的URL进行签名
        // 或者，某些服务可能需要将处理参数作为请求的一部分而不是URL查询字符串
        // 此基础实现假设参数可以直接附加到 objectName (如果服务支持) 或作为查询参数
        // 大多数对象存储的预签名URL是针对原始对象的，处理参数通常在访问时应用或服务本身支持对处理后结果签名

        String objectToSign = resourceId;
        if (StringUtils.isNotBlank(imageProcessingParams)) {
            // 注意: 并非所有服务都支持对带有查询参数的对象路径进行签名，或签名方式不同。
            // OSS: 可以对原始对象签名，然后在访问时附加处理参数。
            // MinIO: 类似，通常对原始对象签名。
            // 此处简单拼接，子类可能需要根据其SDK的签名能力覆盖此逻辑
            // objectToSign += (imageProcessingParams.startsWith("?") ? "" : "?") + imageProcessingParams;
            log.warn("为资源ID '{}' 生成带图片处理参数 '{}' 的预签名URL。注意：最终URL的有效性及图片处理是否生效，取决于具体存储服务及其CDN配置。通常建议对原始资源签名，并在访问时应用处理参数（如果CDN或源站支持）。", resourceId, imageProcessingParams);
        }

        String presignedUrl = sdkGeneratePresignedUrl(getBucketName(), objectToSign, expireTimeInMinutes);

        // 如果sdkGeneratePresignedUrl返回的是不带处理参数的URL，且我们有处理参数，尝试拼接
        // （但这可能破坏签名，除非CDN或服务允许这样做）
        if (StringUtils.isNotBlank(presignedUrl) && StringUtils.isNotBlank(imageProcessingParams) && !presignedUrl.contains(imageProcessingParams.startsWith("?")? imageProcessingParams.substring(1).split("=")[0] : imageProcessingParams.split("=")[0] )) {
             if (presignedUrl.contains("?")) {
                presignedUrl += "&" + (imageProcessingParams.startsWith("&") || imageProcessingParams.startsWith("?") ? imageProcessingParams.substring(1) : imageProcessingParams);
            } else {
                presignedUrl += (imageProcessingParams.startsWith("?") ? "" : "?") + imageProcessingParams;
            }
            log.debug("已将图片处理参数 '{}' 附加到 {} 生成的预签名URL上。", imageProcessingParams, getStorageType());
        }

        if (StringUtils.isNotBlank(presignedUrl)){
            log.debug("为资源 {} 生成的 {} 预签名URL ({}分钟有效): {}", resourceId, getStorageType(), expireTimeInMinutes, presignedUrl);
        }
        return presignedUrl;
    }

    @Override
    public String generatePresignedUrl(String resourceId, ResourceType resourceType, int expireTimeInMinutes) throws Exception {
        // 直接调用新的重载方法，imageProcessingParams 为 null
        return generatePresignedUrl(resourceId, resourceType, expireTimeInMinutes, null);
    }

    /**
     * 获取配置的缩略图尺寸列表。
     * @return 例如 ["150x150", "300x300"]。如果不支持或未配置，则返回空列表。
     */
    protected List<String> getThumbnailSizes() {
        return Collections.emptyList(); // 默认不生成缩略图，子类应覆盖此方法
    }

    /**
     * 获取配置的缩略图存储子目录名称。
     * @return 例如 "thumbs"。
     */
    protected String getThumbnailSubDir() {
        return "thumbs"; // 默认值，子类可覆盖
    }

    // +++ 实现 ResourceService 接口的 updateObjectTags 方法 +++
    @Override
    public void updateObjectTags(String resourceId, Map<String, String> tags) throws Exception {
        if (!isClientAvailable()) {
            throw new IllegalStateException(getStorageType() + " 服务未初始化或配置错误，无法更新标签。");
        }
        if (StringUtils.isBlank(resourceId)) {
            throw new IllegalArgumentException("资源ID不能为空，无法更新标签。");
        }
        if (tags == null) {
            // 或者根据需要，null表示清除所有标签，这取决于sdkUpdateObjectTags的实现期望
            // 为简单起见，这里假设null或空map不执行操作，或由sdk实现处理
            log.warn("标签参数为null，不执行对象 {} 的标签更新。", resourceId);
            return;
        }
        sdkUpdateObjectTags(getBucketName(), resourceId, tags);
        log.info("{} 对象 {} 的标签已更新。", getStorageType(), resourceId);
    }
    // --- 实现结束 ---

    @Override
    public String uploadBytes(byte[] imageBytes, String fileName, ResourceType resourceType) throws Exception {
        String bucketName = ensureClientAvailable();
        if (StringUtils.isEmpty(bucketName)) {
            throw new IllegalStateException(getStorageType() + "客户端不可用");
        }

        String objectKey = generateObjectKey(resourceType, fileName);
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes)) {
            sdkUploadFile(bucketName, objectKey, inputStream, imageBytes.length, MimeTypeEnum.getMimeTypeByFilename(fileName));
        }

        log.info("字节数组上传成功: {}, 大小: {} bytes, 内容类型: {}", objectKey, imageBytes.length, MimeTypeEnum.getMimeTypeByFilename(fileName));
        return objectKey;
    }

    private String ensureClientAvailable() {
        if (!isClientAvailable()) {
            try {
                log.info("初始化{}客户端...", getStorageType());
                sdkInitializeClient();

                if (isConfigurationMissing()) {
                    log.warn("{}配置缺失，无法初始化客户端。", getStorageType());
                    return null;
                }

                String bucketName = getBucketName();
                sdkEnsureBucketExists(bucketName);
                log.info("{}客户端初始化成功，存储桶'{}'可用。", getStorageType(), bucketName);
                return bucketName;
            } catch (Exception e) {
                markClientAsUnavailable(e);
                log.error("{}客户端初始化失败: {}", getStorageType(), e.getMessage(), e);
                return null;
            }
        }
        return getBucketName();
    }

    protected String generateObjectKey(ResourceType resourceType, String originalFilename) {
        String extension = FileUtils.getSuffix(originalFilename);
        String baseName = UUID.randomUUID().toString().replace("-", "");

        if (StringUtils.isNotEmpty(extension)) {
            baseName = baseName + "." + extension;
        }

        // 子目录结构: resourceType/yyyy/MM/dd/
        String directory = resourceType.getPath() + "/"
                + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) + "/";

        return directory + baseName;
    }

    /**
     * 根据文件名确定内容类型(MIME类型)
     *
     * @param filename 文件名
     * @return 对应的MIME类型，如果无法确定则返回通用的application/octet-stream
     */
    protected String determineContentType(String filename) {
        return MimeTypeEnum.getMimeTypeByFilename(filename);
    }
}
