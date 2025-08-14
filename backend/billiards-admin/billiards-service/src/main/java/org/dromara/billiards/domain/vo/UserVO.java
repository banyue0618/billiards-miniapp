package org.dromara.billiards.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "用户信息视图对象 (小程序端)")
public class UserVO {

    @Schema(description = "用户ID")
    private String id;

    // openid and unionid are typically not exposed to the client
    // private String openid;
    // private String unionid;

    @Schema(description = "用户昵称")
    private String nickname;

    @Schema(description = "用户头像URL")
    private String avatarUrl;

    @Schema(description = "性别 0-未知 1-男 2-女")
    private Integer gender;

    @Schema(description = "手机号码")
    private String phone;

    @Schema(description = "是否是会员 0-否 1-是")
    private Integer isMember;

    @Schema(description = "会员等级")
    private Integer memberLevel;

    @Schema(description = "会员过期时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime memberExpire;

    @Schema(description = "积分")
    private Integer points;

    @Schema(description = "余额")
    private BigDecimal balance;

    @Schema(description = "最后登录时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLoginTime;

    @Schema(description = "状态 0-正常 1-禁用")
    private Integer status; // Usually not exposed directly, or only for admin

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
