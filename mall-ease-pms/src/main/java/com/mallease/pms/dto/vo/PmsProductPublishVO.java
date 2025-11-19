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
     * 失败详情列表
     */
    private List<PublishFailDetailVO> failDetails;
}
