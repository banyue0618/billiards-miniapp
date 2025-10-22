package org.dromara.billiards.iot.executor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.retry.RetryContext;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class HttpCommandExecutor implements IoTCommandExecutor {

    private final RestTemplate iotRestTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean supports(String protocol) {
        return "http".equalsIgnoreCase(protocol);
    }

    @Override
    @Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public ExecutionResult execute(ExecutionContext context) throws Exception {
        long start = System.currentTimeMillis();
        Map<String, Object> cfg = context.protocolConfig;
        String url = (String) cfg.get("url");
        String method = String.valueOf(cfg.getOrDefault("method", "POST"));
        Map<String, String> headersCfg = objectMapper.convertValue(cfg.getOrDefault("headers", Map.of()), new TypeReference<Map<String, String>>(){});

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headersCfg.forEach(headers::set);

        // Body 包含 command 与 params
        Map<String, Object> body = Map.of(
            "deviceCode", context.deviceCode,
            "command", context.command,
            "params", parseParams(context.paramsJson)
        );
        HttpEntity<Map<String, Object>> req = new HttpEntity<>(body, headers);

        ResponseEntity<String> resp;
        if ("GET".equalsIgnoreCase(method)) {
            resp = iotRestTemplate.exchange(url, HttpMethod.GET, req, String.class);
        } else if ("PUT".equalsIgnoreCase(method)) {
            resp = iotRestTemplate.exchange(url, HttpMethod.PUT, req, String.class);
        } else if ("DELETE".equalsIgnoreCase(method)) {
            resp = iotRestTemplate.exchange(url, HttpMethod.DELETE, req, String.class);
        } else {
            resp = iotRestTemplate.exchange(url, HttpMethod.POST, req, String.class);
        }

        if (resp.getStatusCode().is2xxSuccessful()) {
            ExecutionResult r = ExecutionResult.ok(System.currentTimeMillis() - start);
            r.retryCount = currentRetryCount();
            return r;
        }
        ExecutionResult r = ExecutionResult.fail("HTTP status:" + resp.getStatusCode().value());
        r.retryCount = currentRetryCount();
        return r;
    }

    @Override
    public void record(ExecutionContext ctx, ExecutionResult result) {

    }

    @Override
    public void alertIfNeeded(ExecutionContext ctx, ExecutionResult result) {

    }

    private Map<String, Object> parseParams(String json) throws Exception {
        if (json == null || json.isBlank()) return Map.of();
        return objectMapper.readValue(json, new TypeReference<Map<String, Object>>(){});
    }

    private int currentRetryCount() {
        RetryContext ctx = RetrySynchronizationManager.getContext();
        return ctx == null ? 0 : ctx.getRetryCount();
    }
}


