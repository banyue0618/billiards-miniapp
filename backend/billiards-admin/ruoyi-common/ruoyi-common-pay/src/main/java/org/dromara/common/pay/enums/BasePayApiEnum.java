package org.dromara.common.pay.enums;

public enum BasePayApiEnum implements WxApiEnum{
    /**
     * JSAPI下单
     */
    JS_API_PAY("/v3/pay/transactions/jsapi", "JSAPI 下单"),

    /**
     * 服务商模式-JSAPI下单
     */
    PARTNER_JS_API_PAY("/v3/pay/partner/transactions/jsapi", "服务商模式-JSAPI下单"),

    /**
     * APP 下单
     */
    APP_PAY("/v3/pay/transactions/app", "APP 下单"),

    /**
     * 服务商模式-APP 下单
     */
    PARTNER_APP_PAY("/v3/pay/partner/transactions/app", "服务商模式-APP 下单"),

    /**
     * H5 下单
     */
    H5_PAY("/v3/pay/transactions/h5", "H5 下单"),

    /**
     * 服务商模式-H5 下单
     */
    PARTNER_H5_PAY("/v3/pay/partner/transactions/h5", "服务商模式-H5 下单"),

    /**
     * Native 下单
     */
    NATIVE_PAY("/v3/pay/transactions/native", "Native 下单"),

    /**
     * 服务商模式-Native 下单
     */
    PARTNER_NATIVE_PAY("/v3/pay/partner/transactions/native", "服务商模式-Native 下单"),

    /**
     * 合单 APP 下单
     */
    COMBINE_TRANSACTIONS_APP("/v3/combine-transactions/app", "合单 APP 下单"),

    /**
     * 合单 JSAPI 下单
     */
    COMBINE_TRANSACTIONS_JS("/v3/combine-transactions/jsapi", "合单 JSAPI 下单"),

    /**
     * 合单 H5 下单
     */
    COMBINE_TRANSACTIONS_H5("/v3/combine-transactions/h5", "合单 H5 下单"),

    /**
     * 合单 Native下单
     */
    COMBINE_TRANSACTIONS_NATIVE("/v3/combine-transactions/native", "合单 Native 下单"),

    /**
     * 合单查询订单
     */
    COMBINE_QUERY_BY_OUT_TRADE_NO("/v3/combine-transactions/out-trade-no/%s", "合单查询订单"),

    /**
     * 合单关闭订单
     */
    COMBINE_CLOSE_BY_OUT_TRADE_NO("/v3/combine-transactions/out-trade-no/%s/close", "合单关闭订单"),

    /**
     * 合单支付-申请退款
     */
    DOMESTIC_REFUND("/v3/refund/domestic/refunds", "合单支付-申请退款"),

    /**
     * 合单支付-查询单笔退款
     */
    DOMESTIC_REFUND_QUERY("/v3/refund/domestic/refunds/%s", "合单支付-查询单笔退款"),

    /**
     * 微信支付订单号查询
     */
    ORDER_QUERY_BY_TRANSACTION_ID("/v3/pay/transactions/id/%s", "微信支付订单号查询"),

    /**
     * 服务商模式-微信支付订单号查询
     */
    PARTNER_ORDER_QUERY_BY_TRANSACTION_ID("/v3/pay/partner/transactions/id/%s", "服务商模式-微信支付订单号查询"),

    /**
     * 商户订单号查询
     */
    ORDER_QUERY_BY_OUT_TRADE_NO("/v3/pay/transactions/out-trade-no/%s", "商户订单号查询"),

    /**
     * 服务商模式-商户订单号查询
     */
    PARTNER_ORDER_QUERY_BY_OUT_TRADE_NO("/v3/pay/partner/transactions/out-trade-no/%s", "服务商模式-商户订单号查询"),

    /**
     * 关闭订单
     */
    CLOSE_ORDER_BY_OUT_TRADE_NO("/v3/pay/transactions/out-trade-no/%s/close", "关闭订单"),

    /**
     * 服务商模式-关闭订单
     */
    PARTNER_CLOSE_ORDER_BY_OUT_TRADE_NO("/v3/pay/partner/transactions/out-trade-no/%s/close", "服务商模式-关闭订单"),

    /**
     * 申请退款
     */
    REFUND("/v3/refund/domestic/refunds", "申请退款"),

    /**
     * 查询单笔退款
     */
    REFUND_QUERY_BY_OUT_REFUND_NO("/v3/refund/domestic/refunds/%s", "查询单笔退款"),


    /**
     * 申请交易账单
     */
    TRADE_BILL("/v3/bill/tradebill", "申请交易账单"),

    /**
     * 申请资金账单
     */
    FUND_FLOW_BILL("/v3/bill/fundflowbill", "申请资金账单"),

    /**
     * 申请单个子商户资金账单
     */
    SUB_MERCHANT_FUND_FLOW_BILL("/v3/bill/sub-merchant-fundflowbill", "申请单个子商户资金账单"),

    /**
     * 下载账单
     */
    BILL_DOWNLOAD("/v3/billdownload/file", "下载账单"),
    ;

    /**
     * 接口URL
     */
    private final String url;

    /**
     * 接口描述
     */
    private final String desc;

    BasePayApiEnum(String url, String desc) {
        this.url = url;
        this.desc = desc;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getDesc() {
        return desc;
    }

    @Override
    public String toString() {
        return url;
    }
}
