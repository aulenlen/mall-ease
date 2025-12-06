package com.mallease.pms.dto.cache;

import com.mallease.pms.dto.vo.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 商品详情缓存聚合对象
 * 用于 Redis 缓存和 Feign 服务调用
 *
 * 缓存策略：
 * - Redis Key: product:detail:{productId}
 * - TTL: 30分钟（可根据业务调整）
 * - 序列化方式: JSON（Jackson）
 *
 * 使用场景：
 * 1. 商品详情页展示
 * 2. 订单服务获取商品信息
 * 3. 购物车服务获取商品信息
 *
 * @author: Aulen
 * @create: 2025-11-20
 */
@Schema(description = "商品详情缓存聚合对象")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PmsProductDetailCacheDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    // ==================== 核心信息 ====================

    @Schema(description = "商品基础信息")
    private PmsProductBasicCacheDTO product;

    @Schema(description = "品牌信息")
    private PmsBrandCacheDTO brand;

    // ==================== 商品属性 ====================

    @Schema(description = "商品属性定义列表（用于渲染规格选择器）")
    private List<PmsProductAttributeVO> productAttributeList;

    @Schema(description = "商品属性值列表（当前商品的属性值）")
    private List<PmsProductAttributeValueVO> productAttributeValueList;

    // ==================== SKU与库存 ====================

    @Schema(description = "SKU库存列表")
    private List<PmsSkuStockVO> skuStockList;

    // ==================== 促销信息 ====================

    @Schema(description = "商品阶梯价格列表")
    private List<PmsProductLadderVO> productLadderList;

    @Schema(description = "商品满减列表")
    private List<PmsProductFullReductionVO> productFullReductionList;

    @Schema(description = "商品会员价格列表")
    private List<PmsMemberPriceVO> memberPriceList;

    @Schema(description = "可用优惠券列表（待SMS模块完善后对接）")
    private List<?> couponList;

    // ==================== 缓存元数据 ====================

    @Schema(description = "缓存生成时间戳")
    private Long cacheTime;

    @Schema(description = "数据版本号（用于缓存更新控制）")
    private Integer version;

    // ==================== 扩展字段 ====================

    @Schema(description = "扩展信息（用于存储不确定的业务字段）")
    private transient Object extendInfo;
}
