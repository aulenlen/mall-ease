package com.mallease.pms.service.impl;

import com.mallease.common.exception.Asserts;
import com.mallease.common.service.RedisService;
import com.mallease.pms.dto.vo.PmsProductPublishVO;
import com.mallease.pms.dto.vo.SmartPublishResultVO;
import com.mallease.pms.dto.vo.TaskProgressVO;
import com.mallease.pms.service.PmsProductService;
import com.mallease.pms.service.PublishProductAsyncHandler;
import com.mallease.pms.service.SmartPublishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author: Aulen
 * @description:
 * @create: 2025-11-19 14:44
 **/
@Slf4j
@Service
public class SmartPublishServiceImpl implements SmartPublishService {
    @Autowired
    private PmsProductService productService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private PublishProductAsyncHandler publishProductAsyncHandler;

    private static final int ASYNC_THRESHOLD = 20;


    @Override
    public SmartPublishResultVO smartPublish(List<Long> productids, Integer publishStatus) {
        // 商品小于100个直接处理
        if (productids.size() < ASYNC_THRESHOLD) {
            PmsProductPublishVO result = productService.updatePublishStatusBatch(
                    productids, publishStatus);
            return SmartPublishResultVO.sync(result, productids.size());
        } else {
            String taskId = "TASK-" + System.currentTimeMillis();
            publishProductAsyncHandler.processAsync(productids, publishStatus, taskId);
            return SmartPublishResultVO.async(taskId, productids.size(),"任务已提交，请稍后查询", 1);
        }
    }

    @Override
    public TaskProgressVO getProgress(String taskId) {
        String key = "publish:progress:" + taskId;
        return (TaskProgressVO) redisService.get(key);
    }
}
