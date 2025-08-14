package org.dromara.billiards.common.constant;

public enum OrderChannelEnum {

    // （APP/小程序/公众号/WEB/H5）
    APP("app"),
    MINI_PROGRAM("xcx"),
    PUBLIC_ACCOUNT("gzh"),
    WEB("web"),
    H5("h5");
    private final String channelName;
    OrderChannelEnum(String channelName) {
        this.channelName = channelName;
    }
    public String getChannelName() {
        return channelName;
    }
    public static OrderChannelEnum fromString(String channelName) {
        for (OrderChannelEnum channel : OrderChannelEnum.values()) {
            if (channel.channelName.equalsIgnoreCase(channelName)) {
                return channel;
            }
        }
        throw new IllegalArgumentException("Unknown order channel: " + channelName);
    }
}
