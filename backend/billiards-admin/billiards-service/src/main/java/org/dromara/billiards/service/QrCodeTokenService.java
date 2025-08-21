package org.dromara.billiards.service;

/**
 * 二维码内容短链/令牌服务
 * 负责生成可嵌入二维码的安全内容，以及从内容中解析出桌台ID。
 */
public interface QrCodeTokenService {

    /**
     * 为桌台生成二维码内容（包含签名的短码）。
     * 形如：t_<32位hex的tableId>_<12位hex签名>
     *
     * @param tableId 桌台ID（32位hex）
     * @return 可写入二维码的内容字符串
     */
    String generateContentForTable(String tableId);

    /**
     * 从二维码内容中解析桌台ID。
     * 支持以下格式：
     * 1) 直接为32位hex的桌台ID（兼容旧逻辑）
     * 2) t_<32位hex的tableId>_<12位hex签名>
     *
     * @param content 二维码内容
     * @return 合法则返回桌台ID，否则返回null
     */
    String parseTableIdFromContent(String content);
}


