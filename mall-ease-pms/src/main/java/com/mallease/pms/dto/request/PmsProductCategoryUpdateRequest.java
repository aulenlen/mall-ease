package com.mallease.pms.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 商品分类更新请求参数
 *
 * @author: Aulen
 * @create: 2025-11-12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PmsProductCategoryUpdateRequest {
    /**
     * 父分类ID：0表示一级分类
     */
    private Long parentId;

    /**
     * 分类名称
     */
    @NotBlank(message = "分类名称不能为空")
    @Size(max = 64, message = "分类名称长度不能超过64个字符")
    private String name;

    /**
     * 分类单位
     */
    @Size(max = 32, message = "分类单位长度不能超过32个字符")
    private String productUnit;

    /**
     * 是否显示在导航栏：0->不显示；1->显示
     */
    @Min(value = 0, message = "导航栏显示状态值只能是0或1")
    @Max(value = 1, message = "导航栏显示状态值只能是0或1")
    private Integer navStatus;

    /**
     * 显示状态：0->不显示；1->显示
     */
    @Min(value = 0, message = "显示状态值只能是0或1")
    @Max(value = 1, message = "显示状态值只能是0或1")
    private Integer showStatus;

    /**
     * 排序
     */
    @Min(value = 0, message = "排序值不能小于0")
    private Integer sort;

    /**
     * 图标
     */
    @Size(max = 255, message = "图标路径长度不能超过255个字符")
    private String icon;

    /**
     * 关键词
     */
    @Size(max = 255, message = "关键词长度不能超过255个字符")
    private String keywords;

    /**
     * 描述
     */
    @Size(max = 1000, message = "描述长度不能超过1000个字符")
    private String description;

    /**
     * 产品属性ID集合
     */
    private List<Long> productAttributeIdList;
}






