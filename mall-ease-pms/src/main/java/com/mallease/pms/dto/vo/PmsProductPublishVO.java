package com.mallease.pms.dto.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 商品批量上架结果视图对象
 *
 * @author: Aulen
 * @create: 2025-11-19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PmsProductPublishVO {

    /**
     * 成功数量
     */
    private Integer successCount;

    /**
     * 失败数量
     */
    private Integer failCount;

    /**
     * 跳过数量（已处于目标状态的商品）
     */
    private Integer skippedCount;

    /**
     * 失败详情列表
     */
    private List<PublishFailDetailVO> failDetails;

    /**
     * 跳过的商品ID列表（已处于目标状态）
     */
    private List<Long> skippedIds;
}
