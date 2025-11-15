package com.mallease.pms.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 商品聚合请求对象（用于商品创建和更新）
 *
 * @author: Aulen
 * @description: 包含商品基本信息及所有关联信息的聚合请求对象
 * @create: 2025-11-13 23:09
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PmsProductAggregationRequest {
    /**
     * 主键ID（更新时需要）
     */
    private Long id;

    /**
     * 品牌ID
     */
    @NotNull(message = "品牌ID不能为空")
    private Long brandId;

    /**
     * 产品分类ID
     */
    @NotNull(message = "产品分类ID不能为空")
    private Long productCategoryId;

    /**
     * 运费模板ID
     */
    private Long feightTemplateId;

    /**
     * 产品属性分类ID
     */
    private Long productAttributeCategoryId;

    /**
     * 商品名称
     */
    @NotBlank(message = "商品名称不能为空")
    private String name;

    /**
     * 商品图片
     */
    private String pic;

    /**
     * 货号
     */
    @NotBlank(message = "货号不能为空")
    private String productSn;

    /**
     * 删除状态：0->未删除；1->已删除
     */
    @Builder.Default
    private Integer deleteStatus = 0;

    /**
     * 上架状态：0->下架；1->上架
     */
    @Builder.Default
    private Integer publishStatus = 0;

    /**
     * 新品状态:0->不是新品；1->新品
     */
    @Builder.Default
    private Integer newStatus = 0;

    /**
     * 推荐状态；0->不推荐；1->推荐
     */
    @Builder.Default
    private Integer recommandStatus = 0;

    /**
     * 审核状态：0->未审核；1->审核通过
     */
    @Builder.Default
    private Integer verifyStatus = 0;

    /**
     * 排序
     */
    @Builder.Default
    private Integer sort = 0;

    /**
     * 销量
     */
    @Builder.Default
    private Integer sale = 0;

    /**
     * 价格
     */
    @NotNull(message = "价格不能为空")
    private BigDecimal price;

    /**
     * 促销价格
     */
    private BigDecimal promotionPrice;

    /**
     * 赠送的成长值
     */
    @Builder.Default
    private Integer giftGrowth = 0;

    /**
     * 赠送的积分
     */
    @Builder.Default
    private Integer giftPoint = 0;

    /**
     * 限制使用的积分数
     */
    private Integer usePointLimit;

    /**
     * 副标题
     */
    private String subTitle;

    /**
     * 市场价
     */
    private BigDecimal originalPrice;

    /**
     * 库存
     */
    @NotNull(message = "库存不能为空")
    private Integer stock;

    /**
     * 库存预警值
     */
    private Integer lowStock;

    /**
     * 单位
     */
    private String unit;

    /**
     * 商品重量，默认为克
     */
    private BigDecimal weight;

    /**
     * 是否为预告商品：0->不是；1->是
     */
    @Builder.Default
    private Integer previewStatus = 0;

    /**
     * 以逗号分割的产品服务：1->无忧退货；2->快速退款；3->免费包邮
     */
    private String serviceIds;

    /**
     * 关键词
     */
    private String keywords;

    /**
     * 备注
     */
    private String note;

    /**
     * 画册图片，连产品图片限制为5张，以逗号分割
     */
    private String albumPics;

    /**
     * 详情标题
     */
    private String detailTitle;

    /**
     * 促销开始时间
     */
    private Date promotionStartTime;

    /**
     * 促销结束时间
     */
    private Date promotionEndTime;

    /**
     * 活动限购数量
     */
    private Integer promotionPerLimit;

    /**
     * 促销类型：0->没有促销使用原价;1->使用促销价；2->使用会员价；3->使用阶梯价格；4->使用满减价格；5->限时购
     */
    @Builder.Default
    private Integer promotionType = 0;

    /**
     * 品牌名称
     */
    private String brandName;

    /**
     * 商品分类名称
     */
    private String productCategoryName;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 详情描述
     */
    private String detailDesc;

    /**
     * 产品详情网页内容
     */
    private String detailHtml;

    /**
     * 移动端网页详情
     */
    private String detailMobileHtml;

    /**
     * 商品阶梯价格列表
     */
    @Valid
    private List<PmsProductLadderRequest> productLadderList;

    /**
     * 商品满减列表
     */
    @Valid
    private List<PmsProductFullReductionRequest> productFullReductionList;

    /**
     * 商品会员价格列表
     */
    @Valid
    private List<PmsMemberPriceRequest> memberPriceList;

    /**
     * SKU库存列表
     */
    @Valid
    private List<PmsSkuStockRequest> skuStockList;

    /**
     * 商品属性值列表
     */
    @Valid
    private List<PmsProductAttributeValueRequest> productAttributeValueList;

    /**
     * 专题商品关联列表
     */
    @Valid
    private List<CmsSubjectProductRelationRequest> subjectProductRelationList;

    /**
     * 优选专区商品关联列表
     */
    @Valid
    private List<CmsPreferenceAreaProductRelationRequest> preferenceAreaProductRelationList;
}
