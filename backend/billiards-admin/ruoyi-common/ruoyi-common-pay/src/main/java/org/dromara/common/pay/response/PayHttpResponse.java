package org.dromara.common.pay.response;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.CaseInsensitiveMap;
import cn.hutool.core.util.StrUtil;

import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author zhangsip
 * @Version V1.0.0
 * @Since 1.0
 * @Date 2025/6/2
 */
public class PayHttpResponse {

    private String body;
    private byte[] bodyByte;
    private int status;
    private Map<String, List<String>> headers;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public byte[] getBodyByte() {
        return bodyByte;
    }

    public void setBodyByte(byte[] bodyByte) {
        this.bodyByte = bodyByte;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }

    public String getHeader(String name) {
        List<String> values = this.headerList(name);
        return CollectionUtil.isEmpty(values) ? null : values.get(0);
    }

    private List<String> headerList(String name) {
        if (StrUtil.isBlank(name)) {
            return null;
        } else {
            CaseInsensitiveMap<String, List<String>> headersIgnoreCase = new CaseInsensitiveMap<>(getHeaders());
            return headersIgnoreCase.get(name.trim());
        }
    }

    @Override
    public String toString() {
        return "PayHttpResponse{" +
            "body='" + body + '\'' +
            ", status=" + status +
            ", headers=" + headers +
            '}';
    }
}
