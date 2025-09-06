package org.dromara.common.file.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记一个方法，表示在其成功执行后，需要处理与该方法参数关联的DTO中
 * 使用 {@link FileResource} 注解标记的文件资源的持久化逻辑。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ProcessFilePersistence {

}
