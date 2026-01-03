package com.mallease.product.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * SPU 上下架状态变更事件
 */
@Getter
@AllArgsConstructor
public class SpuPublishEvent {

    private final List<Long> spuIds;

    /** 1-上架, 0-下架 */
    private final Integer publishStatus;

}
