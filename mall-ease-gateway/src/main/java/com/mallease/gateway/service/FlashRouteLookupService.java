package com.mallease.gateway.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.mallease.common.constant.FlashRedisKeys;
import com.mallease.common.dto.remote.FlashRouteDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class FlashRouteLookupService {
    /**
     * 1. route 什么时候覆盖旧值最合理
     * 2. 售罄后 route/overlay 什么时候回退
     * 3. 热点详情缓存什么时候失效
     */
    private final ReactiveStringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final Cache<Long, Optional<FlashRouteDTO>> flashRouteLocalCache;

    public Mono<FlashRouteDTO> getRoute(Long spuId) {
        Optional<FlashRouteDTO> localCache = flashRouteLocalCache.getIfPresent(spuId);
        if (localCache != null) {
            if (localCache.isEmpty()) {
                return Mono.empty();
            }
            FlashRouteDTO localRoute = localCache.get();
            if (!localRoute.isExpired(LocalDateTime.now())) {
                return Mono.just(localRoute);
            }
            flashRouteLocalCache.invalidate(spuId);
        }

        return getRouteFromRedis(spuId);
    }

    private Mono<FlashRouteDTO> getRouteFromRedis(Long spuId) {
        Mono<String> routeCache = redisTemplate.opsForValue().get(FlashRedisKeys.routeKey(spuId));

        return routeCache.flatMap(json -> {
            FlashRouteDTO route;
            try {
                route = objectMapper.readValue(json, FlashRouteDTO.class);
            } catch (Exception e) {
                log.warn("解析秒杀路由失败，spuId: {}, json: {}", spuId, json, e);
                flashRouteLocalCache.invalidate(spuId);
                return Mono.empty();
            }

            if (route == null) {
                flashRouteLocalCache.put(spuId, Optional.empty());
                return Mono.empty();
            }

            if (route.getRouteType() == null) {
                log.warn("秒杀路由缺少routeType，spuId: {}, json: {}", spuId, json);
                flashRouteLocalCache.put(spuId, Optional.empty());
                return Mono.empty();
            }

            if (route.isExpired(LocalDateTime.now())) {
                flashRouteLocalCache.put(spuId, Optional.empty());
                return Mono.empty();
            }

            flashRouteLocalCache.put(spuId, Optional.of(route));

            return Mono.just(route);
        }).switchIfEmpty(Mono.defer(() -> {
            flashRouteLocalCache.put(spuId, Optional.empty());
            return Mono.empty();
        })).onErrorResume(e -> {
            log.warn("读取秒杀路由失败，spuId: {}", spuId, e);
            flashRouteLocalCache.invalidate(spuId);
            return Mono.empty();
        });
    }
}
