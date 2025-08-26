package org.dromara.billiards.config;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.dromara.common.file.handler.PageRecordExtractor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * @Description MyBatis Plus IPage 分页记录提取器实现。
 * @Author zhangsip
 * @Version V1.0.0
 * @Since 1.0
 * @Date 2025/5/15
 */
@Component
public class MyBatisPlusPageRecordExtractor implements PageRecordExtractor {
    @Override
    public Optional<List<?>> extractRecords(Object pageObject) {
        if (pageObject instanceof IPage) {
            IPage<?> iPage = (IPage<?>) pageObject;
            return Optional.ofNullable(iPage.getRecords());
        }
        return Optional.empty();
    }
}
