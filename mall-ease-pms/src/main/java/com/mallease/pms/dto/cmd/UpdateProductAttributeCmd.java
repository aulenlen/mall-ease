package com.mallease.pms.dto.cmd;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * 更新商品属性命令
 *
 * @author: Aulen
 * @create: 2025-11-15
 */
@Data
@Schema(description = "更新商品属性命令")
public class UpdateProductAttributeCmd {

    @Schema(description = "属性名称")
    @Size(max = 64, message = "属性名称长度不能超过64个字符")
    private String name;

    @Schema(description = "属性选择类型：0->唯一；1->单选；2->多选")
    @Min(value = 0, message = "选择类型值必须在0-2之间")
    @Max(value = 2, message = "选择类型值必须在0-2之间")
    private Integer selectType;

    @Schema(description = "属性录入方式：0->手工录入；1->从列表中选取")
    @Min(value = 0, message = "录入方式值必须为0或1")
    @Max(value = 1, message = "录入方式值必须为0或1")
    private Integer inputType;

    @Schema(description = "可选值列表，以逗号隔开")
    @Size(max = 255, message = "可选值列表长度不能超过255个字符")
    private String inputList;

    @Schema(description = "排序")
    @Min(value = 0, message = "排序值不能小于0")
    private Integer sort;

    @Schema(description = "分类筛选样式：0->普通；1->颜色")
    @Min(value = 0, message = "筛选样式值必须为0或1")
    @Max(value = 1, message = "筛选样式值必须为0或1")
    private Integer filterType;

    @Schema(description = "检索类型：0->不需要进行检索；1->关键字检索；2->范围检索")
    @Min(value = 0, message = "检索类型值必须在0-2之间")
    @Max(value = 2, message = "检索类型值必须在0-2之间")
    private Integer searchType;

    @Schema(description = "相同属性产品是否关联：0->不关联；1->关联")
    @Min(value = 0, message = "关联状态值必须为0或1")
    @Max(value = 1, message = "关联状态值必须为0或1")
    private Integer relatedStatus;

    @Schema(description = "是否支持手动新增：0->不支持；1->支持")
    @Min(value = 0, message = "手动新增状态值必须为0或1")
    @Max(value = 1, message = "手动新增状态值必须为0或1")
    private Integer handAddStatus;

    @Schema(description = "属性的类型：0->规格；1->参数")
    @Min(value = 0, message = "属性类型值必须为0或1")
    @Max(value = 1, message = "属性类型值必须为0或1")
    private Integer type;
}
