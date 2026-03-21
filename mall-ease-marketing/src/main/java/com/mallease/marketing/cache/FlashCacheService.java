package com.mallease.marketing.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mallease.common.api.R;
import com.mallease.common.constant.FlashRedisKeys;
import com.mallease.common.dto.remote.FlashRouteDTO;
import com.mallease.common.dto.remote.ProductDTO;
import com.mallease.common.dto.remote.SpuFlashOverlayDTO;
import com.mallease.common.enums.FlashRouteType;
import com.mallease.common.service.RedisService;
import com.mallease.marketing.feign.ProductFeignClient;
import com.mallease.marketing.model.client.query.FlashProductQuery;
import com.mallease.marketing.model.client.vo.FlashProductVO;
import com.mallease.marketing.model.data.entity.FlashProduct;
import com.mallease.marketing.model.data.entity.FlashSession;
import com.mallease.marketing.service.FlashService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FlashCacheService {
    private static final long CACHE_BUFFER_MINUTES = 15L;

    private final RedisService redisService;
    private final FlashService flashService;
    private final ProductFeignClient productFeignClient;
    private final ObjectMapper objectMapper;

    public void warmUpCurrentSession() {
        FlashSession currentSession = flashService.getCurrentFlashSessions();
        if (currentSession == null || currentSession.getId() == null) {
            log.info("当前无可预热的秒杀场次");
            return;
        }
        warmUpSessions(Collections.singletonMap(currentSession.getId(), currentSession));
    }

    public void warmUpUpcomingSessions() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime deadline = now.plusMinutes(CACHE_BUFFER_MINUTES);

        Map<Long, FlashSession> sessionMap = flashService.listSessionsToWarmUp(now, deadline).stream()
                .filter(session -> session.getId() != null)
                .collect(Collectors.toMap(FlashSession::getId, session -> session, (left, right) -> left));

        if (sessionMap.isEmpty()) {
            log.info("未来15分钟内无待预热秒杀场次");
            return;
        }

        warmUpSessions(sessionMap);
    }

    public void warmUpSessions(Map<Long, FlashSession> sessionMap) {

        if (sessionMap == null || sessionMap.isEmpty()) {
            return;
        }

        List<Long> sessionIds = sessionMap.keySet().stream().filter(Objects::nonNull).toList();
        if (sessionIds.isEmpty()) {
            return;
        }

        List<FlashProduct> flashProducts = flashService.listFlashProductBySessionIds(sessionIds);
        if (flashProducts == null || flashProducts.isEmpty()) {
            log.info("待预热场次无秒杀商品，sessionIds: {}", sessionIds);
            return;
        }

        Map<Long, List<FlashProduct>> productsBySession = flashProducts.stream()
                .filter(product -> product.getFlashSessionId() != null)
                .collect(Collectors.groupingBy(FlashProduct::getFlashSessionId));
        List<FlashProductVO> enrichedProducts = flashService.enrichWithSkuInfo(flashProducts);
        Map<Long, List<FlashProductVO>> enrichedProductsBySession = enrichedProducts.stream()
                .filter(product -> product.getFlashSessionId() != null)
                .collect(Collectors.groupingBy(FlashProductVO::getFlashSessionId));
        List<Long> hotSpuIds = flashProducts.stream()
                .filter(product -> FlashRouteType.isHot(product.getRouteType()))
                .map(FlashProduct::getSpuId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        Map<Long, ProductDTO> hotSnapshotMap = Collections.emptyMap();
        if (!hotSpuIds.isEmpty()) {
            hotSnapshotMap = loadSnapshotMap(hotSpuIds);
            if (hotSnapshotMap.isEmpty()) {
                log.warn("热点秒杀详情快照预热失败，将仅预热 route 与 overlay，sessionIds: {}, hotSpuIds: {}",
                        sessionIds, hotSpuIds);
            }
        }

        for (Map.Entry<Long, FlashSession> entry : sessionMap.entrySet()) {
            Long sessionId = entry.getKey();
            FlashSession session = entry.getValue();
            List<FlashProduct> sessionProducts = productsBySession.getOrDefault(sessionId, List.of());
            List<FlashProductVO> sessionEnrichedProducts = enrichedProductsBySession.getOrDefault(sessionId, List.of());
            warmUpProducts(session, sessionProducts, sessionEnrichedProducts, hotSnapshotMap);
        }
    }

    public ProductDTO getHotDetail(Long sessionId, Long spuId) {
        ProductDTO snapshot = getHotSnapshot(sessionId, spuId);
        if (snapshot == null) {
            return null;
        }
        SpuFlashOverlayDTO overlay = getOverlay(sessionId, spuId);
        if (overlay == null) {
            return copyProduct(snapshot);
        }
        return mergeSnapshotWithOverlay(snapshot, overlay);
    }

    public SpuFlashOverlayDTO getOverlay(Long sessionId, Long spuId) {

        if (sessionId == null || spuId == null) {
            return null;
        }

        Object cached = redisService.get(buildOverlayKey(sessionId, spuId));
        if (cached instanceof SpuFlashOverlayDTO overlayDTO) {
            return overlayDTO;
        }

        SpuFlashOverlayDTO overlay = loadOverlayFromSource(sessionId, spuId);
        if (overlay == null) {
            return null;
        }

        FlashSession session = flashService.getFlashSessionById(sessionId);
        redisService.set(buildOverlayKey(sessionId, spuId), overlay, calcExpireSeconds(session.getEndTime()));
        return overlay;
    }

    private Map<Long, ProductDTO> loadSnapshotMap(List<Long> spuIds) {
        if (spuIds == null || spuIds.isEmpty()) {
            return Collections.emptyMap();
        }

        R<List<ProductDTO>> snapshotResponse = productFeignClient.listProductDetailSnapshots(spuIds);
        if (snapshotResponse == null || !snapshotResponse.isSuccess()) {
            log.warn("批量拉取商品详情快照失败，spuIds: {}, response: {}", spuIds, snapshotResponse != null ? snapshotResponse.getMessage() : "null");
            return Collections.emptyMap();
        }

        List<ProductDTO> productSnapshots = snapshotResponse.getData();
        if (productSnapshots == null || productSnapshots.isEmpty()) {
            log.warn("批量拉取商品详情快照为空，spuIds: {}", spuIds);
            return Collections.emptyMap();
        }

        return productSnapshots.stream().filter(snapshot -> snapshot.getId() != null)
                .collect(Collectors.toMap(ProductDTO::getId, snapshot -> snapshot, (left, right) -> left));
    }

    private void warmUpProducts(FlashSession session, List<FlashProduct> sessionProducts,
                                List<FlashProductVO> enrichedProducts,
                                Map<Long, ProductDTO> hotSnapshotMap) {
        if (session == null || session.getId() == null || sessionProducts == null || sessionProducts.isEmpty()) {
            return;
        }

        long expireSeconds = calcExpireSeconds(session.getEndTime());

        Map<Long, List<FlashProduct>> productsBySpu = sessionProducts.stream()
                .filter(product -> product.getSpuId() != null)
                .collect(Collectors.groupingBy(FlashProduct::getSpuId));

        Map<Long, List<FlashProductVO>> productVoBySpu = enrichedProducts.stream()
                .filter(product -> product.getSpuId() != null)
                .collect(Collectors.groupingBy(FlashProductVO::getSpuId));

        int stockCount = warmUpFlashStock(session, enrichedProducts, expireSeconds);
        int overlayCount = warmUpRouteAndOverlay(session, productsBySpu, productVoBySpu, expireSeconds);
        int detailCount = warmUpHotDetail(session, productsBySpu, hotSnapshotMap, expireSeconds);

        log.info("秒杀缓存预热完成，sessionId: {}, spu数量: {}, overlay数量: {}, detail数量: {}, stock数量: {}",
                session.getId(), productsBySpu.size(), overlayCount, detailCount, stockCount);
    }

    private int warmUpRouteAndOverlay(FlashSession session, Map<Long, List<FlashProduct>> productsBySpu,
                                      Map<Long, List<FlashProductVO>> productVoBySpu,
                                      long expireSeconds) {
        Map<String, Object> overlayMap = new HashMap<>();
        Map<String, FlashRouteDTO> candidateRouteMap = new HashMap<>();

        for (Map.Entry<Long, List<FlashProduct>> entry : productsBySpu.entrySet()) {
            Long spuId = entry.getKey();
            SpuFlashOverlayDTO overlay = buildOverlay(session, productVoBySpu.getOrDefault(spuId, List.of()));
            if (overlay == null) {
                continue;
            }

            overlayMap.put(buildOverlayKey(session.getId(), spuId), overlay);
            candidateRouteMap.put(buildRouteKey(spuId), buildRoute(session, entry.getValue()));
        }

        Map<String, Object> cacheMap = new HashMap<>(overlayMap);
        cacheMap.putAll(selectRoutesToUpsert(candidateRouteMap));
        if (!cacheMap.isEmpty()) {
            redisService.multiSetWithExpire(cacheMap, expireSeconds);
        }
        return overlayMap.size();
    }

    private int warmUpFlashStock(FlashSession session, List<FlashProductVO> enrichedProducts, long expireSeconds) {
        if (session == null || session.getId() == null) {
            return 0;
        }

        Map<String, Object> stockMap = buildFlashStockCacheMap(session.getId(), enrichedProducts);
        if (!stockMap.isEmpty()) {
            redisService.multiSetWithExpire(stockMap, expireSeconds);
        }
        return stockMap.size();
    }

    private Map<String, Object> buildFlashStockCacheMap(Long sessionId, List<FlashProductVO> enrichedProducts) {
        if (sessionId == null || enrichedProducts == null || enrichedProducts.isEmpty()) {
            return Collections.emptyMap();
        }

        return enrichedProducts.stream()
                .filter(product -> product.getSkuId() != null && product.getFlashStock() != null)
                .collect(Collectors.toMap(
                        product -> buildStockKey(sessionId, product.getSkuId()),
                        FlashProductVO::getFlashStock,
                        (left, right) -> left
                ));
    }

    private int warmUpHotDetail(FlashSession session, Map<Long, List<FlashProduct>> productsBySpu,
                                Map<Long, ProductDTO> hotSnapshotMap, long expireSeconds) {

        if (hotSnapshotMap == null || hotSnapshotMap.isEmpty()) {
            return 0;
        }

        Map<String, Object> detailMap = new HashMap<>();
        for (Map.Entry<Long, List<FlashProduct>> entry : productsBySpu.entrySet()) {
            if (!isHotRoute(entry.getValue())) {
                continue;
            }

            Long spuId = entry.getKey();
            ProductDTO snapshot = hotSnapshotMap.get(spuId);
            if (snapshot == null) {
                log.warn("热点秒杀详情预热缺少商品快照，sessionId: {}, spuId: {}", session.getId(), spuId);
                continue;
            }
            detailMap.put(buildDetailKey(session.getId(), spuId), snapshot);
        }

        if (!detailMap.isEmpty()) {
            redisService.multiSetWithExpire(detailMap, expireSeconds);
        }
        return detailMap.size();
    }

    private ProductDTO getHotSnapshot(Long sessionId, Long spuId) {
        if (sessionId == null || spuId == null) {
            return null;
        }
        Object cached = redisService.get(buildDetailKey(sessionId, spuId));
        if (cached instanceof ProductDTO productDTO) {
            return productDTO;
        }

        ProductDTO snapshot = loadSnapshotMap(List.of(spuId)).get(spuId);
        if (snapshot == null) {
            return null;
        }

        FlashSession session = flashService.getFlashSessionById(sessionId);
        redisService.set(buildDetailKey(sessionId, spuId), snapshot, calcExpireSeconds(session.getEndTime()));
        return snapshot;
    }

    private SpuFlashOverlayDTO loadOverlayFromSource(Long sessionId, Long spuId) {
        FlashSession session = flashService.getFlashSessionById(sessionId);
        if (session == null) {
            return null;
        }

        FlashProductQuery query = new FlashProductQuery();
        query.setSessionId(sessionId);
        query.setSpuId(spuId);

        List<FlashProduct> products = flashService.listFlashProducts(query);
        if (products == null || products.isEmpty()) {
            return null;
        }

        List<FlashProductVO> productVOS = flashService.enrichWithSkuInfo(products);
        return buildOverlay(session, productVOS);
    }

    private SpuFlashOverlayDTO buildOverlay(FlashSession session, List<FlashProductVO> productVOS) {
        if (session == null || session.getId() == null || productVOS == null || productVOS.isEmpty()) {
            return null;
        }

        List<SpuFlashOverlayDTO.SkuFlashOverlayDTO> skuFlashList = productVOS.stream()
                .filter(product -> product.getSkuId() != null)
                .sorted(Comparator.comparing(FlashProductVO::getSort, Comparator.nullsLast(Integer::compareTo))
                        .thenComparing(FlashProductVO::getSkuId, Comparator.nullsLast(Long::compareTo)))
                .map(product -> SpuFlashOverlayDTO.SkuFlashOverlayDTO.builder()
                        .skuId(product.getSkuId())
                        .compareAtPrice(product.getCompareAtPrice())
                        .flashPrice(product.getFlashPrice())
                        .flashStock(product.getFlashStock())
                        .flashLimit(product.getFlashLimit())
                        .build())
                .toList();

        if (skuFlashList.isEmpty()) {
            return null;
        }

        return SpuFlashOverlayDTO.builder()
                .sessionId(session.getId())
                .spuId(productVOS.get(0).getSpuId())
                .startTime(session.getStartTime())
                .endTime(session.getEndTime())
                .skuFlashList(skuFlashList)
                .build();
    }

    private FlashRouteDTO buildRoute(FlashSession session, List<FlashProduct> products) {
        Integer routeType = products.stream()
                .map(FlashProduct::getRouteType)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(FlashRouteType.NORMAL.getCode());

        return FlashRouteDTO.builder()
                .sessionId(session.getId())
                .routeType(routeType)
                .startTime(session.getStartTime())
                .endTime(session.getEndTime())
                .build();
    }

    private Map<String, Object> selectRoutesToUpsert(Map<String, FlashRouteDTO> candidateRouteMap) {
        if (candidateRouteMap == null || candidateRouteMap.isEmpty()) {
            return Collections.emptyMap();
        }

        List<String> routeKeys = candidateRouteMap.keySet().stream().toList();
        List<Object> cachedRoutes = redisService.multiGet(routeKeys);
        Map<String, Object> routeUpdateMap = new HashMap<>();

        for (int i = 0; i < routeKeys.size(); i++) {
            String key = routeKeys.get(i);
            FlashRouteDTO candidateRoute = candidateRouteMap.get(key);
            if (candidateRoute == null) {
                continue;
            }

            Object cached = cachedRoutes != null && i < cachedRoutes.size() ? cachedRoutes.get(i) : null;
            if (cached instanceof FlashRouteDTO existingRoute && !shouldReplaceRoute(existingRoute, candidateRoute)) {
                continue;
            }
            routeUpdateMap.put(key, candidateRoute);
        }

        return routeUpdateMap;
    }

    private boolean shouldReplaceRoute(FlashRouteDTO existingRoute, FlashRouteDTO candidateRoute) {
        LocalDateTime now = LocalDateTime.now();
        if (existingRoute == null) {
            return true;
        }
        if (existingRoute.getEndTime() != null && existingRoute.getEndTime().isBefore(now)) {
            return true;
        }

        boolean existingOngoing = isRouteOngoing(existingRoute, now);
        boolean candidateOngoing = isRouteOngoing(candidateRoute, now);
        if (candidateOngoing != existingOngoing) {
            return candidateOngoing;
        }

        LocalDateTime existingStart = existingRoute.getStartTime();
        LocalDateTime candidateStart = candidateRoute.getStartTime();
        if (existingStart == null) {
            return true;
        }
        if (candidateStart == null) {
            return false;
        }
        return candidateStart.isBefore(existingStart);
    }

    private boolean isRouteOngoing(FlashRouteDTO route, LocalDateTime now) {
        if (route == null || route.getStartTime() == null || route.getEndTime() == null) {
            return false;
        }
        return !now.isBefore(route.getStartTime()) && now.isBefore(route.getEndTime());
    }

    private boolean isHotRoute(List<FlashProduct> products) {
        return products.stream()
                .map(FlashProduct::getRouteType)
                .filter(Objects::nonNull)
                .findFirst()
                .map(FlashRouteType::isHot)
                .orElse(false);
    }

    private ProductDTO mergeSnapshotWithOverlay(ProductDTO snapshot, SpuFlashOverlayDTO overlay) {
        ProductDTO merged = copyProduct(snapshot);
        if (merged == null
                || merged.getSkuList() == null
                || merged.getSkuList().isEmpty()
                || overlay == null
                || overlay.getSkuFlashList() == null
                || overlay.getSkuFlashList().isEmpty()) {
            return merged;
        }

        Map<Long, SpuFlashOverlayDTO.SkuFlashOverlayDTO> overlaySkuMap = overlay.getSkuFlashList().stream()
                .filter(sku -> sku.getSkuId() != null)
                .collect(Collectors.toMap(SpuFlashOverlayDTO.SkuFlashOverlayDTO::getSkuId, sku -> sku, (left, right) -> left));

        LocalDateTime now = LocalDateTime.now();
        for (ProductDTO.SkuInfo skuInfo : merged.getSkuList()) {
            if (skuInfo == null || skuInfo.getBasic() == null || skuInfo.getBasic().getId() == null) {
                continue;
            }

            SpuFlashOverlayDTO.SkuFlashOverlayDTO overlaySku = overlaySkuMap.get(skuInfo.getBasic().getId());
            if (overlaySku == null) {
                continue;
            }

            if (skuInfo.getPrice() == null) {
                skuInfo.setPrice(new ProductDTO.SkuPriceInfo());
            }
            if (overlaySku.getCompareAtPrice() != null && skuInfo.getPrice().getCompareAtPrice() == null) {
                skuInfo.getPrice().setCompareAtPrice(overlaySku.getCompareAtPrice());
            }

            if (shouldUseFlashPrice(overlay, overlaySku, now)) {
                skuInfo.getPrice().setPromotionPrice(overlaySku.getFlashPrice());
            }
        }

        refreshSpuPriceRange(merged);
        return merged;
    }

    private boolean shouldUseFlashPrice(SpuFlashOverlayDTO overlay,
                                        SpuFlashOverlayDTO.SkuFlashOverlayDTO overlaySku,
                                        LocalDateTime now) {
        if (overlay == null || overlaySku == null || overlaySku.getFlashPrice() == null) {
            return false;
        }
        if (overlaySku.getFlashStock() != null && overlaySku.getFlashStock() <= 0) {
            return false;
        }
        if (overlay.getStartTime() != null && now.isBefore(overlay.getStartTime())) {
            return false;
        }
        return overlay.getEndTime() == null || now.isBefore(overlay.getEndTime());
    }

    private void refreshSpuPriceRange(ProductDTO product) {
        if (product == null || product.getSkuList() == null || product.getSkuList().isEmpty()) {
            return;
        }

        List<BigDecimal> prices = product.getSkuList().stream()
                .map(ProductDTO.SkuInfo::getPrice)
                .filter(Objects::nonNull)
                .map(this::resolveDisplayPrice)
                .filter(Objects::nonNull)
                .toList();
        if (prices.isEmpty()) {
            return;
        }

        BigDecimal minPrice = prices.stream().min(BigDecimal::compareTo).orElse(null);
        BigDecimal maxPrice = prices.stream().max(BigDecimal::compareTo).orElse(null);

        if (product.getSpuDetail() == null) {
            product.setSpuDetail(new ProductDTO.SpuDetailInfo());
        }
        product.getSpuDetail().setMinPrice(minPrice);
        product.getSpuDetail().setMaxPrice(maxPrice);
    }

    private BigDecimal resolveDisplayPrice(ProductDTO.SkuPriceInfo priceInfo) {
        if (priceInfo == null) {
            return null;
        }
        return priceInfo.getPromotionPrice() != null ? priceInfo.getPromotionPrice() : priceInfo.getBasePrice();
    }

    private ProductDTO copyProduct(ProductDTO snapshot) {
        if (snapshot == null) {
            return null;
        }
        return objectMapper.convertValue(snapshot, ProductDTO.class);
    }

    private long calcExpireSeconds(LocalDateTime endTime) {
        if (endTime == null) {
            return 60L;
        }
        long seconds = Duration.between(LocalDateTime.now(), endTime.plusMinutes(CACHE_BUFFER_MINUTES)).getSeconds();
        return Math.max(seconds, 60L);
    }

    private String buildRouteKey(Long spuId) {
        return FlashRedisKeys.routeKey(spuId);
    }

    private String buildOverlayKey(Long sessionId, Long spuId) {
        return FlashRedisKeys.overlayKey(sessionId, spuId);
    }

    private String buildDetailKey(Long sessionId, Long spuId) {
        return FlashRedisKeys.detailKey(sessionId, spuId);
    }

    private String buildStockKey(Long sessionId, Long skuId) {
        return FlashRedisKeys.stockKey(sessionId, skuId);
    }
}
