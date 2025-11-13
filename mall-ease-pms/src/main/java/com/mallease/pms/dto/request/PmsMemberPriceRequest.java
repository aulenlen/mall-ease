package com.mallease.pms.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 商品会员价格请求参数
 *
 * @author: Aulen
 * @create: 2025-11-13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PmsMemberPriceRequest {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 产品ID
     */
    private Long productId;

    /**
     * 会员等级ID
     */
    @NotNull(message = "会员等级ID不能为空")
    private Long memberLevelId;

    /**
     * 会员价格
     */
    @NotNull(message = "会员价格不能为空")
    private BigDecimal memberPrice;

    /**
     * 会员等级名称
     */
    private String memberLevelName;
}
