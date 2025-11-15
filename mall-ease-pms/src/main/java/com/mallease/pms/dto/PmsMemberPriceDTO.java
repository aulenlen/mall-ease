package com.mallease.pms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商品会员价格DTO（服务间传输对象）
 *
 * @author: Aulen
 * @create: 2025-11-15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PmsMemberPriceDTO implements Serializable {

    private static final long serialVersionUID = 1L;

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
    private Long memberLevelId;

    /**
     * 会员价格
     */
    private BigDecimal memberPrice;

    /**
     * 会员等级名称
     */
    private String memberLevelName;
}
