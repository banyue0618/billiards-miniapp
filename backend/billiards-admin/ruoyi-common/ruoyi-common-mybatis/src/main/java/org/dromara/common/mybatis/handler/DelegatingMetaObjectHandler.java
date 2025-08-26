package org.dromara.common.mybatis.handler;

import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import jakarta.annotation.PostConstruct;
import org.apache.ibatis.reflection.MetaObject;
import org.dromara.common.mybatis.DataSourceFillStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author zhangsip
 * @Version V1.0.0
 * @Since 1.0
 * @Date 2025/5/7
 */
@Component
@Primary
public class DelegatingMetaObjectHandler implements MetaObjectHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(DelegatingMetaObjectHandler.class);

    private final List<DataSourceFillStrategy> strategies;
    private final Map<String, DataSourceFillStrategy> strategyMap = new HashMap<>();
    private DataSourceFillStrategy defaultStrategy;

    // 通过构造函数注入所有 DataSourceFillStrategy 类型的Beans
    @Autowired
    public DelegatingMetaObjectHandler(List<DataSourceFillStrategy> strategies) {
        this.strategies = strategies;
    }

    @PostConstruct // Bean 初始化后执行
    public void initStrategies() {
        if (strategies == null || strategies.isEmpty()) {
            LOGGER.warn("No DataSourceFillStrategy beans found. MetaObjectHandler might not work as expected for specific data sources.");
            return;
        }
        for (DataSourceFillStrategy strategy : strategies) {
            String dsName = strategy.getDataSourceName();
            if (dsName != null && !dsName.trim().isEmpty()) {
                if (strategyMap.containsKey(dsName)) {
                    LOGGER.warn("Duplicate strategy found for data source: {}. Overwriting with: {}", dsName, strategy.getClass().getName());
                }
                strategyMap.put(dsName, strategy);
                LOGGER.info("Registered fill strategy: {} for data source: {}", strategy.getClass().getSimpleName(), dsName);
            } else {
                // 如果策略返回的 dataSourceName 为空或 null，我们将其视为默认策略
                // 这里假设只有一个默认策略，如果找到多个，则可能需要更复杂的逻辑来决定哪个是真正的默认
                if (this.defaultStrategy == null) {
                    this.defaultStrategy = strategy;
                    LOGGER.info("Registered default fill strategy: {}", strategy.getClass().getSimpleName());
                } else {
                    LOGGER.warn("Multiple default strategies found (getDataSourceName() is null/empty). Using the first one found: {}. Ignoring: {}",
                        this.defaultStrategy.getClass().getSimpleName(), strategy.getClass().getSimpleName());
                }
            }
        }
        // 确保至少有一个默认策略，如果没有通过getDataSourceName()为null/empty指定的，则可以尝试查找名为"primary"或特定名称的策略作为默认
        if (this.defaultStrategy == null) {
            this.defaultStrategy = strategyMap.get("admin");
            if(this.defaultStrategy == null && !strategies.isEmpty()){ //如果还没有，就取列表第一个
                this.defaultStrategy = strategies.get(0);
                LOGGER.warn("No explicit default strategy found, using the first available strategy as default: {}", this.defaultStrategy.getClass().getSimpleName());
            }
        }
        if (this.defaultStrategy == null) {
            LOGGER.error("CRITICAL: No default fill strategy could be determined. Auto-filling might not work correctly.");
            // 可以选择抛出异常，或者创建一个空操作的默认策略
            // this.defaultStrategy = new NoOpFillStrategy();
        }
    }

    private DataSourceFillStrategy getStrategy() {
        String currentDataSource = DynamicDataSourceContextHolder.peek();
        // 如果当前数据源是null/empty（可能代表主数据源或未切换），或者在map中找不到特定策略，则返回默认策略
        if (currentDataSource == null || currentDataSource.trim().isEmpty()) {
            LOGGER.debug("Current data source is null or empty, using default strategy.");
            return defaultStrategy;
        }
        return strategyMap.getOrDefault(currentDataSource, defaultStrategy);
    }

    @Override
    public void insertFill(MetaObject metaObject) {
        DataSourceFillStrategy strategy = getStrategy();
        LOGGER.debug("Using strategy: {} for insert fill on data source: {}", strategy.getClass().getSimpleName(), DynamicDataSourceContextHolder.peek());
        strategy.insertFill(metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        DataSourceFillStrategy strategy = getStrategy();
        LOGGER.debug("Using strategy: {} for update fill on data source: {}", strategy.getClass().getSimpleName(), DynamicDataSourceContextHolder.peek());
        strategy.updateFill(metaObject);
    }

}
