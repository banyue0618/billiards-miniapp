package org.dromara.billiards.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dromara.billiards.service.QrCodeTokenService;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

/**
 * 基于HMAC-SHA256签名的二维码短码实现。
 * 采用无状态校验：内容里携带签名，后端使用共享密钥验证。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QrCodeTokenServiceImpl implements QrCodeTokenService {

    // 注意：实际项目中建议改为从配置中心或环境变量注入，不要硬编码
    private static final String HMAC_SECRET = "billiards.qr.hmac.secret.change.me";

    private static final String PREFIX = "t_"; // token前缀，便于快速识别

    @Override
    public String generateContentForTable(String tableId) {
        if (StringUtils.isBlank(tableId)) {
            return null;
        }
        String signature = sign(tableId);
        // 缩短签名长度：取前12位hex即可，足以防止伪造
        String shortSig = signature.substring(0, 12);
        return PREFIX + tableId + "_" + shortSig;
    }

    @Override
    public String parseTableIdFromContent(String content) {
        if (StringUtils.isBlank(content)) {
            return null;
        }
        // 兼容旧格式：直接为tableId或以"table_"为前缀
        if (content.startsWith("table_")) {
            return content.substring("table_".length());
        }
        // 兼容直接ID（32位hex）
        if (isHex32(content)) {
            return content;
        }
        // 新格式：t_<id>_<sig>
        if (!content.startsWith(PREFIX)) {
            return null;
        }
        String rest = content.substring(PREFIX.length());
        int idx = rest.lastIndexOf('_');
        if (idx <= 0 || idx == rest.length() - 1) {
            return null;
        }
        String tableId = rest.substring(0, idx);
        String sig = rest.substring(idx + 1);
        if (!isHex32(tableId) || sig.length() != 12) {
            return null;
        }
        String expect = sign(tableId).substring(0, 12);
        if (!StringUtils.equalsIgnoreCase(expect, sig)) {
            log.warn("二维码短码签名校验失败: content={}", content);
            return null;
        }
        return tableId;
    }

    private static boolean isHex32(String s) {
        if (s == null || s.length() != 32) return false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            boolean isHex = (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F');
            if (!isHex) return false;
        }
        return true;
    }

    private static String sign(String tableId) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(HMAC_SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(keySpec);
            byte[] bytes = mac.doFinal(tableId.getBytes(StandardCharsets.UTF_8));
            return toHex(bytes);
        } catch (Exception e) {
            // 理论上不会失败
            return "";
        }
    }

    private static String toHex(byte[] data) {
        StringBuilder sb = new StringBuilder(data.length * 2);
        for (byte b : data) {
            String s = Integer.toHexString(b & 0xff);
            if (s.length() == 1) sb.append('0');
            sb.append(s);
        }
        return sb.toString();
    }
}


