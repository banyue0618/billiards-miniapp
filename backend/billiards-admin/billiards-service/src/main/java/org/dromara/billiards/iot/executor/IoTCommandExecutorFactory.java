package org.dromara.billiards.iot.executor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class IoTCommandExecutorFactory {

    private final List<IoTCommandExecutor> executors;

    public IoTCommandExecutor getByProtocol(String protocol) {
        return executors.stream()
            .filter(e -> e.supports(protocol))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("No executor for protocol: " + protocol));
    }
}


