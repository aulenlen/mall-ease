package com.mallease.pms.service.impl;

import com.mallease.common.service.RedisService;
import com.mallease.common.util.LoginContextUtil;
import com.mallease.pms.dto.vo.PmsProductPublishVO;
import com.mallease.pms.dto.vo.SmartPublishResultVO;
import com.mallease.pms.dto.vo.TaskProgressVO;
import com.mallease.pms.service.PmsProductService;
import com.mallease.pms.service.PublishProductAsyncHandler;
import com.mallease.pms.service.SmartPublishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    private static final int ASYNC_THRESHOLD = 100;


    @Override
    public SmartPublishResultVO smartPublish(List<Long> productIds, Integer publishStatus) {
        // 获取当前操作人信息
        Long operatorId = LoginContextUtil.getUserId();
        String operatorName = LoginContextUtil.getUserName();
        // 商品小于100个直接处理
        if (productIds.size() < ASYNC_THRESHOLD) {
            PmsProductPublishVO result = productService.updatePublishStatusBatch(
                    productIds, publishStatus, operatorId, operatorName);
            return SmartPublishResultVO.sync(result, productIds.size());
        } else {
            String taskId = "TASK-" + System.currentTimeMillis();
            publishProductAsyncHandler.processAsync(productIds, publishStatus, taskId, operatorId, operatorName);
            return SmartPublishResultVO.async(taskId, productIds.size(), "任务已提交，请稍后查询", 1);
        }
    }

    @Override
    public TaskProgressVO getProgress(String taskId) {
        String key = "publish:progress:" + taskId;
        return (TaskProgressVO) redisService.get(key);
    }
}
