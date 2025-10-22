package org.dromara.billiards.common.constant;

public enum TriggerSceneEnum {
    OPEN_TABLE("open_table", "开台"),
    CLOSE_TABLE("close_table", "关台"),
    TIMEOUT_WARNING("timeout_warning", "超时预警");

    private final String code;

    private final String desc;

    TriggerSceneEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static TriggerSceneEnum getByCode(String code) {
        for (TriggerSceneEnum scene : TriggerSceneEnum.values()) {
            if (scene.getCode().equals(code)) {
                return scene;
            }
        }
        return null;
    }

}
