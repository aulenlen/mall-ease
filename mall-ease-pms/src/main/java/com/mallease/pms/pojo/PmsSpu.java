package com.mallease.pms.pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * SPU标准产品单元
 *
 * @author: Aulen
 * @create: 2025-12-10
 */
@Data
public class PmsSpu {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * SPU编码（唯一）
     */
    private String spuCode;

    /**
     * 品牌ID
     */
    private Long brandId;

    /**
     * 商品分类ID（叶子节点）
     */
    private Long productCategoryId;

    /**
     * 运费模板ID
     */
    private Long freightTemplateId;

    /**
     * 分类路径(逗号分隔): 1,5,32
     */
    private String categoryIds;

    /**
     * SPU名称
     */
    private String name;

    /**
     * 副标题
     */
    private String subTitle;

    /**
     * SPU描述
     */
    private String description;

    /**
     * 关键字
     */
    private String keywords;

    /**
     * 备注
     */
    private String note;

    /**
     * SPU主图URL
     */
    private String pic;

    /**
     * 画册图片（逗号分割）
     */
    private String albumPics;

    /**
     * 单位
     */
    private String unit;

    /**
     * 商品重量（克）
     */
    private BigDecimal weight;

    /**
     * 逻辑删除: 0-未删除, 1-已删除
     */
    private Integer deleted;

    /**
     * 上架状态: 0-下架, 1-上架
     */
    private Integer publishStatus;

    /**
     * 新品状态: 0-不是新品, 1-新品
     */
    private Integer newStatus;

    /**
     * 推荐状态: 0-不推荐, 1-推荐
     */
    private Integer recommendStatus;

    /**
     * 审核状态: 0-未审核, 1-审核通过
     */
    private Integer verifyStatus;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 总销量（所有 SKU 销量之和）
     */
    private Integer sale;

    /**
     * 最低价格（所有 SKU 最低价）
     */
    private BigDecimal minPrice;

    /**
     * 最高价格（所有 SKU 最高价）
     */
    private BigDecimal maxPrice;

    /**
     * 总库存（所有 SKU 库存之和）
     */
    private Integer stock;

    /**
     * 品牌名称（冗余）
     */
    private String brandName;

    /**
     * 商品分类名称（冗余）
     */
    private String productCategoryName;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 更新人
     */
    private String updater;

    /**
     * 乐观锁版本号
     */
    private Integer version;
}
