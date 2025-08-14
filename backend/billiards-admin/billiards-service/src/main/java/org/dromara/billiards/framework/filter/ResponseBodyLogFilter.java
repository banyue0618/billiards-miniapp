package org.dromara.billiards.framework.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 响应体日志过滤器
 * 记录API响应的内容
 */
@Slf4j
@Component
// 不使用WebFilter自动注册，改为使用FilterRegistrationBean手动注册
public class ResponseBodyLogFilter implements Filter {

    // 需要记录响应体的内容类型列表
    private static final List<String> LOGGABLE_CONTENT_TYPES = Arrays.asList(
        MediaType.APPLICATION_JSON_VALUE,
        MediaType.APPLICATION_XML_VALUE,
        MediaType.TEXT_PLAIN_VALUE,
        MediaType.TEXT_XML_VALUE
    );

    // 最大记录的响应体长度，防止过大的响应体占用日志空间
    private static final int MAX_PAYLOAD_LENGTH = 10000;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("ResponseBodyLogFilter 初始化成功");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 只处理API请求的响应
        if (shouldCaptureResponse(httpRequest)) {
            // 包装响应
            ResponseBodyWrapper responseWrapper = new ResponseBodyWrapper(httpResponse);

            try {
                // 继续过滤器链，使用包装后的响应
                chain.doFilter(request, responseWrapper);

                // 获取和记录响应体
                String contentType = responseWrapper.getContentType();

                // 只记录指定类型的响应
                if (isContentTypeLoggable(contentType)) {
                    String responseBody = responseWrapper.getContentAsString();

                    // 截断过长的响应体
                    if (responseBody.length() > MAX_PAYLOAD_LENGTH) {
                        responseBody = responseBody.substring(0, MAX_PAYLOAD_LENGTH) + "... [内容过长已截断]";
                    }

                    log.info("API响应: {} - 状态码: {} - 内容: {}",
                            httpRequest.getRequestURI(),
                            responseWrapper.getStatus(),
                            responseBody);
                }

            } catch (Exception e) {
                log.error("记录响应内容时发生错误", e);
                // 发生错误时，继续原始的过滤器链
                chain.doFilter(request, response);
            }
        } else {
            // 对于不需要记录响应体的请求，直接继续
            chain.doFilter(request, response);
        }
    }

    /**
     * 判断是否需要捕获响应
     */
    private boolean shouldCaptureResponse(HttpServletRequest request) {
        String uri = request.getRequestURI();

        // 如果是API请求，则捕获响应
        return uri.startsWith("/api/") &&
               !uri.contains("/uploads/") &&
               !uri.endsWith(".jpg") &&
               !uri.endsWith(".png") &&
               !uri.endsWith(".gif") &&
               !uri.endsWith(".css") &&
               !uri.endsWith(".js");
    }

    /**
     * 判断内容类型是否需要记录
     */
    private boolean isContentTypeLoggable(String contentType) {
        if (contentType == null) {
            return false;
        }

        return LOGGABLE_CONTENT_TYPES.stream()
                .anyMatch(type -> contentType.toLowerCase().contains(type.toLowerCase()));
    }

    @Override
    public void destroy() {
        log.info("ResponseBodyLogFilter 已销毁");
    }
}
