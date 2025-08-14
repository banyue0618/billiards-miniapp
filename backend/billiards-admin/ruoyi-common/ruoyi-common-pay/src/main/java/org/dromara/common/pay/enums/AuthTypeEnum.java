package org.dromara.common.pay.enums;

public enum AuthTypeEnum {

    /**
     * 国密
     */
    SM("WECHATPAY2-SM2-WITH-SM3", "国密算法"),
    /**
     * RSA
     */
    RSA("WECHATPAY2-SHA256-RSA2048", "RSA算法"),
    ;

    private final String url;

    private final String desc;

    AuthTypeEnum(String url, String desc) {
        this.url = url;
        this.desc = desc;
    }

    /**
     * 获取枚举URL
     *
     * @return 枚举编码
     */
    public String getUrl() {
        return url;
    }

    /**
     * 获取详细的描述信息
     *
     * @return 描述信息
     */
    public String getDesc() {
        return desc;
    }

}
