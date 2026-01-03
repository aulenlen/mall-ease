package com.mallease.product.model.data.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * SPU详情表（垂直拆分，低频查询字段）
 *
 * @author: Aulen
 * @create: 2025-12-10
 */
@Data
public class SpuDetail {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * SPU ID（关联 pms_spu.id）
     */
    private Long spuId;

    /**
     * 详情标题
     */
    private String detailTitle;

    /**
     * 详情描述
     */
    private String detailDesc;

    /**
     * 产品详情网页内容（PC端）
     */
    private String detailHtml;

    /**
     * 移动端网页详情
     */
    private String detailMobileHtml;

    /**
     * 产品服务（逗号分割）: 1-无忧退货, 2-快速退款, 3-免费包邮
     */
    private String serviceIds;

    /**
     * 包装清单
     */
    private String packingList;

    /**
     * 售后服务
     */
    private String afterSaleService;

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
}
