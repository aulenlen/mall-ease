package com.mallease.product.service.spu;

import com.mallease.product.dal.entity.SpuSnapshot;

/**
 * 单个 SPU 的发布计划。
 */
public record PublishSnapshotPlan(
        Long spuId,
        SpuSnapshot snapshot,
        boolean contentChanged) {}
