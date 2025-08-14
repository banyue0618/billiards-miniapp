package org.dromara.common.pay.enums;

public enum SignType {
    /**
     * MD5签名
     */
    MD5("MD5"),

    /**
     * HMAC-SHA256签名
     */
    HMAC_SHA256("HMAC-SHA256"),

    /**
     * RSA签名
     */
    RSA("RSA"),

    /**
     * RSA2签名
     */
    RSA2("RSA2");

    private final String type;

    SignType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return type;
    }
}
