package org.dromara.billiards.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.annotation.WebFilter;

import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * 请求日志过滤器
 * 记录所有HTTP请求的基本信息
 */
@Slf4j
@Component
// 不使用WebFilter自动注册，改为使用FilterRegistrationBean手动注册
public class LogFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("LogFilter 初始化成功");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        // 记录请求信息
        String requestURI = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();
        String remoteAddr = httpRequest.getRemoteAddr();

        log.info("收到请求: {} {} 来自IP: {}", method, requestURI, remoteAddr);

        // 记录请求开始时间
        long startTime = System.currentTimeMillis();

        try {
            // 继续处理请求
            chain.doFilter(request, response);
        } finally {
            // 计算请求处理时间
            long endTime = System.currentTimeMillis();
            log.info("请求: {} {} 处理完成, 耗时: {}ms", method, requestURI, (endTime - startTime));
        }
    }

    @Override
    public void destroy() {
        log.info("LogFilter 已销毁");
    }
}
