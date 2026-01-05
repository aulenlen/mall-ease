package com.mallease.product.model.client.cmd;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 保存参数组命令（创建/更新/克隆统一）
 *
 * @author: Aulen
 * @create: 2025-12-13
 */
@Schema(description = "保存参数组命令")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParamGroupCmd {

    public interface Create {
    }

    public interface Update {
    }

    public interface Clone {
    }

    @Schema(description = "参数组ID（更新时必传，克隆时表示源参数组ID）")
    @NotNull(groups = { Update.class, Clone.class }, message = "参数组ID不能为空")
    private Long id;

    @Schema(description = "参数组名称")
    @NotBlank(groups = Create.class, message = "创建时参数组名称不能为空")
    @Size(max = 64, message = "参数组名称长度不能超过64个字符")
    private String name;

    @Schema(description = "排序值，越小越靠前")
    @Min(value = 0, message = "排序值不能小于0")
    @Builder.Default
    private Integer sort = 0;

    @Schema(description = "状态: 0-禁用, 1-启用")
    @Min(value = 0, message = "状态值必须为0或1")
    @Max(value = 1, message = "状态值必须为0或1")
    @Builder.Default
    private Integer status = 1;

    @Schema(description = "关联的分类ID（创建/克隆时可传入，自动绑定到该分类）")
    @NotNull(groups = Clone.class, message = "克隆时分类ID不能为空")
    private Long categoryId;
}
