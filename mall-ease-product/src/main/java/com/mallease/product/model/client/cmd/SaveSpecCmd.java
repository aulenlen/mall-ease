package com.mallease.product.model.client.cmd;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 保存规格定义命令（创建/更新统一）
 *
 * @author: Aulen
 * @create: 2025-12-13
 */
@Schema(description = "保存规格定义命令")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaveSpecCmd {

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

    @Schema(description = "规格ID（创建时不传，更新时必传）")
    @NotNull(groups = Update.class, message = "更新时规格ID不能为空")
    private Long id;

    @Schema(description = "所属规格组ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(groups = Create.class, message = "创建时规格组ID不能为空")
    private Long groupId;

    @Schema(description = "规格名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(groups = Create.class, message = "创建时规格名称不能为空")
    @Size(max = 64, message = "规格名称长度不能超过64个字符")
    private String name;

    @Schema(description = "展示类型: 0-文字, 1-颜色块, 2-图片")
    @Min(value = 0, message = "展示类型值必须为0、1或2")
    @Max(value = 2, message = "展示类型值必须为0、1或2")
    @Builder.Default
    private Integer displayType = 0;

    @Schema(description = "是否必选: 0-否, 1-是")
    @Min(value = 0, message = "是否必选值必须为0或1")
    @Max(value = 1, message = "是否必选值必须为0或1")
    @Builder.Default
    private Integer isRequired = 1;

    @Schema(description = "是否可搜索: 0-否, 1-是")
    @Min(value = 0, message = "是否可搜索值必须为0或1")
    @Max(value = 1, message = "是否可搜索值必须为0或1")
    @Builder.Default
    private Integer isSearchable = 0;

    @Schema(description = "是否可筛选: 0-否, 1-是")
    @Min(value = 0, message = "是否可筛选值必须为0或1")
    @Max(value = 1, message = "是否可筛选值必须为0或1")
    @Builder.Default
    private Integer isFilterable = 1;

    @Schema(description = "排序值，越小越靠前")
    @Min(value = 0, message = "排序值不能小于0")
    @Builder.Default
    private Integer sort = 0;

    @Schema(description = "规格值列表（可选，创建/更新规格时同时操作规格值）")
    @Valid
    private List<SpecValueCmd> valueList;

    /**
     * 规格值命令（内部类）
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "规格值")
    public static class SpecValueCmd {

        @Schema(description = "规格值ID（有ID表示更新，无ID表示新增）")
        private Long id;

        @Schema(description = "规格值", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "规格值不能为空")
        @Size(max = 64, message = "规格值长度不能超过64个字符")
        private String value;

        @Schema(description = "图片URL（颜色/图片类型规格可用）")
        @Size(max = 255, message = "图片URL长度不能超过255个字符")
        private String image;

        @Schema(description = "颜色代码（颜色类型规格可用），如#000000")
        @Size(max = 16, message = "颜色代码长度不能超过16个字符")
        private String colorCode;

        @Schema(description = "排序值")
        @Min(value = 0, message = "排序值不能小于0")
        @Builder.Default
        private Integer sort = 0;
    }
}
