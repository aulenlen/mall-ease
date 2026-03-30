package com.mallease.marketing.service.flash.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mallease.common.api.R;
import com.mallease.common.constant.FlashRedisKeys;
import com.mallease.common.dto.remote.FlashRouteDTO;
import com.mallease.common.dto.remote.ProductDTO;
import com.mallease.common.dto.remote.SpuFlashOverlayDTO;
import com.mallease.common.enums.FlashRouteType;
import com.mallease.common.service.TypedRedisService;
import com.mallease.marketing.feign.ProductFeignClient;
import com.mallease.marketing.controller.admin.flash.vo.FlashProductRespVO;
import com.mallease.marketing.dal.entity.FlashProduct;
import com.mallease.marketing.dal.entity.FlashSession;
import com.mallease.marketing.service.flash.FlashService;
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

    private final TypedRedisService typedRedisService;
    private final FlashService flashService;
    private final ProductFeignClient productFeignClient;
    private final ObjectMapper objectMapper;

    // 对外缓存入口

    public void warmUpCurrentSession() {
        FlashSession currentSession = flashService.getCurrentSession();
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

        List<FlashProduct> flashProducts = flashService.listProductsBySessionIds(sessionIds);
        if (flashProducts == null || flashProducts.isEmpty()) {
            log.info("待预热场次无秒杀商品，sessionIds: {}", sessionIds);
            return;
        }

        Map<Long, List<FlashProduct>> productsBySession = flashProducts.stream()
                .filter(product -> product.getFlashSessionId() != null)
                .collect(Collectors.groupingBy(FlashProduct::getFlashSessionId));
        List<FlashProductRespVO> enrichedProducts = flashService.enrichWithSkuInfo(flashProducts);
        Map<Long, List<FlashProductRespVO>> enrichedProductsBySession = enrichedProducts.stream()
                .filter(product -> product.getFlashSessionId() != null)
                .collect(Collectors.groupingBy(FlashProductRespVO::getFlashSessionId));
        List<Long> hotSpuIds = flashProducts.stream()
                .filter(product -> FlashRouteType.isHot(product.getRouteType()))
                .map(FlashProduct::getSpuId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        Map<Long, ProductDTO> hotSnapshotMap = Collections.emptyMap();
        if (!hotSpuIds.isEmpty()) {
            hotSnapshotMap = loadProductSnapshotMap(hotSpuIds);
            if (hotSnapshotMap.isEmpty()) {
                log.warn("热点秒杀详情快照预热失败，将仅预热 route 与 overlay，sessionIds: {}, hotSpuIds: {}",
                        sessionIds, hotSpuIds);
            }
        }

        for (Map.Entry<Long, FlashSession> entry : sessionMap.entrySet()) {
            Long sessionId = entry.getKey();
            FlashSession session = entry.getValue();
            List<FlashProduct> sessionProducts = productsBySession.getOrDefault(sessionId, List.of());
            List<FlashProductRespVO> sessionEnrichedProducts = enrichedProductsBySession.getOrDefault(sessionId, List.of());
            warmUpSessionProducts(session, sessionProducts, sessionEnrichedProducts, hotSnapshotMap);
        }
    }

    public ProductDTO getHotDetail(Long sessionId, Long spuId) {
        ProductDTO snapshot = getHotDetailSnapshot(sessionId, spuId);
        if (snapshot == null) {
            return null;
        }
        SpuFlashOverlayDTO overlay = getOverlay(sessionId, spuId);
        if (overlay == null) {
            return deepCopyProduct(snapshot);
        }
        return mergeSnapshotWithOverlay(snapshot, overlay);
    }

    public SpuFlashOverlayDTO getOverlay(Long sessionId, Long spuId) {

        if (sessionId == null || spuId == null) {
            return null;
        }

        SpuFlashOverlayDTO cachedOverlay = typedRedisService.getJson(buildOverlayKey(sessionId, spuId), SpuFlashOverlayDTO.class);
        if (cachedOverlay != null) {
            return cachedOverlay;
        }

        SpuFlashOverlayDTO overlay = loadOverlayFromSource(sessionId, spuId);
        if (overlay == null) {
            return null;
        }

        FlashSession session = flashService.getSessionById(sessionId);
        typedRedisService.setJson(buildOverlayKey(sessionId, spuId), overlay, calcExpireSeconds(session.getEndTime()));
        return overlay;
    }

    // 回源加载

    private Map<Long, ProductDTO> loadProductSnapshotMap(List<Long> spuIds) {
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

    // 预热写入

    private void warmUpSessionProducts(FlashSession session, List<FlashProduct> sessionProducts,
                                       List<FlashProductRespVO> enrichedProducts,
                                       Map<Long, ProductDTO> hotSnapshotMap) {
        if (session == null || session.getId() == null || sessionProducts == null || sessionProducts.isEmpty()) {
            return;
        }

        long expireSeconds = calcExpireSeconds(session.getEndTime());

        Map<Long, List<FlashProduct>> productsBySpu = sessionProducts.stream()
                .filter(product -> product.getSpuId() != null)
                .collect(Collectors.groupingBy(FlashProduct::getSpuId));

        Map<Long, List<FlashProductRespVO>> productVoBySpu = enrichedProducts.stream()
                .filter(product -> product.getSpuId() != null)
                .collect(Collectors.groupingBy(FlashProductRespVO::getSpuId));

        int stockCount = warmUpFlashStock(session, enrichedProducts, expireSeconds);
        int overlayCount = warmUpRouteAndOverlay(session, productsBySpu, productVoBySpu, expireSeconds);
        int detailCount = warmUpHotDetail(session, productsBySpu, hotSnapshotMap, expireSeconds);

        log.info("秒杀缓存预热完成，sessionId: {}, spu数量: {}, overlay数量: {}, detail数量: {}, stock数量: {}",
                session.getId(), productsBySpu.size(), overlayCount, detailCount, stockCount);
    }

    private int warmUpRouteAndOverlay(FlashSession session, Map<Long, List<FlashProduct>> productsBySpu,
                                      Map<Long, List<FlashProductRespVO>> productVoBySpu,
                                      long expireSeconds) {
        Map<String, SpuFlashOverlayDTO> overlayMap = new HashMap<>();
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

        Map<String, FlashRouteDTO> routeUpdateMap = selectRoutesToUpsert(candidateRouteMap);
        if (!overlayMap.isEmpty()) {
            typedRedisService.multiSetJsonWithExpire(overlayMap, expireSeconds);
        }
        if (!routeUpdateMap.isEmpty()) {
            typedRedisService.multiSetJsonWithExpire(routeUpdateMap, expireSeconds);
        }
        return overlayMap.size();
    }

    private int warmUpFlashStock(FlashSession session, List<FlashProductRespVO> enrichedProducts, long expireSeconds) {
        if (session == null || session.getId() == null) {
            return 0;
        }

        Map<String, String> stockMap = buildFlashStockCacheMap(session.getId(), enrichedProducts);
        if (!stockMap.isEmpty()) {
            typedRedisService.multiSetStringWithExpire(stockMap, expireSeconds);
        }
        return stockMap.size();
    }

    private Map<String, String> buildFlashStockCacheMap(Long sessionId, List<FlashProductRespVO> enrichedProducts) {
        if (sessionId == null || enrichedProducts == null || enrichedProducts.isEmpty()) {
            return Collections.emptyMap();
        }

        return enrichedProducts.stream()
                .filter(product -> product.getSkuId() != null && product.getFlashStock() != null)
                .collect(Collectors.toMap(
                        product -> buildStockKey(sessionId, product.getSkuId()),
                        product -> String.valueOf(product.getFlashStock()),
                        (left, right) -> left
                ));
    }

    private int warmUpHotDetail(FlashSession session, Map<Long, List<FlashProduct>> productsBySpu,
                                Map<Long, ProductDTO> hotSnapshotMap, long expireSeconds) {

        if (hotSnapshotMap == null || hotSnapshotMap.isEmpty()) {
            return 0;
        }

        Map<String, ProductDTO> detailMap = new HashMap<>();
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
            typedRedisService.multiSetJsonWithExpire(detailMap, expireSeconds);
        }
        return detailMap.size();
    }

    private ProductDTO getHotDetailSnapshot(Long sessionId, Long spuId) {
        if (sessionId == null || spuId == null) {
            return null;
        }
        ProductDTO cachedSnapshot = typedRedisService.getJson(buildDetailKey(sessionId, spuId), ProductDTO.class);
        if (cachedSnapshot != null) {
            return cachedSnapshot;
        }

        ProductDTO snapshot = loadProductSnapshotMap(List.of(spuId)).get(spuId);
        if (snapshot == null) {
            return null;
        }

        FlashSession session = flashService.getSessionById(sessionId);
        typedRedisService.setJson(buildDetailKey(sessionId, spuId), snapshot, calcExpireSeconds(session.getEndTime()));
        return snapshot;
    }

    private SpuFlashOverlayDTO loadOverlayFromSource(Long sessionId, Long spuId) {
        FlashSession session = flashService.getSessionById(sessionId);
        if (session == null) {
            return null;
        }

        List<FlashProduct> products = flashService.listProductsBySessionAndSpuId(sessionId, spuId);
        if (products == null || products.isEmpty()) {
            return null;
        }

        List<FlashProductRespVO> productVOS = flashService.enrichWithSkuInfo(products);
        return buildOverlay(session, productVOS);
    }

    private SpuFlashOverlayDTO buildOverlay(FlashSession session, List<FlashProductRespVO> productVOS) {
        if (session == null || session.getId() == null || productVOS == null || productVOS.isEmpty()) {
            return null;
        }

        List<SpuFlashOverlayDTO.SkuFlashOverlayDTO> skuFlashList = productVOS.stream()
                .filter(product -> product.getSkuId() != null)
                .sorted(Comparator.comparing(FlashProductRespVO::getSort, Comparator.nullsLast(Integer::compareTo))
                        .thenComparing(FlashProductRespVO::getSkuId, Comparator.nullsLast(Long::compareTo)))
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

    private Map<String, FlashRouteDTO> selectRoutesToUpsert(Map<String, FlashRouteDTO> candidateRouteMap) {
        if (candidateRouteMap == null || candidateRouteMap.isEmpty()) {
            return Collections.emptyMap();
        }

        List<String> routeKeys = candidateRouteMap.keySet().stream().toList();
        Map<String, FlashRouteDTO> cachedRouteMap = typedRedisService.multiGetJson(routeKeys, FlashRouteDTO.class);
        Map<String, FlashRouteDTO> routeUpdateMap = new HashMap<>();

        for (String key : routeKeys) {
            FlashRouteDTO candidateRoute = candidateRouteMap.get(key);
            if (candidateRoute == null) {
                continue;
            }

            FlashRouteDTO existingRoute = cachedRouteMap.get(key);
            if (existingRoute != null && !shouldReplaceRoute(existingRoute, candidateRoute)) {
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

    // 缓存结果合并

    private ProductDTO mergeSnapshotWithOverlay(ProductDTO snapshot, SpuFlashOverlayDTO overlay) {
        ProductDTO merged = deepCopyProduct(snapshot);
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
        for (ProductDTO.SkuViewInfo skuView : merged.getSkuList()) {
            if (skuView == null || skuView.getSku() == null || skuView.getSku().getId() == null) {
                continue;
            }

            SpuFlashOverlayDTO.SkuFlashOverlayDTO overlaySku = overlaySkuMap.get(skuView.getSku().getId());
            if (overlaySku == null) {
                continue;
            }

            if (overlaySku.getCompareAtPrice() != null && skuView.getSku().getCompareAtPrice() == null) {
                skuView.getSku().setCompareAtPrice(overlaySku.getCompareAtPrice());
            }
            if (shouldUseFlashPrice(overlay, overlaySku, now)) {
                skuView.getSku().setPromotionPrice(overlaySku.getFlashPrice());
            }
            skuView.getSku().setDisplayPrice(resolveDisplayPrice(skuView.getSku()));
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
                .map(ProductDTO.SkuViewInfo::getSku)
                .filter(Objects::nonNull)
                .map(this::resolveDisplayPrice)
                .filter(Objects::nonNull)
                .toList();
        if (prices.isEmpty()) {
            return;
        }

        BigDecimal minPrice = prices.stream().min(BigDecimal::compareTo).orElse(null);
        BigDecimal maxPrice = prices.stream().max(BigDecimal::compareTo).orElse(null);

        if (product.getSale() == null) {
            product.setSale(new ProductDTO.SpuSaleInfo());
        }
        product.getSale().setMinPrice(minPrice);
        product.getSale().setMaxPrice(maxPrice);
    }

    private BigDecimal resolveDisplayPrice(ProductDTO.SkuInfo skuInfo) {
        if (skuInfo == null) {
            return null;
        }
        return skuInfo.getPromotionPrice() != null ? skuInfo.getPromotionPrice() : skuInfo.getBasePrice();
    }

    private ProductDTO deepCopyProduct(ProductDTO snapshot) {
        if (snapshot == null) {
            return null;
        }
        return objectMapper.convertValue(snapshot, ProductDTO.class);
    }

    // 缓存键与过期时间

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
