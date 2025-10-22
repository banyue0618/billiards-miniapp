package org.dromara.billiards.iot.service;

import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.billiards.common.constant.TriggerSceneEnum;
import org.dromara.billiards.iot.domain.BlsIotDevice;
import org.dromara.billiards.iot.domain.BlsIotDeviceBinding;
import org.dromara.billiards.iot.executor.IoTCommandExecutor;
import org.dromara.billiards.iot.executor.IoTCommandExecutorFactory;
import org.dromara.billiards.iot.mapper.BlsIotDeviceBindingMapper;
import org.dromara.billiards.iot.mapper.BlsIotDeviceMapper;
import org.dromara.common.satoken.utils.LoginHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class IoTOrchestrationService {

    private final IBlsIotDeviceBindingService iotDeviceBindingService;
    private final IBlsIotDeviceService iotDeviceService;
    private final IoTCommandExecutorFactory executorFactory;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void openTable(String tableId, String orderId){
        TriggerContext context = TriggerContext.buildTriggerContext("user:" + LoginHelper.getUserIdStr(), orderId);
        triggerScene(tableId, TriggerSceneEnum.OPEN_TABLE.getCode(), context);
    }

    public void openTableByAdmin(String tableId, String orderId){
        TriggerContext context = TriggerContext.buildTriggerContext("admin:" + LoginHelper.getUserIdStr(), orderId);
        triggerScene(tableId, TriggerSceneEnum.OPEN_TABLE.getCode(), context);
    }

    public void closeTable(String tableId, String orderId){
        TriggerContext context = TriggerContext.buildTriggerContext("user:" + LoginHelper.getUserIdStr(), orderId);
        triggerScene(tableId, TriggerSceneEnum.CLOSE_TABLE.getCode(), context);
    }

    public void closeTableByAdmin(String tableId, String orderId){
        TriggerContext context = TriggerContext.buildTriggerContext("admin:" + LoginHelper.getUserIdStr(), orderId);
        triggerScene(tableId, TriggerSceneEnum.CLOSE_TABLE.getCode(), context);
    }



    @Async("iotExecutor")
    public void triggerScene(String tableId, String scene, TriggerContext ctx) {
        try {
            List<BlsIotDeviceBinding> bindings = iotDeviceBindingService.selectByTableIdAndScene(tableId, scene);
            if (bindings == null || bindings.isEmpty()) {
                log.info("No bindings found for scene. tableId={}, scene={}", tableId, scene);
                return;
            }

            for (BlsIotDeviceBinding b : bindings) {
                try {
                    BlsIotDevice device = iotDeviceService.queryByDeviceCode(b.getDeviceCode());
                    if (device == null) {
                        // TODO: 记录控制日志 + 告警（设备不存在）
                        log.warn("Device not found: {}", b.getDeviceCode());
                        continue; // 不中断
                    }

                    IoTCommandExecutor executor = executorFactory.getByProtocol(device.getProtocol());

                    IoTCommandExecutor.ExecutionContext execCtx = buildContext(b, device, ctx, scene, tableId);

                    IoTCommandExecutor.ExecutionResult result = executor.execute(execCtx);
                    executor.record(execCtx, result); // 挂点：日志落库
                    executor.alertIfNeeded(execCtx, result); // 挂点：必要时告警

                } catch (Exception ex) {
                    // TODO: 写入控制失败日志 + 告警
                    log.error("Execute command failed. tableId={}, scene={}, bindingId={}", tableId, scene, b.getId(), ex);
                    // 不中断后续
                }
            }
        } catch (Exception e) {
            log.error("Trigger scene error. tableId={}, scene={}", tableId, scene, e);
        }
    }

    private IoTCommandExecutor.ExecutionContext buildContext(BlsIotDeviceBinding binding,
                                                             BlsIotDevice device,
                                                             TriggerContext ctx,
                                                             String scene,
                                                             String tableId) {
        var execCtx = new IoTCommandExecutor.ExecutionContext();
        execCtx.deviceCode = device.getCode();
        execCtx.protocol = device.getProtocol();
        execCtx.command = binding.getCommand();
        execCtx.paramsJson = binding.getParams();
        Map<String, Object> config = parseConfig(device.getProtocolConfig());
        execCtx.protocolConfig = config;
        execCtx.topic = config.getOrDefault("topic", binding.getMqttTopic()).toString();
        execCtx.triggerBy = ctx == null ? "system" : ctx.triggerBy;
        execCtx.triggerScene = scene;
        execCtx.tableId = tableId;
        execCtx.orderId = ctx == null ? null : ctx.orderId;
        return execCtx;
    }


    private Map<String, Object> parseConfig(String json) {
        try {
            if (json == null || json.isBlank()) return Map.of();
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>(){});
        } catch (Exception e) {
            log.warn("Bad protocol_config json: {}", json);
            return Map.of();
        }
    }

    public static class TriggerContext {
        public String triggerBy; // order/admin/system
        public String orderId;
        public String traceId;

        public static TriggerContext buildTriggerContext(String triggerBy, String orderId){
            TriggerContext ctx = new TriggerContext();
            ctx.triggerBy = triggerBy;
            ctx.orderId = orderId;
            return ctx;

        }
    }
}


