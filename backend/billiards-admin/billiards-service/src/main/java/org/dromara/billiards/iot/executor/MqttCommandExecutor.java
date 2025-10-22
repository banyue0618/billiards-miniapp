package org.dromara.billiards.iot.executor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.billiards.iot.mqtt.MqttClientManager;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.retry.RetryContext;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@RequiredArgsConstructor
public class MqttCommandExecutor implements IoTCommandExecutor {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MqttClientManager clientManager;

    @Override
    public boolean supports(String protocol) {
        return "mqtt".equalsIgnoreCase(protocol);
    }

    @Override
    @Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public ExecutionResult execute(ExecutionContext context) throws Exception {
        long start = System.currentTimeMillis();

        Map<String, Object> payload = Map.of(
            "deviceCode", context.deviceCode,
            "command", context.command,
            "params", parseParams(context.paramsJson)
        );
        log.debug("[MQTT] publish device={} topic={} command={} params={}",
            context.deviceCode, context.topic, context.command, context.paramsJson);

        byte[] bytes = objectMapper.writeValueAsString(payload).getBytes(StandardCharsets.UTF_8);
        ;
        Future<?> task = CompletableFuture.runAsync(() -> {
            try {
                clientManager.publish(context.protocolConfig, bytes);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        task.get(2, TimeUnit.SECONDS);
        ExecutionResult r = ExecutionResult.ok(System.currentTimeMillis() - start);
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


