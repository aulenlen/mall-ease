package com.mallease.product.service.spu;

import java.util.List;

/**
 * 处理 SPU 上下架主链路。
 * 包含快照构建、发布状态变更，以及事务提交后的缓存和搜索同步。
 */
public interface SpuPublishService {

    /**
     * 发布指定商品。
     */
    int publish(List<Long> spuIds);

    /**
     * 下架指定商品。
     */
    int unpublish(List<Long> spuIds);
}
