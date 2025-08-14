package org.dromara.common.pay.model;

import cn.hutool.core.util.StrUtil;
import org.dromara.common.pay.config.WxPayKit;
import org.dromara.common.pay.enums.SignType;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description
 * @Author zhangsip
 * @Version V1.0.0
 * @Since 1.0
 * @Date 2025/6/2
 */
public class BaseModel {

    /**
     * 将建构的 builder 转为 Map
     *
     * @return 转化后的 Map
     */
    public Map<String, String> toMap() {
        String[] fieldNames = getFiledNames(this);
        HashMap<String, String> map = new HashMap<String, String>(fieldNames.length);
        for (String name : fieldNames) {
            String value = (String) getFieldValueByName(name, this);
            if (StrUtil.isNotEmpty(value)) {
                map.put(name, value);
            }
        }
        return map;
    }

    /**
     * 构建签名 Map
     *
     * @param partnerKey API KEY
     * @param signType   {@link SignType} 签名类型
     * @return 构建签名后的 Map
     */
    public Map<String, String> createSign(String partnerKey, SignType signType) {
        return createSign(partnerKey, signType, true);
    }

    /**
     * 构建签名 Map
     *
     * @param partnerKey   API KEY
     * @param signType     {@link SignType} 签名类型
     * @param haveSignType 签名是否包含 sign_type 字段
     * @return 构建签名后的 Map
     */
    public Map<String, String> createSign(String partnerKey, SignType signType, boolean haveSignType) {
        return WxPayKit.buildSign(toMap(), partnerKey, signType, haveSignType);
    }

    /**
     * 构建签名 Map
     *
     * @param partnerKey   API KEY
     * @param signType     {@link SignType} 签名类型
     * @param signKey      签名字符串
     * @param signTypeKey  签名类型字符串
     * @param haveSignType 签名是否包含签名类型字符串
     * @return 签名后的 Map
     */
    public Map<String, String> createSign(String partnerKey, SignType signType, String signKey, String signTypeKey, boolean haveSignType) {
        return WxPayKit.buildSign(toMap(), partnerKey, signType, signKey, signTypeKey, haveSignType);
    }

    /**
     * 构建签名 Map
     *
     * @param partnerKey API KEY
     * @param signType   {@link SignType} 签名类型
     * @param signKey    签名字符串
     * @return 签名后的 Map
     */
    public Map<String, String> createSign(String partnerKey, SignType signType, String signKey) {
        return WxPayKit.buildSign(toMap(), partnerKey, signType, signKey, null, false);
    }

    /**
     * 获取属性名数组
     *
     * @param obj 对象
     * @return 返回对象属性名数组
     */
    public String[] getFiledNames(Object obj) {
        Field[] fields = obj.getClass().getDeclaredFields();
        String[] fieldNames = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            fieldNames[i] = fields[i].getName();
        }
        return fieldNames;
    }

    /**
     * 根据属性名获取属性值
     *
     * @param fieldName 属性名称
     * @param obj       对象
     * @return 返回对应属性的值
     */
    public Object getFieldValueByName(String fieldName, Object obj) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = new StringBuffer().append("get")
                .append(firstLetter)
                .append(fieldName.substring(1))
                .toString();
            Method method = obj.getClass().getMethod(getter);
            return method.invoke(obj);
        } catch (Exception e) {
            return null;
        }
    }

}
