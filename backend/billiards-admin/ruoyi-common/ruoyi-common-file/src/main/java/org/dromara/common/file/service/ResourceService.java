package org.dromara.common.file.service;

import org.dromara.common.file.enums.ResourceType;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Map;

/**
 * 资源服务接口
 * 提供统一的资源上传、获取URL等操作，并扩展对图片的处理能力。
 */
public interface ResourceService {

    /**
     * 上传通用资源文件。
     *
     * @param file 待上传的 MultipartFile 文件对象。
     * @param resourceType 资源类型，用于分类和确定存储路径。
     * @return 资源ID，成功上传后返回，用于后续访问或管理该资源。
     * @throws Exception 上传过程中可能发生的IO异常或其他错误。
     */
    String uploadResource(MultipartFile file, ResourceType resourceType) throws Exception;

    /**
     * 上传通用资源流。
     *
     * @param inputStream 资源输入流。
     * @param originalFilename 原始文件名，用于生成存储时的文件名或元数据。
     * @param resourceType 资源类型，用于分类和确定存储路径。
     * @return 资源ID，成功上传后返回。
     * @throws Exception 上传过程中可能发生的IO异常或其他错误。
     */
    String uploadResource(InputStream inputStream, String originalFilename, ResourceType resourceType) throws Exception;

    /**
     * 上传字节数组文件
     *
     * @param imageBytes 字节数组数据
     * @param fileName 文件名
     * @param resourceType 资源类型
     * @return 资源ID，成功上传后返回
     * @throws Exception 上传过程中可能发生的异常
     */
    String uploadBytes(byte[] imageBytes, String fileName, ResourceType resourceType) throws Exception;

    /**
     * 获取通用资源访问URL。
     *
     * @param resourceId 资源ID，通常是上传时返回的ID。
     * @param resourceType 资源类型，与上传时指定的类型一致。
     * @return 完整的公开访问URL。如果资源不存在或无法访问，可能返回null或抛出异常，具体行为由实现类定义。
     */
    String getResourceUrl(String resourceId, ResourceType resourceType);

    /**
     * 上传图片文件。
     * 对于支持图片处理的存储服务，可能不需要额外生成缩略图，而是通过URL参数获取。
     * 对于不支持的服务，此方法可能触发后端异步生成预定义的缩略图规格，或仅作为普通文件上传。
     *
     * @param file 图片文件
     * @param resourceType 资源类型 (应为图片相关的类型，如USER_AVATAR, PRODUCT_IMAGE)
     * @return 原始图片的资源ID
     * @throws Exception 上传或处理异常
     */
    String uploadImage(MultipartFile file, ResourceType resourceType) throws Exception;

    /**
     * 上传图片流。
     * @param inputStream 图片流
     * @param originalFilename 原始文件名
     * @param resourceType 资源类型
     * @return 原始图片的资源ID
     * @throws Exception 上传或处理异常
     */
    String uploadImage(InputStream inputStream, String originalFilename, ResourceType resourceType) throws Exception;

    /**
     * 获取图片的访问URL，可以指定图片处理参数（例如缩略图规格）。
     *
     * @param resourceId 原始图片的资源ID。
     * @param resourceType 图片的资源类型 (可选，因为resourceId本身可能已包含路径信息，但提供它可以用于验证或辅助逻辑)
     * @param imageProcessingParams 特定于存储服务商的图片处理参数字符串 (例如阿里云OSS的 "?x-oss-process=image/resize,w_100")。
     *                              对于不支持URL参数处理的服务，此参数可能被忽略，或者可以根据此参数查找预生成的缩略图。
     * @return 处理后的图片访问URL；如果不支持处理或resourceId无效，则可能返回原图URL或null。
     */
    String getImageUrl(String resourceId, ResourceType resourceType, String imageProcessingParams);

    /**
     * 删除资源。
     *
     * @param resourceId 资源ID。
     * @param resourceType 资源类型。
     * @return 如果成功删除资源，则返回 true；否则返回 false。
     * @throws Exception 删除过程中可能发生的错误。
     */
    boolean deleteResource(String resourceId, ResourceType resourceType) throws Exception;

    /**
     * 生成私有资源的临时访问URL (预签名URL)。
     * 也可用于获取带时效性的图片URL（包括经过处理的图片，如果服务商支持对处理后资源签名）。
     *
     * @param resourceId 资源ID。
     * @param resourceType 资源类型。
     * @param expireTimeInMinutes 过期时间（分钟）。
     * @param imageProcessingParams (可选) 如果是对图片进行处理后的URL进行签名，传入图片处理参数。
     * @return 带签名的临时访问URL。如果不支持或失败，可能返回null或抛出异常。
     * @throws Exception 生成URL过程中可能发生的错误。
     */
    String generatePresignedUrl(String resourceId, ResourceType resourceType, int expireTimeInMinutes, String imageProcessingParams) throws Exception;

    /**
     * 重载 generatePresignedUrl，不含图片处理参数，用于通用文件或原始图片。
     */
    String generatePresignedUrl(String resourceId, ResourceType resourceType, int expireTimeInMinutes) throws Exception;

    /**
     * 更新指定对象的标签。
     * 如果对象不存在或服务不支持标签，则可能静默失败或抛出异常，具体行为由实现决定。
     *
     * @param resourceId 资源的唯一标识 (对象键)
     * @param tags       要设置的标签，键值对形式。如果传入null或空Map，可能表示清除所有标签 (取决于实现)。
     * @throws Exception 如果操作失败
     */
    void updateObjectTags(String resourceId, Map<String, String> tags) throws Exception;

}
