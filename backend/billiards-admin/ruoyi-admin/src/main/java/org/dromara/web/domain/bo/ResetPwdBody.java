package org.dromara.web.domain.bo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 重置密码对象
 */
@Data
@NoArgsConstructor
public class ResetPwdBody {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 邮箱
     */
    @NotBlank(message = "邮箱不能为空")
    private String email;

    /**
     * 邮箱验证码
     */
    @NotBlank(message = "邮箱验证码不能为空")
    private String emailCode;

    /**
     * 新密码
     */
    @NotBlank(message = "新密码不能为空")
    private String password;

    /**
     * 验证码
     */
    @NotBlank(message = "验证码不能为空")
    private String code;

    /**
     * 验证码标识
     */
    @NotBlank(message = "验证码标识不能为空")
    private String uuid;
}
