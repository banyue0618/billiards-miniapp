package org.dromara.billiards.config;

import org.dromara.billiards.framework.filter.LogFilter;
import org.dromara.billiards.framework.filter.RequestBodyLogFilter;
import org.dromara.billiards.framework.filter.ResponseBodyLogFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

/**
 * 过滤器配置类
 * 单独配置过滤器，避免与WebMvc配置冲突
 */
@Configuration
public class BilliardsFilterConfig {

    @Autowired
    private LogFilter logFilter;

    @Autowired
    private RequestBodyLogFilter requestBodyLogFilter;

    @Autowired
    private ResponseBodyLogFilter responseBodyLogFilter;

    /**
     * 注册日志过滤器
     */
    @Bean
    public FilterRegistrationBean<LogFilter> customLogFilter() {
        FilterRegistrationBean<LogFilter> registrationBean = new FilterRegistrationBean<>();
        // 注入过滤器
        registrationBean.setFilter(logFilter);
        // 设置过滤器的URL模式
        registrationBean.addUrlPatterns("/*");
        // 设置过滤器的名称
        registrationBean.setName("customLogFilter");
        // 设置过滤器的执行顺序
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 10);
        return registrationBean;
    }

    /**
     * 注册请求体日志过滤器
     */
    @Bean
    public FilterRegistrationBean<RequestBodyLogFilter> customRequestBodyLogFilter() {
        FilterRegistrationBean<RequestBodyLogFilter> registrationBean = new FilterRegistrationBean<>();
        // 注入过滤器
        registrationBean.setFilter(requestBodyLogFilter);
        // 设置过滤器的URL模式
        registrationBean.addUrlPatterns("/*");
        // 设置过滤器的名称
        registrationBean.setName("customRequestBodyLogFilter");
        // 设置过滤器的执行顺序
        registrationBean.setOrder(FilterRegistrationBean.LOWEST_PRECEDENCE + 20);
        return registrationBean;
    }

    /**
     * 注册响应体日志过滤器
     */
    @Bean
    public FilterRegistrationBean<ResponseBodyLogFilter> customResponseBodyLogFilter() {
        FilterRegistrationBean<ResponseBodyLogFilter> registrationBean = new FilterRegistrationBean<>();
        // 注入过滤器
        registrationBean.setFilter(responseBodyLogFilter);
        // 设置过滤器的URL模式
        registrationBean.addUrlPatterns("/*");
        // 设置过滤器的名称
        registrationBean.setName("customResponseBodyLogFilter");
        // 设置过滤器的执行顺序
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 30);
        return registrationBean;
    }
}
