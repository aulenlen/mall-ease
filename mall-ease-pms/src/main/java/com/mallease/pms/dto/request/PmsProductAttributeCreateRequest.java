package com.mallease.pms.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品属性创建请求参数
 *
 * @author: Aulen
 * @create: 2025-11-12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PmsProductAttributeCreateRequest {
    /**
     * 属性分类ID
     */
    @NotNull(message = "属性分类ID不能为空")
    private Long productAttributeCategoryId;

    /**
     * 属性名称
     */
    @jakarta.validation.constraints.NotBlank(message = "属性名称不能为空")
    @Size(max = 64, message = "属性名称长度不能超过64个字符")
    private String name;

    /**
     * 属性选择类型：0->唯一；1->单选；2->多选
     */
    @Min(value = 0, message = "属性选择类型值只能是0、1或2")
    @Max(value = 2, message = "属性选择类型值只能是0、1或2")
    private Integer selectType;

    /**
     * 属性录入方式：0->手工录入；1->从列表中选取
     */
    @Min(value = 0, message = "属性录入方式值只能是0或1")
    @Max(value = 1, message = "属性录入方式值只能是0或1")
    private Integer inputType;

    /**
     * 可选值列表，以逗号隔开
     */
    @Size(max = 255, message = "可选值列表长度不能超过255个字符")
    private String inputList;

    /**
     * 排序字段
     */
    @Min(value = 0, message = "排序值不能小于0")
    private Integer sort;

    /**
     * 分类筛选样式：0->普通；1->颜色
     */
    @Min(value = 0, message = "分类筛选样式值只能是0或1")
    @Max(value = 1, message = "分类筛选样式值只能是0或1")
    private Integer filterType;

    /**
     * 检索类型；0->不需要进行检索；1->关键字检索；2->范围检索
     */
    @Min(value = 0, message = "检索类型值只能是0、1或2")
    @Max(value = 2, message = "检索类型值只能是0、1或2")
    private Integer searchType;

    /**
     * 相同属性产品是否关联；0->不关联；1->关联
     */
    @Min(value = 0, message = "关联状态值只能是0或1")
    @Max(value = 1, message = "关联状态值只能是0或1")
    private Integer relatedStatus;

    /**
     * 是否支持手动新增；0->不支持；1->支持
     */
    @Min(value = 0, message = "手动新增状态值只能是0或1")
    @Max(value = 1, message = "手动新增状态值只能是0或1")
    private Integer handAddStatus;

    /**
     * 属性的类型；0->规格；1->参数
     */
    @Min(value = 0, message = "属性类型值只能是0或1")
    @Max(value = 1, message = "属性类型值只能是0或1")
    private Integer type;
}

