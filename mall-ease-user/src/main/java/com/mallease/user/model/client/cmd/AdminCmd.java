package com.mallease.user.model.client.cmd;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 管理员操作命令（注册/更新）
 *
 * @author: Aulen
 * @create: 2026-01-25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "管理员操作命令")
public class AdminCmd {

    /**
     * 创建时的校验组
     */
    public interface Create {
    }

    /**
     * 更新时的校验组
     */
    public interface Update {
    }

    @Schema(description = "用户ID（更新时必填）")
    @NotNull(groups = Update.class, message = "用户ID不能为空")
    private Long id;

    @Schema(description = "用户名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(groups = Create.class, message = "用户名不能为空")
    @Size(min = 4, max = 30, message = "用户名长度必须在4到30个字符之间")
    private String username;

    @Schema(description = "密码（创建时必填，更新时若为空则不修改）")
    @NotBlank(groups = Create.class, message = "密码不能为空")
    @Size(min = 6, max = 32, message = "密码长度必须在6到32个字符之间")
    private String password;

    @Schema(description = "头像")
    private String icon;

    @Schema(description = "邮箱")
    @Email(message = "邮箱格式不正确")
    private String email;

    @Schema(description = "昵称")
    private String nickName;

    @Schema(description = "备注")
    private String note;

    @Schema(description = "状态：0->禁用；1->启用")
    @Min(value = 0, message = "状态值必须为0或1")
    @Max(value = 1, message = "状态值必须为0或1")
    private Integer status;
}
