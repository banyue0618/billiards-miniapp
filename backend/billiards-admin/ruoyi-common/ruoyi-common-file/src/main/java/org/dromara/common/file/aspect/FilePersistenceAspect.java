package org.dromara.common.file.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.dromara.common.core.domain.R; // 假设通用返回类型是R
import org.dromara.common.file.annotation.FileResource;
import org.dromara.common.file.annotation.ProcessFilePersistence;
import org.dromara.common.file.service.ResourceService;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 文件持久化处理切面。
 * 监听带有 {@link ProcessFilePersistence} 注解的方法，
 * 在方法成功执行后，处理DTO中标记了 {@link FileResource} 的文件字段，将其状态更新为永久。
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class FilePersistenceAspect {

    private final ResourceService resourceService; // 通过构造函数注入

    @AfterReturning(pointcut = "@annotation(processAnnotation)", returning = "result")
    public void afterSuccessfulReturn(JoinPoint joinPoint, ProcessFilePersistence processAnnotation, Object result) {

        if (result != null && result instanceof R) {
            R<?> rResult = (R<?>) result;
            // 1. 检查方法是否成功执行 (根据实际项目中的成功判断标准)
            if (rResult.getCode() == 200) { // 示例：R.ok() 通常对应 code 200
                log.info("ProcessFilePersistence: 方法成功执行，开始处理文件持久化...");
                // 2. 遍历所有方法参数以查找包含 @FileResource 注解的 DTO
                Object[] args = joinPoint.getArgs();
                if(args != null && args.length > 0){
                    for (Object arg : args) {
                        if (arg != null) {
                            // 检查参数对象是否包含 @FileResource 注解的字段
                            processDtoArgument(arg);
                        }
                    }
                }
            }
        }
    }

    private void processDtoArgument(Object dto) {
        // 使用 ReflectionUtils.doWithFields 遍历DTO及其父类的所有字段
        final List<Field> fieldsToProcess = new ArrayList<>();
        ReflectionUtils.doWithFields(dto.getClass(), field -> {
            if (field.isAnnotationPresent(FileResource.class)) {
                fieldsToProcess.add(field);
            }
        });

        if (fieldsToProcess.isEmpty() && dto.getClass().getDeclaredFields().length > 0) {
            // 如果 DTO 本身有字段，但没有一个被 @FileResource 标记，则可以提前返回或记录日志
            // 但为了保持原逻辑的遍历行为（即使没有找到FileResource），此处不立即返回
            // log.debug("ProcessFilePersistence: DTO {} 中未找到 @FileResource 标记的字段。", dto.getClass().getName());
        }

        boolean foundFileResourceFieldInDto = !fieldsToProcess.isEmpty();

        for (Field field : fieldsToProcess) {
            try {
                ReflectionUtils.makeAccessible(field);
                Object fieldValue = field.get(dto);

                if (fieldValue == null) {
                    // 如果字段值为null，直接跳过，不记录警告
                    continue;
                }

                if (fieldValue instanceof String && !((String) fieldValue).isEmpty()) {
                    String resourceId = (String) fieldValue;
                    persistFile(resourceId);
                } else if (fieldValue instanceof List) {
                    List<?> listValue = (List<?>) fieldValue;
                    boolean processedItem = false;
                    for (Object item : listValue) {
                        if (item instanceof String && !((String) item).isEmpty()) {
                            persistFile((String) item);
                            processedItem = true;
                        }
                    }
                    if (!processedItem && !listValue.isEmpty()) {
                        // 列表不为空，但没有处理任何String类型的item
                        log.warn("ProcessFilePersistence: @FileResource 标记的列表字段 '{}.{}' 中没有有效的String类型的资源ID。",
                                 dto.getClass().getSimpleName(), field.getName());
                    }
                } else {
                    // fieldValue 不为null，但也不是 String 或 List，此时记录警告
                    log.warn("ProcessFilePersistence: @FileResource 标记的字段 '{}.{}' 的值不是String或List<String> (且不为null)，而是: {}，跳过。",
                             dto.getClass().getSimpleName(), field.getName(), fieldValue.getClass().getName());
                }
            } catch (Exception e) {
                log.error("ProcessFilePersistence: 处理字段 '{}.{}' 时发生未知错误。", dto.getClass().getSimpleName(), field.getName(), e);
            }
        }

        if (foundFileResourceFieldInDto) {
            log.info("ProcessFilePersistence: 已处理DTO {} 中的文件资源字段。", dto.getClass().getName());
        }
    }

    private void persistFile(String resourceId) {
        try {
            Map<String, String> permanentTags = Collections.singletonMap("status", "permanent");
            resourceService.updateObjectTags(resourceId, permanentTags);
            log.info("ProcessFilePersistence: 文件 {} 已成功标记为永久。", resourceId);
        } catch (Exception e) {
            log.error("ProcessFilePersistence: 更新文件 {} 标签为永久失败: {}", resourceId, e.getMessage(), e);
            // 异常处理策略：是否需要重试？是否需要通知？
        }
    }
}
