package com.mallease.product.controller.admin.spu.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "后台保存SKU请求")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkuSaveReqVO {

    @Schema(description = "SKU ID，更新已有SKU时传")
    private Long id;

    @Schema(description = "SKU编码")
    @Size(max = 64, message = "SKU编码长度不能超过64个字符")
    private String skuCode;

    @Schema(description = "SKU规格值", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "SKU规格值不能为空")
    @Valid
    private List<@NotNull(message = "SKU规格项不能为空") @Valid AttrValueReqVO> attrValues;

    @Schema(description = "SKU图片")
    @Size(max = 255, message = "SKU图片长度不能超过255个字符")
    private String pic;

    @Schema(description = "销售价", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "销售价不能为空")
    @DecimalMin(value = "0.01", message = "销售价必须大于0")
    private BigDecimal basePrice;

    @Schema(description = "划线价")
    @DecimalMin(value = "0", message = "划线价不能为负数")
    private BigDecimal compareAtPrice;

    @Schema(description = "启用状态: 0-禁用, 1-启用")
    @Min(value = 0, message = "启用状态值必须为0或1")
    @Max(value = 1, message = "启用状态值必须为0或1")
    @Builder.Default
    private Integer enableStatus = 1;

    @Schema(description = "SKU规格值")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AttrValueReqVO {

        @Schema(description = "属性ID，自定义规格时可不传")
        private Long attrId;

        @Schema(description = "属性名称", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "属性名称不能为空")
        @Size(max = 64, message = "属性名称长度不能超过64个字符")
        private String attrName;

        @Schema(description = "属性值", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "属性值不能为空")
        private String attrValue;
    }
}