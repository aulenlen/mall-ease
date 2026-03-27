package com.mallease.marketing.controller.admin.flash.vo;

import com.mallease.common.dto.client.BaseQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "秒杀商品查询对象")
public class FlashProductPageReqVO extends BaseQuery {

    @Schema(description = "场次ID")
    private Long sessionId;

    @Schema(description = "商品关键字，按SPU名称模糊匹配")
    private String keyword;

    @Schema(description = "品牌ID")
    private Long brandId;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "SPU ID")
    private Long spuId;

    @Schema(description = "SKU ID")
    private Long skuId;

    @Schema(description = "路由类型：0-非热点秒杀，1-热点秒杀")
    private Integer routeType;
}
