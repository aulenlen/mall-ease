package com.mallease.pms.service;

import com.mallease.common.exception.Asserts;
import com.mallease.common.service.RedisService;
import com.mallease.pms.dto.vo.PmsProductPublishVO;
import com.mallease.pms.dto.vo.TaskProgressVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author: Aulen
 * @description:
 * @create: 2025-11-19 22:55
 **/
@Component
@Slf4j
public class PublishProductAsyncHandler {
    @Autowired
    private RedisService redisService;
    @Autowired
    private PmsProductService productService;

    @Async("taskExecutor")
    @Transactional(rollbackFor = Exception.class)
    public void processAsync(List<Long> productids, Integer publishStatus, String taskId) {

        try {
            TaskProgressVO taskProgressVO = new TaskProgressVO(taskId, productids.size());
            saveProgress(taskId, taskProgressVO);
            List<List<Long>> chunkList = partition(productids, 50);
            Integer processed = 0;

            for (List<Long> chunk : chunkList) {
                PmsProductPublishVO result = productService.updatePublishStatusBatch(chunk, publishStatus);

                processed += chunk.size();
                taskProgressVO.update(result, processed);
                saveProgress(taskId, taskProgressVO);
            }

            taskProgressVO.complete();
            saveProgress(taskId, taskProgressVO);
        } catch (Exception e) {
            log.error("异步上架商品任务失败 message: {}", e.getMessage());
            Asserts.fail("异步上架商品任务失败");
        }
    }

    private void saveProgress(String taskId, TaskProgressVO progress) {
        String key = "publish:progress:" + taskId;
        redisService.set(key, progress, 3600);
    }

    private List<List<Long>> partition(List<Long> ids, int chunkSize) {
        if (ids == null || ids.isEmpty()) {
            Asserts.fail("不存在上架商品");
        }

        return IntStream.range(0, (ids.size() + chunkSize - 1) / chunkSize)
                .mapToObj(i -> ids.subList(i * chunkSize, Math.min(i * chunkSize + chunkSize, ids.size())))
                .collect(Collectors.toList());
    }
}
