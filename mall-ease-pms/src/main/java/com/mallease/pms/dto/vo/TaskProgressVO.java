package com.mallease.pms.dto.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Aulen
 * @description: 任务进度对象
 * @create: 2025-11-19
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskProgressVO {
    private String taskId;
    private Integer total;
    private Integer processed = 0;
    private Integer success = 0;
    private Integer fail = 0;
    private Double percentage = 0.0;
    private String status = "PROCESSING";  // PROCESSING/COMPLETED/FAILED
    private List<PublishFailDetailVO> failList = new ArrayList<>();
    private Long startTime = System.currentTimeMillis();
    private Long endTime;

    public TaskProgressVO(String taskId, Integer total) {
        this.taskId = taskId;
        this.total = total;
    }

    public void update(PmsProductPublishVO result, int processed) {
        this.processed = processed;
        this.success += result.getSuccessCount();
        this.fail += result.getFailCount();
        this.failList.addAll(result.getFailDetails());
        this.percentage = 100.0 * processed / total;
    }

    public void complete() {
        this.status = "COMPLETED";
        this.endTime = System.currentTimeMillis();
    }

    public void updateChunkFailed(int processed, int chunkSize, String message) {
        this.processed = processed;
        this.fail += chunkSize;
        this.percentage = 100.0 * processed / total;

        PublishFailDetailVO failDetail = PublishFailDetailVO.builder().productId(0L).productName("Chunk处理失败").reason("批量处理异常: " + message).build();
        this.failList.add(failDetail);
    }
}
