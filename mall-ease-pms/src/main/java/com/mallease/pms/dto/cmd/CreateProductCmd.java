package com.mallease.pms.dto.cmd;

import com.mallease.pms.dto.vo.*;
import io.swagger.v3.oas.annotations.media.Schema;
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
 * 创建商品命令对象
 *
 * @author: Aulen
 * @create: 2025-11-15
 */
@Schema(description = "创建商品命令")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProductCmd {

    @Schema(description = "品牌ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "品牌ID不能为空")
    private Long brandId;

    @Schema(description = "商品分类ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "商品分类ID不能为空")
    private Long productCategoryId;

    @Schema(description = "运费模板ID")
    private Long freightTemplateId;

    @Schema(description = "商品属性分类ID")
    private Long productAttributeCategoryId;

    @Schema(description = "商品名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "商品名称不能为空")
    private String name;

    @Schema(description = "商品图片")
    private String pic;

    @Schema(description = "商品货号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "货号不能为空")
    private String productSn;

    @Schema(description = "删除状态(0:未删除 1:已删除)")
    @Builder.Default
    private Integer deleteStatus = 0;

    @Schema(description = "上架状态(0:下架 1:上架)")
    @Builder.Default
    private Integer publishStatus = 0;

    @Schema(description = "新品状态(0:不是新品 1:新品)")
    @Builder.Default
    private Integer newStatus = 0;

    @Schema(description = "推荐状态(0:不推荐 1:推荐)")
    @Builder.Default
    private Integer recommendStatus = 0;

    @Schema(description = "审核状态(0:未审核 1:审核通过)")
    @Builder.Default
    private Integer verifyStatus = 0;

    @Schema(description = "排序")
    @Builder.Default
    private Integer sort = 0;

    @Schema(description = "销量")
    @Builder.Default
    private Integer sale = 0;

    @Schema(description = "价格", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "价格不能为空")
    private BigDecimal price;

    @Schema(description = "促销价格")
    private BigDecimal promotionPrice;

    @Schema(description = "赠送的成长值")
    @Builder.Default
    private Integer giftGrowth = 0;

    @Schema(description = "赠送的积分")
    @Builder.Default
    private Integer giftPoint = 0;

    @Schema(description = "限制使用的积分数")
    private Integer usePointLimit;

    @Schema(description = "副标题")
    private String subTitle;

    @Schema(description = "市场价")
    private BigDecimal originalPrice;

    @Schema(description = "库存", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "库存不能为空")
    private Integer stock;

    @Schema(description = "库存预警值")
    private Integer lowStock;

    @Schema(description = "单位")
    private String unit;

    @Schema(description = "商品重量(克)")
    private BigDecimal weight;

    @Schema(description = "预告商品(0:不是 1:是)")
    @Builder.Default
    private Integer previewStatus = 0;

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
    @Builder.Default
    private Integer promotionType = 0;

    @Schema(description = "品牌名称")
    private String brandName;

    @Schema(description = "商品分类名称")
    private String productCategoryName;

    @Schema(description = "商品描述")
    private String description;

    @Schema(description = "详情描述")
    private String detailDesc;

    @Schema(description = "详情网页内容")
    private String detailHtml;

    @Schema(description = "移动端详情")
    private String detailMobileHtml;

    @Schema(description = "商品阶梯价格列表")
    @Valid
    private List<PmsProductLadderVO> productLadderList;

    @Schema(description = "商品满减列表")
    @Valid
    private List<PmsProductFullReductionVO> productFullReductionList;

    @Schema(description = "商品会员价格列表")
    @Valid
    private List<PmsMemberPriceVO> memberPriceList;

    @Schema(description = "SKU库存列表")
    @Valid
    private List<PmsSkuStockVO> skuStockList;

    @Schema(description = "商品属性值列表")
    @Valid
    private List<PmsProductAttributeValueVO> productAttributeValueList;

    @Schema(description = "专题商品关联列表")
    @Valid
    private List<CmsSubjectProductRelationVO> subjectProductRelationList;

    @Schema(description = "优选专区商品关联列表")
    @Valid
    private List<CmsPreferenceAreaProductRelationVO> preferenceAreaProductRelationList;
}
