package com.mallease.common.dto.remote;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 购物车项数据传输对象（跨服务传输）
 *
 * @author: Aulen
 * @create: 2026-01-27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 购物车项ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * SKU ID
     */
    private Long skuId;

    /**
     * SPU ID
     */
    private Long spuId;

    /**
     * 商品数量
     */
    private Integer quantity;

    /**
     * 选中状态: 0-未选中, 1-已选中
     */
    private Integer checked;

    /**
     * 商品名称
     */
    private String spuName;

    /**
     * SKU图片URL
     */
    private String skuPic;

    /**
     * SKU规格属性
     */
    private String skuAttrs;

    /**
     * 加入时单价
     */
    private BigDecimal price;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
