package com.mallease.product.dal.entity;

import lombok.Data;

import java.util.Date;

/**
 * SPU 上下架记录实体
 *
 * @author: Aulen
 * @create: 2025-12-23
 */
@Data
public class SpuPublishRecord {
    private Long id;
    private Long spuId;
    private String spuName;
    private Long operatorId;
    private String operatorName;
    private Integer action;
    private Integer fromStatus;
    private Integer toStatus;
    private String reason;
    private Date createTime;
}
