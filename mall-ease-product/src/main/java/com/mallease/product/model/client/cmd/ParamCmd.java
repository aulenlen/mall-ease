package com.mallease.product.model.client.cmd;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 保存参数定义命令（创建/更新统一）
 *
 * @author: Aulen
 * @create: 2025-12-13
 */
@Schema(description = "保存参数定义命令")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParamCmd {

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

    @Schema(description = "参数ID（创建时不传，更新时必传）")
    @NotNull(groups = Update.class, message = "更新时参数ID不能为空")
    private Long id;

    @Schema(description = "所属参数组ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(groups = Create.class, message = "创建时参数组ID不能为空")
    private Long groupId;

    @Schema(description = "参数名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(groups = Create.class, message = "创建时参数名称不能为空")
    @Size(max = 64, message = "参数名称长度不能超过64个字符")
    private String name;

    @Schema(description = "单位")
    @Size(max = 16, message = "单位长度不能超过16个字符")
    private String unit;

    @Schema(description = "录入方式: 0-手动输入, 1-从列表选择")
    @Min(value = 0, message = "录入方式值必须为0或1")
    @Max(value = 1, message = "录入方式值必须为0或1")
    @Builder.Default
    private Integer inputType = 0;

    @Schema(description = "可选值列表（逗号分隔），录入方式为1时使用")
    @Size(max = 500, message = "可选值列表长度不能超过500个字符")
    private String inputList;

    @Schema(description = "是否必填: 0-否, 1-是")
    @Min(value = 0, message = "是否必填值必须为0或1")
    @Max(value = 1, message = "是否必填值必须为0或1")
    @Builder.Default
    private Integer isRequired = 0;

    @Schema(description = "是否可搜索: 0-否, 1-是")
    @Min(value = 0, message = "是否可搜索值必须为0或1")
    @Max(value = 1, message = "是否可搜索值必须为0或1")
    @Builder.Default
    private Integer isSearchable = 0;

    @Schema(description = "是否亮点参数（商品列表展示）: 0-否, 1-是")
    @Min(value = 0, message = "是否亮点参数值必须为0或1")
    @Max(value = 1, message = "是否亮点参数值必须为0或1")
    @Builder.Default
    private Integer isHighlight = 0;

    @Schema(description = "是否可对比: 0-否, 1-是")
    @Min(value = 0, message = "是否可对比值必须为0或1")
    @Max(value = 1, message = "是否可对比值必须为0或1")
    @Builder.Default
    private Integer isComparable = 0;

    @Schema(description = "排序值，越小越靠前")
    @Min(value = 0, message = "排序值不能小于0")
    @Builder.Default
    private Integer sort = 0;
}
