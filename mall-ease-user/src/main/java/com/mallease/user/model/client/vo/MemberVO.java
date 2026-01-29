package com.mallease.user.model.client.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 会员视图对象
 *
 * @author: Aulen
 * @create: 2026-01-29
 */
@Schema(description = "会员视图对象")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberVO {

    @Schema(description = "会员ID")
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "手机号码")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "头像")
    private String icon;

    @Schema(description = "性别：0-未知 1-男 2-女")
    private Integer gender;

    @Schema(description = "生日")
    private LocalDate birthday;

    @Schema(description = "所在城市")
    private String city;

    @Schema(description = "职业")
    private String job;

    @Schema(description = "个性签名")
    private String personalizedSignature;

    @Schema(description = "积分")
    private Integer integration;

    @Schema(description = "成长值")
    private Integer growth;

    @Schema(description = "会员等级ID")
    private Long memberLevelId;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
