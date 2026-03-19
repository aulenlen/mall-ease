package com.mallease.gateway.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.mallease.common.dto.remote.FlashRouteDTO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Optional;

@Configuration
public class GatewayCacheConfig {

    @Bean
    public Cache<Long, Optional<FlashRouteDTO>> cache() {
        return Caffeine.newBuilder()
                .maximumSize(20000)
                .expireAfterWrite(Duration.ofSeconds(2))
                .build();
    }

}
