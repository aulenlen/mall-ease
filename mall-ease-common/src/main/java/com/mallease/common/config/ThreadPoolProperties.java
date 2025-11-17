package com.mallease.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 线程池配置属性
 *
 * @author: Aulen
 * @create: 2025-11-16
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "thread-pool")
public class ThreadPoolProperties {
    /**
     * 通用业务线程池
     */
    private ExecutorConfig taskExecutor = new ExecutorConfig();

    @Data
    public static class ExecutorConfig {
        /**
         * 核心线程数
         */
        private Integer corePoolSize = 4;

        /**
         * 最大线程数
         */
        private Integer maxPoolSize = 8;

        /**
         * 队列容量
         */
        private Integer queueCapacity = 200;

        /**
         * 线程空闲时间（秒）
         */
        private Integer keepAliveSeconds = 60;

        /**
         * 线程名称前缀
         */
        private String threadNamePrefix = "mall-task-";
    }
}
