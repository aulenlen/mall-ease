package com.mallease.pms.dto.cmd;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 更新SPU命令对象
 * <p>
 * 支持部分更新，只更新传入的非空字段。
 * 对于关联数据（SKU、属性值等），采用全量替换策略。
 *
 * @author: Aulen
 * @create: 2025-12-12
 */
@Schema(description = "更新SPU命令")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatePmsSpuCmd {

    // ========================================================================
    // 必填字段
    // ========================================================================

    @Schema(description = "SPU ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "SPU ID不能为空")
    private Long id;

    // ========================================================================
    // SPU 基础信息（可选更新）
    // ========================================================================

    @Schema(description = "品牌ID")
    private Long brandId;

    @Schema(description = "商品分类ID（叶子节点）")
    private Long categoryId;

    @Schema(description = "运费模板ID")
    private Long freightTemplateId;

    @Schema(description = "SPU名称")
    @Size(max = 200, message = "SPU名称长度不能超过200个字符")
    private String name;

    @Schema(description = "副标题")
    @Size(max = 255, message = "副标题长度不能超过255个字符")
    private String subTitle;

    @Schema(description = "SPU描述")
    @Size(max = 500, message = "描述长度不能超过500个字符")
    private String description;

    @Schema(description = "关键字")
    @Size(max = 255, message = "关键字长度不能超过255个字符")
    private String keywords;

    @Schema(description = "备注")
    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String note;

    @Schema(description = "SPU主图URL")
    @Size(max = 255, message = "主图URL长度不能超过255个字符")
    private String pic;

    @Schema(description = "画册图片（逗号分割，最多5张）")
    @Size(max = 1000, message = "画册图片URL总长度不能超过1000个字符")
    private String albumPics;

    @Schema(description = "单位")
    @Size(max = 16, message = "单位长度不能超过16个字符")
    private String unit;

    @Schema(description = "商品重量（克）")
    @DecimalMin(value = "0", message = "重量不能为负数")
    private BigDecimal weight;

    @Schema(description = "新品状态: 0-不是新品, 1-新品")
    @Min(value = 0, message = "新品状态值必须为0或1")
    @Max(value = 1, message = "新品状态值必须为0或1")
    private Integer newStatus;

    @Schema(description = "推荐状态: 0-不推荐, 1-推荐")
    @Min(value = 0, message = "推荐状态值必须为0或1")
    @Max(value = 1, message = "推荐状态值必须为0或1")
    private Integer recommendStatus;

    @Schema(description = "排序（数值越小越靠前）")
    @Min(value = 0, message = "排序值不能小于0")
    private Integer sort;

    // ========================================================================
    // SPU 详情（可选更新）
    // ========================================================================

    @Schema(description = "SPU详情信息")
    @Valid
    private CreatePmsSpuCmd.SpuDetailCmd spuDetail;

    // ========================================================================
    // 关联数据（全量替换或增量更新）
    // ========================================================================

    @Schema(description = "SKU列表（快照更新：传空数组=清空；不传=不更新；仅支持传已有SKU ID）")
    @Valid
    private List<UpdatePmsSkuCmd> skuList;

    @Schema(description = "SPU参数属性值列表（全量替换）")
    @Valid
    private List<CreatePmsSpuCmd.SpuAttributeValueCmd> attributeValueList;

    @Schema(description = "满减规则列表（全量替换）")
    @Valid
    private List<CreatePmsSpuCmd.SpuFullReductionCmd> fullReductionList;

    @Schema(description = "专题关联ID列表（全量替换）")
    private List<Long> subjectIds;

    @Schema(description = "优选专区关联ID列表（全量替换）")
    private List<Long> preferenceAreaIds;
}
