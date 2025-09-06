package org.dromara.common.mybatis;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.*;

/**
 * 用于控制不同的数据源采用不同的填充策略
 */
public interface DataSourceFillStrategy extends MetaObjectHandler {

    /**
     * 获取此策略对应的数据源名称。
     * 如果返回 null 或空字符串，则可能被视为主数据源/默认策略（取决于DelegatingMetaObjectHandler的实现）。
     * @return 数据源名称
     */
    String getDataSourceName();
}
