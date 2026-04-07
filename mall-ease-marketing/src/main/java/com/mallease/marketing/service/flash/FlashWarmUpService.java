package com.mallease.marketing.service.flash;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mallease.common.api.R;
import com.mallease.common.dto.remote.ProductDTO;
import com.mallease.common.service.TypedRedisService;
import com.mallease.marketing.constant.FlashRedisKeys;
import com.mallease.marketing.controller.portal.flash.vo.FlashPortalProductRespVO;
import com.mallease.marketing.controller.portal.flash.vo.FlashPortalSessionRespVO;
import com.mallease.marketing.convert.FlashConvert;
import com.mallease.marketing.dal.entity.FlashProduct;
import com.mallease.marketing.dal.entity.FlashSession;
import com.mallease.marketing.feign.ProductFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FlashWarmUpService {
    private static final long CACHE_BUFFER_MINUTES = 15L;
    private static final int DEFAULT_ACTIVE_WINDOW_MINUTES = 15;

    private final TypedRedisService typedRedisService;
    private final FlashService flashService;
    private final FlashConvert flashConvert;
    private final ProductFeignClient productFeignClient;
    private final ObjectMapper objectMapper;
    private final FlashSnapshotBuilder flashSnapshotBuilder;

    public FlashWarmUpResult rebuildPortalCacheByDate(LocalDate date) {
        long startTime = System.currentTimeMillis();
        LocalDate targetDate = date != null ? date : LocalDate.now();
        List<FlashSession> sessions = filterPortalSessions(flashService.listPublishedSessions(targetDate.atStartOfDay()));
        if (sessions.isEmpty()) {
            log.info("当天无当前或未来可预热的秒杀场次，date: {}", targetDate);
            cacheEmptySessionIndex(targetDate);
            return buildResult("REBUILD_BY_DATE", List.of(), 0, 0, startTime, "当天无当前或未来场次，已清空索引缓存");
        }

        cacheSessionIndex(targetDate, sessions);
        cacheSessionMeta(sessions);

        Map<Long, FlashSession> sessionMap = sessions.stream()
                .filter(s -> s.getId() != null)
                .collect(Collectors.toMap(FlashSession::getId, s -> s, (l, r) -> l, LinkedHashMap::new));
        return refreshSessionsCache("REBUILD_BY_DATE", sessionMap, startTime, "按日期重建门户缓存完成");
    }

    public FlashWarmUpResult refreshSessionCache(Long sessionId) {
        if (sessionId == null) {
            throw new IllegalArgumentException("场次ID不能为空");
        }
        long startTime = System.currentTimeMillis();
        FlashSession session = flashService.getSessionById(sessionId);
        if (session == null || session.getId() == null) {
            return buildResult("REFRESH_SESSION", List.of(), 0, 0, startTime, "场次不存在，无需刷新");
        }
        if (isSessionExpired(session)) {
            return buildResult("REFRESH_SESSION", List.of(sessionId), 0, 0, startTime, "场次已结束，无需刷新");
        }

        cacheSessionMeta(List.of(session));
        cacheSessionOverview(session);
        return refreshSessionsCache(
                "REFRESH_SESSION",
                Collections.singletonMap(sessionId, session),
                startTime,
                "按场次刷新缓存完成"
        );
    }

    public FlashWarmUpResult refreshActiveSessionsCache(Integer windowMinutes) {
        long startTime = System.currentTimeMillis();
        int effectiveWindowMinutes = (windowMinutes == null || windowMinutes <= 0)
                ? DEFAULT_ACTIVE_WINDOW_MINUTES
                : windowMinutes;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime deadline = now.plusMinutes(effectiveWindowMinutes);

        Map<Long, FlashSession> sessions = new LinkedHashMap<>();
        FlashSession currentSession = flashService.getCurrentSession();
        if (currentSession != null && currentSession.getId() != null && !isSessionExpired(currentSession)) {
            sessions.put(currentSession.getId(), currentSession);
        }

        flashService.listSessionsToWarmUp(now, deadline).stream()
                .filter(Objects::nonNull)
                .filter(session -> session.getId() != null)
                .filter(session -> !isSessionExpired(session))
                .forEach(session -> sessions.putIfAbsent(session.getId(), session));

        if (sessions.isEmpty()) {
            log.info("当前及未来{}分钟内无待预热秒杀场次", effectiveWindowMinutes);
            return buildResult("REFRESH_ACTIVE", List.of(), 0, 0, startTime, "当前和未来窗口内无待预热场次");
        }

        List<FlashSession> sessionList = new ArrayList<>(sessions.values());
        cacheSessionMeta(sessionList);
        sessionList.forEach(this::cacheSessionOverview);
        return refreshSessionsCache("REFRESH_ACTIVE", sessions, startTime,
                "刷新当前和未来" + effectiveWindowMinutes + "分钟活跃场次缓存完成");
    }

    FlashWarmUpResult refreshCurrentSessionCache() {
        FlashSession currentSession = flashService.getCurrentSession();
        if (currentSession == null || currentSession.getId() == null || isSessionExpired(currentSession)) {
            long startTime = System.currentTimeMillis();
            return buildResult("REFRESH_CURRENT", List.of(), 0, 0, startTime, "当前无可预热的秒杀场次");
        }
        return refreshSessionCache(currentSession.getId());
    }

    FlashWarmUpResult refreshUpcomingSessionsCache() {
        return refreshActiveSessionsCache(DEFAULT_ACTIVE_WINDOW_MINUTES);
    }

    long calcExpireSeconds(LocalDateTime endTime) {
        if (endTime == null) {
            return 60L;
        }
        long seconds = Duration.between(LocalDateTime.now(), endTime.plusMinutes(CACHE_BUFFER_MINUTES)).getSeconds();
        return Math.max(seconds, 60L);
    }

    private void cacheSessionMeta(List<FlashSession> sessions) {
        for (FlashSession session : sessions) {
            try {
                String metaJson = objectMapper.writeValueAsString(Map.of(
                        "startTime", session.getStartTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                        "endTime", session.getEndTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                ));
                typedRedisService.setString(
                        FlashRedisKeys.sessionMetaKey(session.getId()),
                        metaJson,
                        calcExpireSeconds(session.getEndTime())
                );
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void cacheSessionOverview(FlashSession session) {
        if (session == null || session.getId() == null) {
            return;
        }
        FlashPortalSessionRespVO sessionResp = flashConvert.toFlashPortalSessionResp(session);
        if (sessionResp == null) {
            return;
        }
        sessionResp.setTimeStatus(flashConvert.resolveTimeStatus(sessionResp.getStartTime(), sessionResp.getEndTime()));
        typedRedisService.setJson(
                FlashRedisKeys.sessionKey(session.getId()),
                sessionResp,
                calcExpireSeconds(session.getEndTime())
        );
    }

    private void cacheSessionIndex(LocalDate date, List<FlashSession> sessions) {
        String key = FlashRedisKeys.sessionsKey(date.toString());
        if (sessions == null || sessions.isEmpty()) {
            cacheEmptySessionIndex(date);
            return;
        }

        List<Long> sessionIds = new ArrayList<>(sessions.size());
        for (FlashSession session : sessions) {
            if (session == null || session.getId() == null) {
                continue;
            }
            cacheSessionOverview(session);
            sessionIds.add(session.getId());
        }

        if (sessionIds.isEmpty()) {
            cacheEmptySessionIndex(date);
            return;
        }

        long expireSeconds = calcDayExpire(sessions);
        typedRedisService.setJson(key, sessionIds, expireSeconds);
        log.info("场次列表缓存预热完成，date: {}, 场次数: {}, TTL: {}s", date, sessionIds.size(), expireSeconds);
    }

    private FlashWarmUpResult refreshSessionsCache(String triggerType,
                                                   Map<Long, FlashSession> sessions,
                                                   long startTime,
                                                   String successMessage) {
        if (sessions == null || sessions.isEmpty()) {
            return buildResult(triggerType, List.of(), 0, 0, startTime, "无可预热场次");
        }

        List<Long> sessionIds = sessions.keySet().stream().filter(Objects::nonNull).toList();
        if (sessionIds.isEmpty()) {
            return buildResult(triggerType, List.of(), 0, 0, startTime, "无可预热场次");
        }

        List<FlashProduct> allProducts = flashService.listProductsBySessionIds(sessionIds);
        if (allProducts == null || allProducts.isEmpty()) {
            log.info("待预热场次无秒杀商品，sessionIds: {}", sessionIds);
            return buildResult(triggerType, sessionIds, 0, 0, startTime, "场次已刷新，但无秒杀商品需要预热");
        }

        Map<Long, List<FlashProduct>> productsBySession = allProducts.stream()
                .filter(p -> p.getFlashSessionId() != null)
                .collect(Collectors.groupingBy(FlashProduct::getFlashSessionId));

        for (Map.Entry<Long, FlashSession> entry : sessions.entrySet()) {
            Long sessionId = entry.getKey();
            FlashSession session = entry.getValue();
            List<FlashProduct> products = productsBySession.getOrDefault(sessionId, List.of());
            List<Long> spuIds = products.stream()
                    .filter(Objects::nonNull)
                    .map(FlashProduct::getSpuId)
                    .distinct()
                    .toList();

            long expireSeconds = calcExpireSeconds(session.getEndTime());
            Map<Long, ProductDTO> productSnapshots = loadProductSnapshotMap(spuIds);

            cacheSessionProducts(session, products, productSnapshots, expireSeconds);
            cacheDetailSnapshots(session, products, productSnapshots, expireSeconds);
            cacheSelectorSnapshots(session, products, productSnapshots, expireSeconds);
            cacheSessionStock(session, products, expireSeconds);
        }

        int spuCount = (int) allProducts.stream()
                .filter(Objects::nonNull)
                .map(FlashProduct::getSpuId)
                .filter(Objects::nonNull)
                .distinct()
                .count();
        int skuCount = (int) allProducts.stream().filter(Objects::nonNull).count();
        return buildResult(triggerType, sessionIds, spuCount, skuCount, startTime, successMessage);
    }

    private FlashWarmUpResult buildResult(String triggerType,
                                          List<Long> sessionIds,
                                          int spuCount,
                                          int skuCount,
                                          long startTime,
                                          String message) {
        List<Long> safeSessionIds = sessionIds == null ? List.of() : sessionIds;
        return FlashWarmUpResult.builder()
                .triggerType(triggerType)
                .sessionIds(safeSessionIds)
                .sessionCount(safeSessionIds.size())
                .spuCount(spuCount)
                .skuCount(skuCount)
                .costMs(System.currentTimeMillis() - startTime)
                .message(message)
                .build();
    }

    private void cacheSessionProducts(FlashSession session, List<FlashProduct> products,
                                      Map<Long, ProductDTO> productSnapshots, long expireSeconds) {
        if (products.isEmpty() || productSnapshots == null || productSnapshots.isEmpty()) {
            return;
        }

        List<FlashPortalProductRespVO> voList = flashSnapshotBuilder.buildProductListVO(products, productSnapshots);
        String key = FlashRedisKeys.productsKey(session.getId());
        typedRedisService.setJson(key, voList, expireSeconds);
        log.info("商品列表缓存预热完成，sessionId: {}, 商品数: {}", session.getId(), voList.size());
    }

    private void cacheDetailSnapshots(FlashSession session, List<FlashProduct> products,
                                      Map<Long, ProductDTO> productSnapshots, long expireSeconds) {
        if (products.isEmpty() || productSnapshots == null || productSnapshots.isEmpty()) {
            return;
        }
        Map<Long, List<FlashProduct>> productsBySpu = products.stream()
                .filter(p -> p.getSpuId() != null)
                .collect(Collectors.groupingBy(FlashProduct::getSpuId));

        HashMap<String, ProductDTO> detailCache = new HashMap<>(productSnapshots.size());
        for (Map.Entry<Long, ProductDTO> entry : productSnapshots.entrySet()) {
            Long spuId = entry.getKey();
            ProductDTO productDTO = entry.getValue();
            List<FlashProduct> spuFlashProducts = productsBySpu.getOrDefault(spuId, List.of());
            ProductDTO flashSnapshot = flashSnapshotBuilder.buildFlashProductSnapshot(productDTO, spuFlashProducts);
            if (flashSnapshot != null) {
                detailCache.put(FlashRedisKeys.detailKey(session.getId(), spuId), flashSnapshot);
            }
        }

        if (!detailCache.isEmpty()) {
            typedRedisService.multiSetJsonWithExpire(detailCache, expireSeconds);
        }
        log.info("详情快照缓存预热完成，sessionId: {}, SPU数量: {}", session.getId(), detailCache.size());
    }

    private void cacheSelectorSnapshots(FlashSession session, List<FlashProduct> products,
                                        Map<Long, ProductDTO> productSnapshots, long expireSeconds) {
        if (products.isEmpty() || productSnapshots == null || productSnapshots.isEmpty()) {
            return;
        }

        Map<Long, List<FlashProduct>> productsBySpu = products.stream()
                .filter(p -> p.getSpuId() != null)
                .collect(Collectors.groupingBy(FlashProduct::getSpuId));

        Map<String, com.mallease.marketing.controller.portal.flash.vo.FlashPortalSelectorRespVO> selectorCache = new HashMap<>(productSnapshots.size());
        for (Map.Entry<Long, ProductDTO> entry : productSnapshots.entrySet()) {
            Long spuId = entry.getKey();
            ProductDTO productDTO = entry.getValue();
            List<FlashProduct> spuFlashProducts = productsBySpu.getOrDefault(spuId, List.of());
            ProductDTO flashSnapshot = flashSnapshotBuilder.buildFlashProductSnapshot(productDTO, spuFlashProducts);
            if (flashSnapshot == null) {
                continue;
            }
            selectorCache.put(
                    FlashRedisKeys.selectorKey(session.getId(), spuId),
                    flashSnapshotBuilder.buildSelectorSnapshot(session.getId(), spuId, flashSnapshot)
            );
        }

        if (!selectorCache.isEmpty()) {
            typedRedisService.multiSetJsonWithExpire(selectorCache, expireSeconds);
        }
        log.info("选择器缓存预热完成，sessionId: {}, SPU数量: {}", session.getId(), selectorCache.size());
    }

    private void cacheSessionStock(FlashSession session, List<FlashProduct> products, long expireSeconds) {
        if (session == null || session.getId() == null || products.isEmpty()) {
            return;
        }

        HashMap<String, String> cacheValues = new HashMap<>();
        for (FlashProduct p : products) {
            if (p == null || p.getSkuId() == null) {
                continue;
            }
            cacheValues.put(FlashRedisKeys.stockKey(session.getId(), p.getSkuId()), String.valueOf(p.getFlashStock()));
            cacheValues.put(FlashRedisKeys.limitKey(session.getId(), p.getSkuId()), String.valueOf(p.getFlashLimit() != null ? p.getFlashLimit() : 0));
        }

        if (cacheValues.isEmpty()) {
            return;
        }
        typedRedisService.multiSetStringWithExpire(cacheValues, expireSeconds);
        log.info("库存与限购缓存预热完成，sessionId: {}, Key数量: {}", session.getId(), cacheValues.size());
    }

    private Map<Long, ProductDTO> loadProductSnapshotMap(List<Long> spuIds) {
        if (spuIds == null || spuIds.isEmpty()) {
            return Collections.emptyMap();
        }

        R<List<ProductDTO>> response = productFeignClient.listProductDetailSnapshots(spuIds);
        if (response == null || !response.isSuccess()) {
            log.warn("批量拉取商品详情快照失败，spuIds: {}, response: {}",
                    spuIds, response != null ? response.getMessage() : "null");
            return Collections.emptyMap();
        }

        List<ProductDTO> snapshots = response.getData();
        if (snapshots == null || snapshots.isEmpty()) {
            log.warn("批量拉取商品详情快照为空，spuIds: {}", spuIds);
            return Collections.emptyMap();
        }

        return snapshots.stream()
                .filter(s -> s.getId() != null)
                .collect(Collectors.toMap(ProductDTO::getId, s -> s, (l, r) -> l));
    }

    private long calcDayExpire(List<FlashSession> sessions) {
        return sessions.stream()
                .map(FlashSession::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .map(this::calcExpireSeconds)
                .orElse(60L);
    }

    private List<FlashSession> filterPortalSessions(List<FlashSession> sessions) {
        if (sessions == null || sessions.isEmpty()) {
            return List.of();
        }
        LocalDateTime now = LocalDateTime.now();
        return sessions.stream()
                .filter(Objects::nonNull)
                .filter(session -> session.getId() != null)
                .filter(session -> session.getStartTime() != null && session.getEndTime() != null)
                .filter(session -> !session.getEndTime().isBefore(now))
                .sorted(java.util.Comparator.comparing(FlashSession::getStartTime).thenComparing(FlashSession::getId))
                .toList();
    }

    private boolean isSessionExpired(FlashSession session) {
        return session == null || session.getEndTime() == null || session.getEndTime().isBefore(LocalDateTime.now());
    }

    private void cacheEmptySessionIndex(LocalDate date) {
        if (date == null) {
            return;
        }
        typedRedisService.setJson(FlashRedisKeys.sessionsKey(date.toString()), List.of(), 60L);
    }
}