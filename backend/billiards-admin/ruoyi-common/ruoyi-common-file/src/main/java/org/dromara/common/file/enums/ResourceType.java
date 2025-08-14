package org.dromara.common.file.enums;

import lombok.Getter;

/**
 * 资源类型枚举
 * 定义不同业务场景下的资源及其基础存储路径片段。
 */
@Getter
public enum ResourceType {

    // 示例类型，您可以根据您的业务需求进行修改或添加
    STORE_COVER("stores/covers", "门店封面图"),
    STORE_GALLERY("stores/gallery", "门店环境图"),
    TABLE_IMAGE("tables", "桌台图片"),
    QRCODE("qrcodes", "二维码图片"),
    USER_AVATAR("users/avatars", "用户头像"),
    PRODUCT_IMAGE("products", "商品图片"),
    ARTICLE_ATTACHMENT("articles/attachments", "文章附件"),
    GENERAL_FILE("general", "通用文件");

    /**
     * 相对路径片段，用于在此类型资源的基础存储路径下进一步组织文件。
     */
    private final String path;
    /**
     * 资源类型的描述，方便理解和管理。
     */
    private final String description;

    ResourceType(String path, String description) {
        this.path = path;
        this.description = description;
    }

} 