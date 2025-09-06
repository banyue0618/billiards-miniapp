package org.dromara.common.file.handler;

import java.util.List;
import java.util.Optional;

/**
 * 用于从分页对象中提取记录列表的接口。
 * 实现类可以针对特定的分页库（如MyBatis Plus, Spring Data Page等）。
 */
@FunctionalInterface
public interface PageRecordExtractor {
    /**
     * 尝试从给定的分页对象中提取记录列表。
     *
     * @param pageObject 可能的分页对象
     * @return 如果 pageObject 是可识别的分页对象并且包含记录，则返回包含记录的 Optional&lt;List&lt;?&gt;&gt;；
     *         否则返回 Optional.empty()。
     */
    Optional<List<?>> extractRecords(Object pageObject);
} 