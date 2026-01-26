package com.mallease.user.model.client.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 管理员视图对象
 *
 * @author: Aulen
 * @create: 2026-01-25
 */
@Schema(description = "管理员视图对象")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminVO {

    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "头像")
    private String icon;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "昵称")
    private String nickName;

    @Schema(description = "备注")
    private String note;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "最后登录时间")
    private Date loginTime;

    @Schema(description = "帐号启用状态：0->禁用；1->启用")
    private Integer status;
}
