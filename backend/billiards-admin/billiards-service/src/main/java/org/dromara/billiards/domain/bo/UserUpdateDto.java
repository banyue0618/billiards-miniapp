package org.dromara.billiards.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
// import jakarta.validation.constraints.Size; // Example, if needed

import java.io.Serializable;

@Data
@Schema(description = "小程序用户更新信息DTO")
public class UserUpdateDto implements Serializable {

    private static final long serialVersionUID = 1L;

    // User ID will be taken from StpUtil in service/controller, not from DTO

    @Schema(description = "用户昵称")
    // @Size(max = 50, message = "昵称长度不能超过50个字符")
    private String nickname;

    @Schema(description = "用户头像URL")
    // @Size(max = 255, message = "头像URL长度不能超过255个字符")
    private String avatarUrl;

    @Schema(description = "性别 0-未知 1-男 2-女")
    // Consider @Min(0) @Max(2) if strict validation is needed
    private Integer gender;
}
