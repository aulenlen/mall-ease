package com.mallease.pms.dto.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * SPU详情视图对象
 * <p>
 * 包含SPU完整信息、详情、SKU列表、参数属性等复合数据
 * 用于详情页、编辑页等需要完整数据的场景
 *
 * @author: Aulen
 * @create: 2025-12-12
 */
@Schema(description = "SPU详情视图对象")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PmsSpuDetailVO {

    // ========================================================================
    // SPU 基础信息
    // ========================================================================

    @Schema(description = "SPU ID")
    private Long id;

    @Schema(description = "SPU编码")
    private String spuCode;

    @Schema(description = "品牌ID")
    private Long brandId;

    @Schema(description = "品牌名称")
    private String brandName;

    @Schema(description = "商品分类ID")
    private Long categoryId;

    @Schema(description = "商品分类名称")
    private String categoryName;

    @Schema(description = "分类路径(逗号分隔)")
    private String categoryIds;

    @Schema(description = "运费模板ID")
    private Long freightTemplateId;

    @Schema(description = "SPU名称")
    private String name;

    @Schema(description = "副标题")
    private String subTitle;

    @Schema(description = "SPU描述")
    private String description;

    @Schema(description = "关键字")
    private String keywords;

    @Schema(description = "备注")
    private String note;

    @Schema(description = "SPU主图URL")
    private String pic;

    @Schema(description = "画册图片（逗号分割）")
    private String albumPics;

    @Schema(description = "画册图片列表")
    private List<String> albumPicList;

    @Schema(description = "单位")
    private String unit;

    @Schema(description = "商品重量（克）")
    private BigDecimal weight;

    @Schema(description = "上架状态: 0-下架, 1-上架")
    private Integer publishStatus;

    @Schema(description = "新品状态: 0-不是新品, 1-新品")
    private Integer newStatus;

    @Schema(description = "推荐状态: 0-不推荐, 1-推荐")
    private Integer recommendStatus;

    @Schema(description = "审核状态: 0-未审核, 1-审核通过")
    private Integer verifyStatus;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "总销量")
    private Integer sale;

    @Schema(description = "最低价格")
    private BigDecimal minPrice;

    @Schema(description = "最高价格")
    private BigDecimal maxPrice;

    @Schema(description = "总库存")
    private Integer stock;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "创建人")
    private String creator;

    @Schema(description = "更新人")
    private String updater;

    @Schema(description = "版本号")
    private Integer version;

    // ========================================================================
    // SPU 详情信息
    // ========================================================================

    @Schema(description = "详情标题")
    private String detailTitle;

    @Schema(description = "详情描述")
    private String detailDesc;

    @Schema(description = "PC端详情网页内容")
    private String detailHtml;

    @Schema(description = "移动端详情网页内容")
    private String detailMobileHtml;

    @Schema(description = "产品服务（逗号分隔）")
    private String serviceIds;

    @Schema(description = "产品服务列表")
    private List<String> serviceList;

    @Schema(description = "包装清单")
    private String packingList;

    @Schema(description = "售后服务")
    private String afterSaleService;

    // ========================================================================
    // 关联数据
    // ========================================================================

    @Schema(description = "SKU列表")
    private List<PmsSkuVO> skuList;

    @Schema(description = "参数属性值列表")
    private List<SpuAttributeValueVO> attributeValueList;

    @Schema(description = "满减规则列表")
    private List<SpuFullReductionVO> fullReductionList;

    @Schema(description = "关联的专题ID列表")
    private List<Long> subjectIds;

    @Schema(description = "关联的优选专区ID列表")
    private List<Long> preferenceAreaIds;

    // ========================================================================
    // 嵌套VO对象
    // ========================================================================

    /**
     * SPU参数属性值VO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "SPU参数属性值")
    public static class SpuAttributeValueVO {

        @Schema(description = "主键ID")
        private Long id;

        @Schema(description = "参数ID")
        private Long paramId;

        @Schema(description = "参数名称")
        private String paramName;

        @Schema(description = "参数值")
        private String value;
    }

    /**
     * 满减规则VO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "满减规则")
    public static class SpuFullReductionVO {

        @Schema(description = "主键ID")
        private Long id;

        @Schema(description = "满足金额")
        private BigDecimal fullPrice;

        @Schema(description = "减少金额")
        private BigDecimal reducePrice;

        @Schema(description = "优惠描述")
        private String description;
    }
}