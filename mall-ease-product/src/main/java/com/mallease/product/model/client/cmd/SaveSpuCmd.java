package com.mallease.product.model.client.cmd;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 保存SPU命令（创建/更新统一）
 *
 * @author: Aulen
 * @create: 2025-12-12
 */
@Schema(description = "保存SPU命令")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaveSpuCmd {

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

    @Schema(description = "SPU ID（创建时不传，更新时必传）")
    @NotNull(groups = Update.class, message = "更新时SPU ID不能为空")
    private Long id;

    @Schema(description = "品牌ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(groups = Create.class, message = "创建时品牌ID不能为空")
    private Long brandId;

    @Schema(description = "商品分类ID（叶子节点）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(groups = Create.class, message = "创建时商品分类ID不能为空")
    private Long categoryId;

    @Schema(description = "运费模板ID")
    private Long freightTemplateId;

    @Schema(description = "SPU名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(groups = Create.class, message = "创建时SPU名称不能为空")
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
    @Builder.Default
    private Integer newStatus = 0;

    @Schema(description = "推荐状态: 0-不推荐, 1-推荐")
    @Min(value = 0, message = "推荐状态值必须为0或1")
    @Max(value = 1, message = "推荐状态值必须为0或1")
    @Builder.Default
    private Integer recommendStatus = 0;

    @Schema(description = "排序（数值越小越靠前）")
    @Min(value = 0, message = "排序值不能小于0")
    @Builder.Default
    private Integer sort = 0;

    @Schema(description = "SPU详情信息（可选）")
    @Valid
    private SpuDetailCmd spuDetail;

    @Schema(description = "SKU列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(groups = Create.class, message = "创建时SKU列表不能为空")
    @Size(min = 1, groups = Create.class, message = "至少需要一个SKU")
    @Valid
    private List<SaveSkuCmd> skuList;

    @Schema(description = "SPU参数属性值列表（可选）")
    @Valid
    private List<SpuParamValueCmd> paramValueList;

    @Schema(description = "满减规则列表（可选）")
    @Valid
    private List<SpuFullReductionCmd> fullReductionList;

    @Schema(description = "专题关联ID列表")
    private List<Long> subjectIds;

    @Schema(description = "优选专区关联ID列表")
    private List<Long> preferenceAreaIds;

    /**
     * SPU详情命令（内部类）
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "SPU详情")
    public static class SpuDetailCmd {

        @Schema(description = "详情标题")
        @Size(max = 255, message = "详情标题长度不能超过255个字符")
        private String detailTitle;

        @Schema(description = "详情描述")
        @Size(max = 500, message = "详情描述长度不能超过500个字符")
        private String detailDesc;

        @Schema(description = "PC端详情网页内容（HTML）")
        private String detailHtml;

        @Schema(description = "移动端详情网页内容（HTML）")
        private String detailMobileHtml;

        @Schema(description = "产品服务（逗号分隔: 1-无忧退货, 2-快速退款, 3-免费包邮）")
        @Size(max = 64, message = "服务ID长度不能超过64个字符")
        private String serviceIds;

        @Schema(description = "包装清单")
        @Size(max = 500, message = "包装清单长度不能超过500个字符")
        private String packingList;

        @Schema(description = "售后服务")
        @Size(max = 500, message = "售后服务长度不能超过500个字符")
        private String afterSaleService;
    }

    /**
     * SPU参数属性值命令（内部类）
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "SPU参数属性值")
    public static class SpuParamValueCmd {

        @Schema(description = "参数ID", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "参数ID不能为空")
        private Long paramId;

        @Schema(description = "参数值", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "参数值不能为空")
        @Size(max = 255, message = "参数值长度不能超过255个字符")
        private String value;
    }

    /**
     * 满减规则
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "满减规则")
    public static class SpuFullReductionCmd {

        @Schema(description = "满足金额")
        @DecimalMin(value = "0.01", message = "满足金额必须大于0")
        private BigDecimal fullPrice;

        @Schema(description = "减少金额")
        @DecimalMin(value = "0.01", message = "减少金额必须大于0")
        private BigDecimal reducePrice;

        /**
         * 判断是否为有效的满减规则
         */
        public boolean isValid() {
            return fullPrice != null && reducePrice != null
                    && fullPrice.compareTo(BigDecimal.ZERO) > 0
                    && reducePrice.compareTo(BigDecimal.ZERO) > 0;
        }
    }

    /**
     * 获取满减规则列表（自动过滤无效数据）
     */
    public List<SpuFullReductionCmd> getFullReductionList() {
        if (fullReductionList == null || fullReductionList.isEmpty()) {
            return null;
        }
        List<SpuFullReductionCmd> validList = fullReductionList.stream()
                .filter(SpuFullReductionCmd::isValid)
                .collect(Collectors.toList());
        return validList.isEmpty() ? null : validList;
    }
}
