package com.mallease.pms.service;

import com.mallease.common.exception.Asserts;
import com.mallease.common.service.RedisService;
import com.mallease.pms.dto.vo.PmsProductPublishVO;
import com.mallease.pms.dto.vo.TaskProgressVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

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
    public void processAsync(List<Long> productids, Integer publishStatus, String taskId, Long operatorId, String operatorName) {
        log.info("异步任务开始，线程: {}", Thread.currentThread().getName());
        TaskProgressVO taskProgressVO = new TaskProgressVO(taskId, productids.size());

        try {
            saveProgress(taskId, taskProgressVO);
        } catch (Exception e) {
            log.error("保存初始进度失败: {}", e.getMessage());
        }

        List<List<Long>> chunkList = partition(productids, 50);
        int processed = 0;

        for (List<Long> chunk : chunkList) {
            try {
                PmsProductPublishVO result = productService.updatePublishStatusBatch(chunk, publishStatus, operatorId, operatorName);

                processed += chunk.size();
                taskProgressVO.update(result, processed);

            } catch (Exception e) {
                log.error("chunk处理失败,继续下一个: {}", chunk, e);
                processed += chunk.size();
                taskProgressVO.updateChunkFailed(processed, chunk.size(), e.getMessage());
            }

            // Redis失败不影响业务
            try {
                saveProgress(taskId, taskProgressVO);
            } catch (Exception e) {
                log.error("保存进度失败，不影响业务: {}", e.getMessage());
            }
        }

        taskProgressVO.complete();
        try {
            saveProgress(taskId, taskProgressVO);
        } catch (Exception e) {
            log.error("保存最终进度失败: {}", e.getMessage());
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
