package org.dromara.common.pay.enums;

public enum WxDomainEnum implements WxDomain{

    /**
     * 中国国内
     */
    CHINA("https://api.mch.weixin.qq.com"),
    /**
     * 中国国内(备用域名)
     */
    CHINA2("https://api2.mch.weixin.qq.com"),
    /**
     * 东南亚
     */
    HK("https://apihk.mch.weixin.qq.com"),
    /**
     * 其它
     */
    US("https://apius.mch.weixin.qq.com"),
    /**
     * 获取公钥
     */
    FRAUD("https://fraud.mch.weixin.qq.com"),
    /**
     * 活动
     */
    ACTION("https://action.weixin.qq.com"),
    /**
     * 刷脸支付
     * PAY_APP
     */
    PAY_APP("https://payapp.weixin.qq.com");


    /**
     * 域名
     */
    private final String domain;

    WxDomainEnum(String domain) {
        this.domain = domain;
    }

    @Override
    public String getDomain() {
        return domain;
    }

    @Override
    public String toString() {
        return domain;
    }

}
