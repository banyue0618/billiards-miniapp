package org.dromara.common.file.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.dromara.common.core.utils.reflect.ReflectUtils;
import org.dromara.common.file.annotation.FilePreviewUrl;
import org.dromara.common.file.enums.ResourceType;
import org.dromara.common.file.handler.PageRecordExtractor;
import org.dromara.common.file.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Aspect
@Component
@Slf4j
public class FilePreviewAspect {

    private final ResourceService resourceService;
    private final Optional<PageRecordExtractor> pageRecordExtractor;

    @Autowired
    public FilePreviewAspect(ResourceService resourceService, @Autowired(required = false) PageRecordExtractor pageRecordExtractor) {
        this.resourceService = resourceService;
        this.pageRecordExtractor = Optional.ofNullable(pageRecordExtractor);
    }

    @Pointcut("execution(public * org.dromara..*Controller.*(..))")
    public void filePreviewPointcut() {
    }

    @Around("filePreviewPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();

        if (result != null) {
            if (result instanceof org.dromara.common.core.domain.R) {
                try {
                    Object dataToProcess = ReflectUtils.invokeGetter(result, "data");
                    if (dataToProcess != null) {
                        if (dataToProcess instanceof Collection) {
                            ((Collection<?>) dataToProcess).forEach(this::processItem);
                        } else {
                            boolean processedAsPage = false;
                            if (pageRecordExtractor.isPresent()) {
                                Optional<List<?>> recordsOpt = pageRecordExtractor.get().extractRecords(dataToProcess);
                                if (recordsOpt.isPresent()) {
                                    recordsOpt.get().forEach(this::processItem);
                                    processedAsPage = true;
                                    log.debug("Processed result as a page using PageRecordExtractor.");
                                }
                            }
                            if (!processedAsPage) {
                                processItem(dataToProcess);
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("Error processing @FilePreviewUrl: {}", e.getMessage(), e);
                }
            }
        }
        return result;
    }

    private void processItem(Object item) {
        if (item == null) {
            return;
        }
        Class<?> clazz = item.getClass();
        Field[] fields = ReflectUtils.getFields(clazz);

        for (Field field : fields) {
            FilePreviewUrl annotation = field.getAnnotation(FilePreviewUrl.class);
            if (annotation != null) {
                try {
                    String resourceIdFieldName = annotation.path();
                    ResourceType resourceType = annotation.resourceType();
                    Object resourceIdObj = ReflectUtils.invokeGetter(item, resourceIdFieldName);

                    if (resourceIdObj instanceof String && StringUtils.hasText((String) resourceIdObj)) {
                        String resourceId = (String) resourceIdObj;
                        String previewUrl = resourceService.getResourceUrl(resourceId, resourceType);
                        ReflectUtils.invokeSetter(item, field.getName(), previewUrl);
                        log.debug("Successfully set preview URL for field '{}' on object of type '{}'", field.getName(), clazz.getSimpleName());
                    } else {
                        if (annotation.setNullIfSourceIsNull()) {
                            ReflectUtils.invokeSetter(item, field.getName(), null);
                        }
                        if (resourceIdObj == null || (resourceIdObj instanceof String && !StringUtils.hasText((String) resourceIdObj))){
                            log.debug("Source field '{}' for preview URL field '{}' is null or empty. Setting preview field to null (if configured).",
                                      resourceIdFieldName, field.getName());
                        } else {
                            log.warn("Source field '{}' (for @FilePreviewUrl on '{}') in class '{}' is not a non-empty String. Actual type: {}. Skipping URL generation.",
                                     resourceIdFieldName, field.getName(), clazz.getSimpleName(), resourceIdObj != null ? resourceIdObj.getClass().getName() : "null");
                        }
                    }
                } catch (Exception e) {
                    log.error("Error processing @FilePreviewUrl for field '{}' in class '{}': {}",
                              field.getName(), clazz.getSimpleName(), e.getMessage(), e);
                }
            }
        }
    }
}
