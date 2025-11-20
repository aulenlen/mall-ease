package com.mallease.pms.service;

import com.mallease.pms.dto.vo.SmartPublishResultVO;
import com.mallease.pms.dto.vo.TaskProgressVO;

import java.util.List;

/**
 * @author: Aulen
 * @description: 批量上架
 * @create: 2025-11-19 14:44
 **/
public interface SmartPublishService {
    /**
     *
     * @param productIds    商品ID列表
     * @param publishStatus 商品发布状态 1 0
     * @return
     */
    SmartPublishResultVO smartPublish(List<Long> productIds, Integer publishStatus);

    /**
     * 查询任务进度
     *
     * @param taskId 批量任务ID
     * @return 结果
     */
    TaskProgressVO getProgress(String taskId);
}
