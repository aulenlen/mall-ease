package com.mallease.pms.dto.cmd;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

/**
 * 更新商品分类命令
 *
 * @author: Aulen
 * @create: 2025-11-15
 */
@Data
@Schema(description = "更新商品分类命令")
public class UpdateProductCategoryCmd {

    @Schema(description = "上级分类ID")
    private Long parentId;

    @Schema(description = "分类名称")
    @Size(max = 64, message = "分类名称长度不能超过64个字符")
    private String name;

    @Schema(description = "分类级别：0->1级；1->2级")
    @Min(value = 0, message = "分类级别值必须为0或1")
    @Max(value = 1, message = "分类级别值必须为0或1")
    private Integer level;

    @Schema(description = "产品单位")
    @Size(max = 64, message = "产品单位长度不能超过64个字符")
    private String productUnit;

    @Schema(description = "是否显示在导航栏：0->不显示；1->显示")
    @Min(value = 0, message = "导航栏显示状态值必须为0或1")
    @Max(value = 1, message = "导航栏显示状态值必须为0或1")
    private Integer navStatus;

    @Schema(description = "显示状态：0->不显示；1->显示")
    @Min(value = 0, message = "显示状态值必须为0或1")
    @Max(value = 1, message = "显示状态值必须为0或1")
    private Integer showStatus;

    @Schema(description = "排序")
    @Min(value = 0, message = "排序值不能小于0")
    private Integer sort;

    @Schema(description = "图标")
    @Size(max = 255, message = "图标URL长度不能超过255个字符")
    private String icon;

    @Schema(description = "关键词")
    @Size(max = 255, message = "关键词长度不能超过255个字符")
    private String keywords;

    @Schema(description = "描述")
    @Size(max = 255, message = "描述长度不能超过255个字符")
    private String description;

    @Schema(description = "产品属性ID集合")
    private List<Long> productAttributeIdList;
}
