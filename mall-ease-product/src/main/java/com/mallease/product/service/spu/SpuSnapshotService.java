package com.mallease.product.service.spu;

import com.mallease.product.dal.entity.SpuSnapshot;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 管理 SPU 发布快照的构建、保存和读取。
 */
public interface SpuSnapshotService {

    /**
     * 为指定 SPU 构建发布计划。
     */
    List<PublishSnapshotPlan> buildPublishPlans(List<Long> spuIds, LocalDateTime publishedAt);

    /**
     * 批量保存发布快照。
     */
    int saveSnapshots(List<SpuSnapshot> snapshots);

    /**
     * 按 SPU ID 查询单条快照记录。
     */
    SpuSnapshot getBySpuId(Long spuId);
}
