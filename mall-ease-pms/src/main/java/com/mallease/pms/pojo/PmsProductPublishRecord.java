package com.mallease.pms.pojo;

import lombok.Data;

import java.util.Date;

/**
 * 商品上下架记录表
 *
 * @author: Aulen
 * @create: 2025-11-17
 */
@Data
public class PmsProductPublishRecord {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 商品名称（冗余字段）
     */
    private String productName;

    /**
     * 操作人ID
     */
    private Long operatorId;

    /**
     * 操作人姓名
     */
    private String operatorName;

    /**
     * 操作类型：1-上架, 0-下架
     */
    private Integer action;

    /**
     * 原状态（可为空）：1-上架, 0-下架
     */
    private Integer fromStatus;

    /**
     * 新状态：1-上架, 0-下架
     */
    private Integer toStatus;

    /**
     * 操作原因
     */
    private String reason;

    /**
     * 创建时间
     */
    private Date createTime;
}
