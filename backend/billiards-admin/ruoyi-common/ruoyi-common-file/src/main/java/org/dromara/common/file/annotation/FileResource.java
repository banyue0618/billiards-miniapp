package org.dromara.common.file.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记一个字段作为文件资源标识符 (例如对象存储中的对象键)。
 * 用于配合 {@link ProcessFilePersistence} 注解进行文件持久化处理。
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FileResource {
    // 可以添加属性，例如标记该字段是否为必须，或文件类型等，暂时保持简单
    // String type() default "";
} 