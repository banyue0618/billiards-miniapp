package org.dromara.billiards.iot.executor;

import java.util.Map;

public interface IoTCommandExecutor {

    /**
     * 是否支持该协议
     * @param protocol
     * @return
     */
    boolean supports(String protocol);

    /**
     * 执行命令
     * @param context
     * @return
     * @throws Exception
     */
    ExecutionResult execute(ExecutionContext context) throws Exception;

    /**
     * 记录执行结果
     * @param ctx
     * @param result
     */
    void record(ExecutionContext ctx, ExecutionResult result);

    /**
     * 必要时告警
     * @param ctx
     * @param result
     */
    void alertIfNeeded(ExecutionContext ctx, ExecutionResult result);


    class ExecutionContext {
        public String topic;
        public String deviceCode;
        public String protocol; // mqtt/http
        public String command;  // turn_on/turn_off/unlock/lock/play_audio
        public String paramsJson; // raw json string
        public Map<String, Object> protocolConfig; // parsed from device.protocol_config
        public String triggerBy; // order/admin/system
        public String triggerScene; // open_table/close_table
        public String tableId;
        public String orderId;
        public String traceId;
    }

    class ExecutionResult {
        public boolean success;
        public String errorMsg;
        public long responseMs;
        public int retryCount;
        public static ExecutionResult ok(long ms){
            ExecutionResult r = new ExecutionResult();
            r.success = true; r.responseMs = ms; return r;
        }
        public static ExecutionResult fail(String msg){
            ExecutionResult r = new ExecutionResult();
            r.success = false; r.errorMsg = msg; return r;
        }
    }
}


