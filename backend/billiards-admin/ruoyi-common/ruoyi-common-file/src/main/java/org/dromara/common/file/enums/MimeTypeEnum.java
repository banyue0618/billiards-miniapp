package org.dromara.common.file.enums;

import org.dromara.common.core.utils.StringUtils;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件MIME类型枚举
 * 用于根据文件后缀判断文件的MIME类型
 */
public class MimeTypeEnum {

    private static final Map<String, String> MIME_TYPE_MAP = new HashMap<>();

    static {
        // 图片类型
        MIME_TYPE_MAP.put("jpg", "image/jpeg");
        MIME_TYPE_MAP.put("jpeg", "image/jpeg");
        MIME_TYPE_MAP.put("png", "image/png");
        MIME_TYPE_MAP.put("gif", "image/gif");
        MIME_TYPE_MAP.put("bmp", "image/bmp");
        MIME_TYPE_MAP.put("webp", "image/webp");
        MIME_TYPE_MAP.put("svg", "image/svg+xml");
        MIME_TYPE_MAP.put("ico", "image/x-icon");
        
        // 文档类型
        MIME_TYPE_MAP.put("pdf", "application/pdf");
        MIME_TYPE_MAP.put("doc", "application/msword");
        MIME_TYPE_MAP.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        MIME_TYPE_MAP.put("xls", "application/vnd.ms-excel");
        MIME_TYPE_MAP.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        MIME_TYPE_MAP.put("ppt", "application/vnd.ms-powerpoint");
        MIME_TYPE_MAP.put("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
        
        // 文本类型
        MIME_TYPE_MAP.put("txt", "text/plain");
        MIME_TYPE_MAP.put("csv", "text/csv");
        MIME_TYPE_MAP.put("html", "text/html");
        MIME_TYPE_MAP.put("htm", "text/html");
        MIME_TYPE_MAP.put("xml", "application/xml");
        MIME_TYPE_MAP.put("json", "application/json");
        MIME_TYPE_MAP.put("md", "text/markdown");
        
        // 压缩文件
        MIME_TYPE_MAP.put("zip", "application/zip");
        MIME_TYPE_MAP.put("rar", "application/x-rar-compressed");
        MIME_TYPE_MAP.put("7z", "application/x-7z-compressed");
        MIME_TYPE_MAP.put("tar", "application/x-tar");
        MIME_TYPE_MAP.put("gz", "application/gzip");
        
        // 音频文件
        MIME_TYPE_MAP.put("mp3", "audio/mpeg");
        MIME_TYPE_MAP.put("wav", "audio/wav");
        MIME_TYPE_MAP.put("ogg", "audio/ogg");
        MIME_TYPE_MAP.put("m4a", "audio/mp4");
        
        // 视频文件
        MIME_TYPE_MAP.put("mp4", "video/mp4");
        MIME_TYPE_MAP.put("avi", "video/x-msvideo");
        MIME_TYPE_MAP.put("mov", "video/quicktime");
        MIME_TYPE_MAP.put("wmv", "video/x-ms-wmv");
        MIME_TYPE_MAP.put("flv", "video/x-flv");
        MIME_TYPE_MAP.put("webm", "video/webm");
    }

    /**
     * 根据文件后缀获取MIME类型
     *
     * @param extension 文件后缀，不包含点号
     * @return MIME类型字符串，如果未知则返回通用二进制流类型
     */
    public static String getMimeType(String extension) {
        if (StringUtils.isEmpty(extension)) {
            return "application/octet-stream";
        }
        
        String lowerExt = extension.toLowerCase();
        return MIME_TYPE_MAP.getOrDefault(lowerExt, "application/octet-stream");
    }
    
    /**
     * 根据文件名获取MIME类型
     *
     * @param filename 完整文件名
     * @return MIME类型字符串，如果未知则返回通用二进制流类型
     */
    public static String getMimeTypeByFilename(String filename) {
        if (StringUtils.isEmpty(filename)) {
            return "application/octet-stream";
        }
        
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return "application/octet-stream";
        }
        
        String extension = filename.substring(lastDotIndex + 1);
        return getMimeType(extension);
    }
} 