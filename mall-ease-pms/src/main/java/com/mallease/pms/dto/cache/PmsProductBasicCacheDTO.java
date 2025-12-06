package com.mallease.pms.dto.cache;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品基础信息缓存对象
 * 排除大文本字段，优化缓存存储
 * 用于 Redis 缓存和 Feign 服务调用
 *
 * @author: Aulen
 * @create: 2025-11-20
 */
@Schema(description = "商品基础信息缓存对象")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PmsProductBasicCacheDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "商品ID")
    private Long id;

    @Schema(description = "品牌ID")
    private Long brandId;

    @Schema(description = "商品分类ID")
    private Long productCategoryId;

    @Schema(description = "运费模板ID")
    private Long freightTemplateId;

    @Schema(description = "商品属性分类ID")
    private Long productAttributeCategoryId;

    @Schema(description = "商品名称")
    private String name;

    @Schema(description = "商品图片")
    private String pic;

    @Schema(description = "商品货号")
    private String productSn;

    @Schema(description = "删除状态(0:未删除 1:已删除)")
    private Integer deleteStatus;

    @Schema(description = "上架状态(0:下架 1:上架)")
    private Integer publishStatus;

    @Schema(description = "新品状态(0:不是新品 1:新品)")
    private Integer newStatus;

    @Schema(description = "推荐状态(0:不推荐 1:推荐)")
    private Integer recommendStatus;

    @Schema(description = "审核状态(0:未审核 1:审核通过)")
    private Integer verifyStatus;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "价格")
    private BigDecimal price;

    @Schema(description = "促销价格")
    private BigDecimal promotionPrice;

    @Schema(description = "市场价")
    private BigDecimal originalPrice;

    @Schema(description = "库存预警值")
    private Integer lowStock;

    @Schema(description = "赠送的成长值")
    private Integer giftGrowth;

    @Schema(description = "赠送的积分")
    private Integer giftPoint;

    @Schema(description = "限制使用的积分数")
    private Integer usePointLimit;

    @Schema(description = "副标题")
    private String subTitle;

    @Schema(description = "单位")
    private String unit;

    @Schema(description = "商品重量(克)")
    private BigDecimal weight;

    @Schema(description = "预告商品(0:不是 1:是)")
    private Integer previewStatus;

    @Schema(description = "产品服务(逗号分隔: 1无忧退货 2快速退款 3免费包邮)")
    private String serviceIds;

    @Schema(description = "关键词")
    private String keywords;

    @Schema(description = "备注")
    private String note;

    @Schema(description = "画册图片(逗号分隔,最多5张)")
    private String albumPics;

    @Schema(description = "详情标题")
    private String detailTitle;

    @Schema(description = "促销开始时间")
    private Date promotionStartTime;

    @Schema(description = "促销结束时间")
    private Date promotionEndTime;

    @Schema(description = "活动限购数量")
    private Integer promotionPerLimit;

    @Schema(description = "促销类型(0:无促销 1:促销价 2:会员价 3:阶梯价格 4:满减价格 5:限时购)")
    private Integer promotionType;

    @Schema(description = "品牌名称")
    private String brandName;

    @Schema(description = "商品分类名称")
    private String productCategoryName;

    @Schema(description = "商品描述")
    private String description;

    @Schema(description = "商品分类的父级ID")
    private Long cateParentId;

    // 大文本已排除，需要时单独查询
    // - detailHtml (详情网页内容)
    // - detailMobileHtml (移动端详情)
    // - detailDesc (详情描述)
}
