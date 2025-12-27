package com.mallease.pms.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * SKU 规格值结构
 *
 * @author: Aulen
 * @create: 2025-12-27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "SKU规格值")
public class SkuSpecValue implements Serializable {

    @Schema(description = "规格ID")
    private Long specId;

    @Schema(description = "规格名称")
    private String specName;

    @Schema(description = "规格值")
    private String value;
}