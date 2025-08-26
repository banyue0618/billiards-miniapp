package org.dromara.common.core.domain.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 三方登录对象
 *
 * @author Lion Li
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class XcxLoginBody extends LoginBody {

    /**
     * 小程序登录凭证
     */
    @NotBlank(message = "{xcx.code.not.blank}")
    private String xcxCode;

    /**
     * 小程序应用ID (用于支持多小程序)
     */
    private String appid;

    /**
     * 获取手机号的凭证(button open-type="getPhoneNumber" 事件回调中获取的code)
     */
    private String phoneCode;

    /**
     * 用户昵称 (可选, 用于首次登录或信息同步)
     */
    private String nickname;

    /**
     * 用户头像图片的 URL (可选)
     */
    private String avatarUrl; // 通常微信返回的是 avatarUrl

    /**
     * 用户性别 (可选, 0-未知, 1-男, 2-女)
     */
    private Integer gender;

}
