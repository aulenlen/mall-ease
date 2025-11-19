package com.mallease.pms.dto.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 智能批量上架统一返回对象，兼容同步与异步两种模式。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmartPublishResultVO {

    /**
     * 响应模式：SYNC 立即返回结果，ASYNC 只返回任务信息。
     */
    private PublishMode mode;

    /**
     * 待处理商品总数，方便前端展示。
     */
    private Integer total;

    /**
     * 同步模式下的处理结果，异步模式为 null。
     */
    private PmsProductPublishVO result;

    /**
     * 异步任务 ID，同步模式为 null。
     */
    private String taskId;

    /**
     * 提示信息，例如“任务已提交，请稍后查询”。
     */
    private String message;

    /**
     * 建议的轮询间隔（秒），便于前端节流，默认为 5 秒。
     */
    private Integer recommendPollInterval;

    public static SmartPublishResultVO sync(PmsProductPublishVO result, int total) {
        return SmartPublishResultVO.builder()
                .mode(PublishMode.SYNC)
                .total(total)
                .result(result)
                .message("处理完成")
                .recommendPollInterval(0)
                .build();
    }

    public static SmartPublishResultVO async(String taskId, int total, String message, Integer pollIntervalSeconds) {
        return SmartPublishResultVO.builder()
                .mode(PublishMode.ASYNC)
                .total(total)
                .taskId(taskId)
                .message(message)
                .recommendPollInterval(pollIntervalSeconds)
                .build();
    }

    public enum PublishMode {
        SYNC,
        ASYNC
    }
}
