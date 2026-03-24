package com.mallease.product.controller.admin.attribute.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 属性保存请求
 */
@Data
@Schema(description = "属性保存请求")
public class AttributeSaveReqVO {

    public interface Create {}
    public interface Update {}

    @NotNull(groups = Update.class, message = "ID不能为空")
    @Schema(description = "属性ID")
    private Long id;

    @NotBlank(groups = Create.class, message = "属性名称不能为空")
    @Size(max = 64, message = "属性名称不能超过64个字符")
    @Schema(description = "属性名称")
    private String name;

    @NotNull(groups = Create.class, message = "属性类型不能为空")
    @Min(value = 0, message = "属性类型无效")
    @Max(value = 1, message = "属性类型无效")
    @Schema(description = "属性类型：0-参数 1-规格")
    private Integer type;

    @Size(max = 32, message = "单位不能超过32个字符")
    @Schema(description = "单位")
    private String unit;

    @Min(value = 0, message = "录入方式无效")
    @Max(value = 1, message = "录入方式无效")
    @Schema(description = "录入方式：0-手工录入 1-预设选项")
    private Integer entryMethod = 0;

    @Schema(description = "全局预设选项列表")
    private List<String> options;

    @Min(value = 0, message = "可搜索标识无效")
    @Max(value = 1, message = "可搜索标识无效")
    @Schema(description = "是否可搜索：0-否 1-是")
    private Integer searchable = 0;

    @Min(value = 0, message = "可筛选标识无效")
    @Max(value = 1, message = "可筛选标识无效")
    @Schema(description = "是否可筛选：0-否 1-是")
    private Integer filterable = 0;
}