package org.dromara.billiards.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.annotation.WebFilter;

import jakarta.servlet.http.HttpServletResponse;
import org.dromara.common.web.filter.RepeatedlyRequestWrapper;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * 请求体日志过滤器
 * 记录POST、PUT等请求的请求体内容
 */
@Slf4j
@Component
// 不使用WebFilter自动注册，改为使用FilterRegistrationBean手动注册
public class RequestBodyLogFilter implements Filter {

    // 需要记录请求体的内容类型列表
    private static final List<String> LOGGABLE_CONTENT_TYPES = Arrays.asList(
        MediaType.APPLICATION_JSON_VALUE,
        MediaType.APPLICATION_XML_VALUE,
        MediaType.APPLICATION_FORM_URLENCODED_VALUE,
        MediaType.TEXT_PLAIN_VALUE,
        MediaType.TEXT_XML_VALUE
    );

    // 最大记录的请求体长度，防止过大的请求体占用日志空间
    private static final int MAX_PAYLOAD_LENGTH = 10000;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("RequestBodyLogFilter 初始化成功");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (!(request instanceof HttpServletRequest)) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String method = httpRequest.getMethod();
        String contentType = httpRequest.getContentType();

        // 只处理POST、PUT、PATCH请求，且内容类型在可记录列表中
        if (shouldLogRequestBody(method, contentType)) {
            // 使用可重复读取的请求包装器
            RepeatedlyRequestWrapper requestWrapper = new RepeatedlyRequestWrapper(httpRequest, response);

            // 读取并记录请求体
            String requestBody = StreamUtils.copyToString(requestWrapper.getInputStream(), StandardCharsets.UTF_8);

            // 截断过长的请求体
            if (requestBody.length() > MAX_PAYLOAD_LENGTH) {
                requestBody = requestBody.substring(0, MAX_PAYLOAD_LENGTH) + "... [内容过长已截断]";
            }

            log.info("请求体内容: {}", requestBody);

            // 继续过滤器链，使用包装后的请求
            chain.doFilter(requestWrapper, response);
        } else {
            // 对于不需要记录请求体的请求，直接继续
            chain.doFilter(request, response);
        }
    }

    /**
     * 判断是否需要记录请求体
     */
    private boolean shouldLogRequestBody(String method, String contentType) {
        if (contentType == null) {
            return false;
        }

        // 只记录POST、PUT、PATCH请求的请求体
        boolean isMethodLoggable = "POST".equals(method) || "PUT".equals(method) || "PATCH".equals(method);

        // 检查内容类型是否在可记录列表中
        boolean isContentTypeLoggable = LOGGABLE_CONTENT_TYPES.stream()
                .anyMatch(type -> contentType.toLowerCase().contains(type.toLowerCase()));

        return isMethodLoggable && isContentTypeLoggable;
    }

    @Override
    public void destroy() {
        log.info("RequestBodyLogFilter 已销毁");
    }
}
