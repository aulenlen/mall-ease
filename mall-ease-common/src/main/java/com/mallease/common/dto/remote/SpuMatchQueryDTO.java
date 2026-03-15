package com.mallease.common.dto.remote;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Schema(description = "SPU匹配查询对象")
public class SpuMatchQueryDTO {

    @Schema(description = "商品关键字，按SPU名称模糊匹配")
    private String keyword;

    @Schema(description = "品牌ID")
    private Long brandId;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "候选SPU ID列表，仅在这些商品中做过滤")
    private List<Long> candidateSpuIds = new ArrayList<>();
}
