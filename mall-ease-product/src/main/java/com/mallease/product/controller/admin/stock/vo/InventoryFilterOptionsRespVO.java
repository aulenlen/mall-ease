package com.mallease.product.controller.admin.stock.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 库存页筛选项
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "库存页筛选项")
public class InventoryFilterOptionsRespVO {

    @Schema(description = "品牌筛选项")
    private List<OptionItemVO> brands;

    @Schema(description = "分类筛选项")
    private List<OptionItemVO> categories;

    @Schema(description = "库存状态筛选项")
    private List<OptionItemVO> stockStatuses;

    @Schema(description = "页签选项")
    private List<OptionItemVO> tabs;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "筛选项")
    public static class OptionItemVO {

        @Schema(description = "值")
        private String value;

        @Schema(description = "标签")
        private String label;
    }
}
