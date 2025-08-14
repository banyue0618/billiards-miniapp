package org.dromara.common.pay.enums;

public enum OtherApiEnum implements WxApiEnum{

    /**
     * 图片上传
     */
    MERCHANT_UPLOAD_MEDIA("/v3/merchant/media/upload", "图片上传"),

    /**
     * 视频上传
     */
    MERCHANT_UPLOAD_VIDEO("/v3/merchant/media/video_upload", "视频上传"),

    /**
     * 图片上传(营销专用)
     */
    MARKETING_UPLOAD_MEDIA("/v3/marketing/favor/media/image-upload", "图片上传(营销专用)"),

    /**
     * 获取平台证书列表
     */
    GET_CERTIFICATES("/v3/certificates", "获取平台证书列表"),

    ;

    /**
     * 接口URL
     */
    private final String url;

    /**
     * 接口描述
     */
    private final String desc;

    OtherApiEnum(String url, String desc) {
        this.url = url;
        this.desc = desc;
    }

    /**
     * 获取枚举URL
     *
     * @return 枚举编码
     */
    @Override
    public String getUrl() {
        return url;
    }

    /**
     * 获取详细的描述信息
     *
     * @return 描述信息
     */
    @Override
    public String getDesc() {
        return desc;
    }

    @Override
    public String toString() {
        return url;
    }

}
