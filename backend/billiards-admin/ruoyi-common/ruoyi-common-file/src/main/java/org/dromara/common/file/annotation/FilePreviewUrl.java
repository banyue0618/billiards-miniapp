package org.dromara.common.file.annotation;

import org.dromara.common.file.enums.ResourceType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记一个字段，用于通过AOP自动填充文件预览URL。
 * <p>
 * 使用方法：
 * <pre>
 * public class YourDto {
 *     // 假设此字段存储 resourceId
 *     private String coverImage;
 *
 *     // 此字段将被填充为 coverImage 对应的预览URL
 *     &#64;FilePreviewUrl(path = "coverImage", resourceType = ResourceType.STORE_COVER)
 *     private String coverImagePreviewUrl;
 * }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FilePreviewUrl {

    /**
     * 指向包含 resourceId 的源字段的名称。
     * 例如，如果 resourceId 存储在名为 "coverImage" 的字段中，则此处应填写 "coverImage"。
     */
    String path();

    /**
     * 资源类型，用于生成正确的预览URL。
     */
    ResourceType resourceType();

    /**
     * （可选）当源字段(path)的值为空或null时，是否将此注解字段也设置成null。
     * 默认为 true。如果为 false，则即使源字段为空，此字段也不会被修改（保留其可能存在的初始值）。
     */
    boolean setNullIfSourceIsNull() default true;
} 