package com.mallease.product.model.client.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * SKU库存视图对象
 *
 * @author: Aulen
 * @create: 2025-11-15
 */
@Schema(description = "SKU库存")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkuStockVO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "SKU ID")
    private Long skuId;

    @Schema(description = "SPU ID")
    private Long spuId;

    @Schema(description = "可用库存")
    private Integer stock;

    @Schema(description = "锁定库存（下单未支付）")
    private Integer lockStock;

    @Schema(description = "累计销量")
    private Integer sale;

    @Schema(description = "库存预警值")
    private Integer lowStock;

    @Schema(description = "库存状态: 0-无货, 1-有货, 2-预售")
    private Integer stockStatus;

    @Schema(description = "版本号")
    private Integer version;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "是否库存预警（库存低于预警值）")
    private Boolean lowStockWarning;

    @Schema(description = "SPU名称（商品名称）")
    private String spuName;

    @Schema(description = "SKU属性值（JSON格式）")
    private String attrValues;

    @Schema(description = "属性值列表（前端展示用）")
    private List<AttrValueVO> attrValuesObj;

    /**
     * SKU属性值
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "SKU属性值")
    public static class AttrValueVO {

        @Schema(description = "属性ID")
        private Long attrId;

        @Schema(description = "属性名称")
        private String attrName;

        @Schema(description = "属性值")
        private String attrValue;
    }
}