package com.mallease.marketing.service.flash;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.mallease.common.api.R;
import com.mallease.common.dto.remote.ProductDTO;
import com.mallease.common.exception.ApiException;
import com.mallease.common.service.TypedRedisService;
import com.mallease.marketing.constant.FlashRedisKeys;
import com.mallease.marketing.controller.portal.flash.vo.FlashPortalDetailRespVO;
import com.mallease.marketing.controller.portal.flash.vo.FlashPortalProductRespVO;
import com.mallease.marketing.controller.portal.flash.vo.FlashPortalSelectorRespVO;
import com.mallease.marketing.controller.portal.flash.vo.FlashPortalSessionRespVO;
import com.mallease.marketing.controller.portal.flash.vo.FlashPortalSessionsRespVO;
import com.mallease.marketing.controller.portal.flash.vo.FlashPortalSkuSelectedRespVO;
import com.mallease.marketing.convert.FlashConvert;
import com.mallease.marketing.dal.entity.FlashProduct;
import com.mallease.marketing.dal.entity.FlashSession;
import com.mallease.marketing.feign.ProductFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FlashPreheatService {
    private static final long CACHE_BUFFER_MINUTES = 15L;

    private final TypedRedisService typedRedisService;
    private final FlashService flashService;
    private final FlashConvert flashConvert;
    private final ProductFeignClient productFeignClient;
    private final ObjectMapper objectMapper;
    
    public void warmUpTodayFlashData(LocalDate date) {
        List<FlashSession> sessions = flashService.listPublishedSessions(date.atStartOfDay());
        if (sessions == null || sessions.isEmpty()) {
            log.info("当天无可预热的秒杀场次，date: {}", date);
            return;
        }

        // 场次列表缓存
        warmUpSessionList(date, sessions);

        // 商品列表 + 详情快照 + 规格选择器 + 库存
        Map<Long, FlashSession> sessionMap = sessions.stream()
                .filter(s -> s.getId() != null)
                .collect(Collectors.toMap(FlashSession::getId, s -> s, (l, r) -> l));
        warmUpSessions(sessionMap);
    }

    public FlashPortalSessionsRespVO getPortalSessions(LocalDate date) {
        if (date == null) {
            return FlashPortalSessionsRespVO.builder()
                    .serverTime(System.currentTimeMillis())
                    .sessions(List.of())
                    .build();
        }

        String key = FlashRedisKeys.sessionsKey(date.toString());
        List<FlashPortalSessionRespVO> cached = getJsonList(key, new TypeReference<List<FlashPortalSessionRespVO>>() {});
        if (cached != null) {
            return FlashPortalSessionsRespVO.builder()
                    .serverTime(System.currentTimeMillis())
                    .sessions(cached)
                    .build();
        }

        warmUpTodayFlashData(date);
        List<FlashPortalSessionRespVO> reloaded = getJsonList(key, new TypeReference<List<FlashPortalSessionRespVO>>() {});
        return FlashPortalSessionsRespVO.builder()
                .serverTime(System.currentTimeMillis())
                .sessions(reloaded != null ? reloaded : List.of())
                .build();
    }

    public List<FlashPortalProductRespVO> getPortalProducts(Long sessionId) {
        if (sessionId == null) {
            return List.of();
        }

        String key = FlashRedisKeys.productsKey(sessionId);
        List<FlashPortalProductRespVO> cached = getJsonList(key, new TypeReference<List<FlashPortalProductRespVO>>() {});
        if (cached != null) {
            return cached;
        }

        FlashSession session = flashService.getSessionById(sessionId);
        if (session == null || session.getId() == null) {
            return List.of();
        }

        warmUpSessions(Collections.singletonMap(sessionId, session));
        List<FlashPortalProductRespVO> reloaded = getJsonList(key, new TypeReference<List<FlashPortalProductRespVO>>() {});
        return reloaded != null ? reloaded : List.of();
    }
    
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

        Map<Long, FlashSession> sessions = flashService.listSessionsToWarmUp(now, deadline).stream()
                .filter(session -> session.getId() != null)
                .collect(Collectors.toMap(FlashSession::getId, session -> session, (l, r) -> l));

        if (sessions.isEmpty()) {
            log.info("未来15分钟内无待预热秒杀场次");
            return;
        }

        warmUpSessions(sessions);
    }
    
    private void warmUpSessions(Map<Long, FlashSession> sessions) {
        if (sessions == null || sessions.isEmpty()) {
            return;
        }

        List<Long> sessionIds = sessions.keySet().stream().filter(Objects::nonNull).toList();
        if (sessionIds.isEmpty()) {
            return;
        }

        List<FlashProduct> allProducts = flashService.listProductsBySessionIds(sessionIds);
        if (allProducts == null || allProducts.isEmpty()) {
            log.info("待预热场次无秒杀商品，sessionIds: {}", sessionIds);
            return;
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
                    .distinct().toList();

            long expireSeconds = calcExpireSeconds(session.getEndTime());
            Map<Long, ProductDTO> productSnapshots = loadProductSnapshotMap(spuIds);

            warmUpProductList(session, products, productSnapshots, expireSeconds);
            warmUpDetailSnapshots(session, products, productSnapshots, expireSeconds);
            warmUpSelectorSnapshots(session, products, productSnapshots, expireSeconds);
            warmUpStock(session, products, expireSeconds);
        }
    }
    
    private void warmUpSessionList(LocalDate date, List<FlashSession> sessions) {

        List<FlashPortalSessionRespVO> voList = flashConvert.toFlashPortalSessionRespList(sessions);

        String key = FlashRedisKeys.sessionsKey(date.toString());

        long expireSeconds = calcDayExpire(sessions);

        typedRedisService.setJson(key, voList, expireSeconds);

        log.info("场次列表缓存预热完成，date: {}, 场次数: {}, TTL: {}s", date, voList.size(), expireSeconds);
    }
    
    private long calcDayExpire(List<FlashSession> sessions) {
        return sessions.stream()
                .map(FlashSession::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .map(this::calcExpireSeconds)
                .orElse(60L);
    }
    
    private void warmUpProductList(FlashSession session, List<FlashProduct> products,
                                   Map<Long, ProductDTO> productSnapshots, long expireSeconds) {
        if (products.isEmpty() || productSnapshots == null || productSnapshots.isEmpty()) {
            return;
        }

        List<FlashPortalProductRespVO> voList = buildProductListVO(products, productSnapshots);
        String key = FlashRedisKeys.productsKey(session.getId());
        typedRedisService.setJson(key, voList, expireSeconds);
        log.info("商品列表缓存预热完成，sessionId: {}, 商品数: {}", session.getId(), voList.size());
    }
    
    private List<FlashPortalProductRespVO> buildProductListVO(List<FlashProduct> products,
                                                              Map<Long, ProductDTO> productSnapshots) {
        Map<Long, List<FlashProduct>> productsBySpu = products.stream()
                .filter(p -> p.getSpuId() != null)
                .collect(Collectors.groupingBy(FlashProduct::getSpuId));

        return productsBySpu.entrySet().stream()
                .sorted(Comparator.<Map.Entry<Long, List<FlashProduct>>, Integer>comparing(entry -> resolveSortValue(entry.getValue()))
                        .thenComparing(Map.Entry::getKey, Comparator.nullsLast(Long::compareTo)))
                .map(entry -> {
                    Long spuId = entry.getKey();
                    ProductDTO snapshot = productSnapshots.get(spuId);
                    ProductDTO flashSnapshot = buildFlashProductSnapshot(snapshot, entry.getValue());
                    if (flashSnapshot == null) {
                        return null;
                    }

                    ProductDTO.SkuViewInfo defaultSku = flashSnapshot.getCurrentSku();
                    ProductDTO.SkuInfo skuInfo = defaultSku != null ? defaultSku.getSku() : null;
                    FlashProduct defaultFlashProduct = resolveDefaultFlashProduct(entry.getValue());
                    ProductDTO.SpuInfo spuInfo = flashSnapshot.getSpu();

                    return FlashPortalProductRespVO.builder()
                            .id(spuId)
                            .flashSessionId(defaultFlashProduct != null ? defaultFlashProduct.getFlashSessionId() : null)
                            .spuId(spuId)
                            .spuName(spuInfo != null ? spuInfo.getName() : null)
                            .spuPic(spuInfo != null ? spuInfo.getPic() : null)
                            .skuId(skuInfo != null ? skuInfo.getId() : null)
                            .skuPic(skuInfo != null ? skuInfo.getPic() : null)
                            .attrValues(joinSpecValues(defaultSku))
                            .compareAtPrice(skuInfo != null ? skuInfo.getCompareAtPrice() : null)
                            .flashPrice(skuInfo != null ? skuInfo.getDisplayPrice() : null)
                            .flashStock(defaultFlashProduct != null ? defaultFlashProduct.getFlashStock() : null)
                            .flashLimit(defaultFlashProduct != null ? defaultFlashProduct.getFlashLimit() : null)
                            .build();
                })
                .filter(Objects::nonNull)
                .toList();
    }
    
    private void warmUpDetailSnapshots(FlashSession session, List<FlashProduct> products,
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
            ProductDTO flashSnapshot = buildFlashProductSnapshot(productDTO, spuFlashProducts);
            if (flashSnapshot != null) {
                detailCache.put(FlashRedisKeys.detailKey(session.getId(), spuId), flashSnapshot);
            }
        }

        if (!detailCache.isEmpty()) {
            typedRedisService.multiSetJsonWithExpire(detailCache, expireSeconds);
        }
        log.info("详情快照缓存预热完成，sessionId: {}, SPU数量: {}", session.getId(), detailCache.size());
    }

    private void warmUpSelectorSnapshots(FlashSession session, List<FlashProduct> products,
                                         Map<Long, ProductDTO> productSnapshots, long expireSeconds) {
        if (products.isEmpty() || productSnapshots == null || productSnapshots.isEmpty()) {
            return;
        }

        Map<Long, List<FlashProduct>> productsBySpu = products.stream()
                .filter(p -> p.getSpuId() != null)
                .collect(Collectors.groupingBy(FlashProduct::getSpuId));

        Map<String, FlashPortalSelectorRespVO> selectorCache = new HashMap<>(productSnapshots.size());
        for (Map.Entry<Long, ProductDTO> entry : productSnapshots.entrySet()) {
            Long spuId = entry.getKey();
            ProductDTO productDTO = entry.getValue();
            List<FlashProduct> spuFlashProducts = productsBySpu.getOrDefault(spuId, List.of());
            ProductDTO flashSnapshot = buildFlashProductSnapshot(productDTO, spuFlashProducts);
            if (flashSnapshot == null) {
                continue;
            }
            selectorCache.put(
                    FlashRedisKeys.selectorKey(session.getId(), spuId),
                    buildSelectorSnapshot(session.getId(), spuId, flashSnapshot)
            );
        }

        if (!selectorCache.isEmpty()) {
            typedRedisService.multiSetJsonWithExpire(selectorCache, expireSeconds);
        }
        log.info("选择器缓存预热完成，sessionId: {}, SPU数量: {}", session.getId(), selectorCache.size());
    }
    
    private ProductDTO buildFlashProductSnapshot(ProductDTO source, List<FlashProduct> spuFlashProducts) {
        if (source == null || spuFlashProducts == null || spuFlashProducts.isEmpty()) {
            return null;
        }

        Map<Long, FlashProduct> flashSkuMap = spuFlashProducts.stream()
                .filter(p -> p.getSkuId() != null)
                .collect(Collectors.toMap(FlashProduct::getSkuId, p -> p, (l, r) -> l));
        if (flashSkuMap.isEmpty()) {
            return null;
        }

        ProductDTO snapshot = deepCopyProduct(source);
        List<ProductDTO.SkuViewInfo> originalSkuList = snapshot.getSkuList();
        if (originalSkuList == null || originalSkuList.isEmpty()) {
            return null;
        }

        // 只保留参与秒杀的 SKU，并覆盖价格/库存
        List<ProductDTO.SkuViewInfo> filteredSkuList = new ArrayList<>();
        for (ProductDTO.SkuViewInfo skuViewInfo : originalSkuList) {
            if (skuViewInfo == null || skuViewInfo.getSku() == null || skuViewInfo.getSku().getId() == null) {
                continue;
            }
            FlashProduct flashProduct = flashSkuMap.get(skuViewInfo.getSku().getId());
            if (flashProduct == null) {
                continue;
            }
            applyFlashSkuData(skuViewInfo, flashProduct);
            filteredSkuList.add(skuViewInfo);
        }

        snapshot.setSkuList(filteredSkuList);
        snapshot.setSpecGroups(filterSpecGroups(snapshot.getSpecGroups(), filteredSkuList));

        Long defaultSkuId = resolveDefaultSkuId(spuFlashProducts);
        ProductDTO.SkuViewInfo defaultSku = filteredSkuList.stream()
                .filter(Objects::nonNull)
                .filter(item -> item.getSku() != null && Objects.equals(item.getSku().getId(), defaultSkuId))
                .findFirst()
                .orElse(filteredSkuList.isEmpty() ? null : filteredSkuList.get(0));
        if (defaultSku == null) {
            snapshot.setSelection(null);
            snapshot.setCurrentSku(null);
        } else {
            snapshot.setSelection(ProductDTO.SelectionInfo.builder()
                    .defaultSkuId(defaultSku.getSku().getId())
                    .selectedSpecValues(defaultSku.getSpecValues())
                    .build());
            snapshot.setCurrentSku(defaultSku);
        }

        refreshSalePriceRange(snapshot, filteredSkuList);
        return snapshot;
    }

    private FlashProduct resolveDefaultFlashProduct(List<FlashProduct> flashProducts) {
        if (flashProducts == null || flashProducts.isEmpty()) {
            return null;
        }
        return flashProducts.stream()
                .filter(Objects::nonNull)
                .min(Comparator.comparing(FlashProduct::getSort, Comparator.nullsLast(Integer::compareTo))
                        .thenComparing(FlashProduct::getSkuId, Comparator.nullsLast(Long::compareTo)))
                .orElse(null);
    }

    private Long resolveDefaultSkuId(List<FlashProduct> flashProducts) {
        FlashProduct defaultFlashProduct = resolveDefaultFlashProduct(flashProducts);
        return defaultFlashProduct != null ? defaultFlashProduct.getSkuId() : null;
    }

    private Integer resolveSortValue(List<FlashProduct> flashProducts) {
        FlashProduct defaultFlashProduct = resolveDefaultFlashProduct(flashProducts);
        return defaultFlashProduct != null && defaultFlashProduct.getSort() != null
                ? defaultFlashProduct.getSort()
                : Integer.MAX_VALUE;
    }

    private String joinSpecValues(ProductDTO.SkuViewInfo skuViewInfo) {
        if (skuViewInfo == null || skuViewInfo.getSpecValues() == null || skuViewInfo.getSpecValues().isEmpty()) {
            return null;
        }
        return skuViewInfo.getSpecValues().stream()
                .filter(Objects::nonNull)
                .map(ProductDTO.AttrValueInfo::getAttrValue)
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" "));
    }

    private void applyFlashSkuData(ProductDTO.SkuViewInfo skuViewInfo, FlashProduct flashProduct) {
        ProductDTO.SkuInfo skuInfo = skuViewInfo.getSku();
        BigDecimal flashPrice = flashProduct.getFlashPrice();
        skuInfo.setPromotionPrice(flashPrice);
        skuInfo.setDisplayPrice(flashPrice != null ? flashPrice : resolveDisplayPrice(skuInfo));

        Integer flashStock = flashProduct.getFlashStock();
        boolean inStock = flashStock != null && flashStock > 0;
        ProductDTO.SkuStockInfo stockInfo = skuViewInfo.getStock();
        if (stockInfo == null) {
            stockInfo = new ProductDTO.SkuStockInfo();
            skuViewInfo.setStock(stockInfo);
        }
        stockInfo.setInStock(inStock);
        stockInfo.setStockStatus(inStock ? 1 : 0);
        stockInfo.setLowStock(Boolean.FALSE);
    }

    private List<ProductDTO.SpecGroupInfo> filterSpecGroups(List<ProductDTO.SpecGroupInfo> specGroups,
                                                            List<ProductDTO.SkuViewInfo> skuList) {
        if (specGroups == null || specGroups.isEmpty() || skuList == null || skuList.isEmpty()) {
            return List.of();
        }

        Set<Long> activeAttrIds = skuList.stream()
                .filter(Objects::nonNull)
                .map(ProductDTO.SkuViewInfo::getSpecValues)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .filter(Objects::nonNull)
                .map(ProductDTO.AttrValueInfo::getAttrId)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        return specGroups.stream()
                .filter(Objects::nonNull)
                .filter(group -> group.getAttrId() != null && activeAttrIds.contains(group.getAttrId()))
                .collect(Collectors.toList());
    }

    private void refreshSalePriceRange(ProductDTO productDTO, List<ProductDTO.SkuViewInfo> skuList) {
        if (productDTO == null) {
            return;
        }

        List<BigDecimal> prices = skuList == null ? List.of() : skuList.stream()
                .filter(Objects::nonNull)
                .map(ProductDTO.SkuViewInfo::getSku)
                .filter(Objects::nonNull)
                .map(ProductDTO.SkuInfo::getDisplayPrice)
                .filter(Objects::nonNull)
                .toList();

        BigDecimal minPrice = prices.stream().min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
        BigDecimal maxPrice = prices.stream().max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);

        if (productDTO.getSale() == null) {
            productDTO.setSale(new ProductDTO.SpuSaleInfo());
        }
        productDTO.getSale().setMinPrice(minPrice);
        productDTO.getSale().setMaxPrice(maxPrice);
    }
    
    private void warmUpStock(FlashSession session, List<FlashProduct> products, long expireSeconds) {
        if (session == null || session.getId() == null || products.isEmpty()) {
            return;
        }

        HashMap<String, String> stocks = new HashMap<>();
        for (FlashProduct p : products) {
            stocks.put(FlashRedisKeys.stockKey(session.getId(), p.getSkuId()),
                    String.valueOf(p.getFlashStock()));
        }

        typedRedisService.multiSetStringWithExpire(stocks, expireSeconds);
        log.info("库存缓存预热完成，sessionId: {}, SKU数量: {}", session.getId(), stocks.size());
    }

    public FlashPortalDetailRespVO getPortalDetail(Long sessionId, Long spuId) {
        ProductDTO snapshot = loadFlashDetailSnapshot(sessionId, spuId);
        if (snapshot == null) {
            return null;
        }
        return FlashPortalDetailRespVO.builder()
                .sessionId(sessionId)
                .spu(snapshot.getSpu())
                .spuDetail(snapshot.getSpuDetail())
                .sale(snapshot.getSale())
                .stock(snapshot.getStock())
                .brand(snapshot.getBrand())
                .category(snapshot.getCategory())
                .params(snapshot.getParams())
                .selection(snapshot.getSelection())
                .specGroups(snapshot.getSpecGroups())
                .skuList(snapshot.getSkuList())
                .currentSku(snapshot.getCurrentSku())
                .build();
    }

    public FlashPortalSelectorRespVO getPortalSelector(Long sessionId, Long spuId) {
        if (sessionId == null || spuId == null) {
            return null;
        }

        String selectorKey = FlashRedisKeys.selectorKey(sessionId, spuId);
        FlashPortalSelectorRespVO cachedSelector = typedRedisService.getJson(selectorKey, FlashPortalSelectorRespVO.class);
        if (cachedSelector != null) {
            return cachedSelector;
        }

        ProductDTO detailSnapshot = loadFlashDetailSnapshot(sessionId, spuId);
        if (detailSnapshot == null) {
            return null;
        }

        FlashPortalSelectorRespVO selector = buildSelectorSnapshot(sessionId, spuId, detailSnapshot);
        FlashSession session = flashService.getSessionById(sessionId);
        typedRedisService.setJson(selectorKey, selector, calcExpireSeconds(session != null ? session.getEndTime() : null));
        return selector;
    }

    public FlashPortalSkuSelectedRespVO getPortalSkuSelected(Long sessionId, Long spuId, Long skuId) {
        if (sessionId == null || spuId == null || skuId == null) {
            return null;
        }

        FlashPortalSelectorRespVO selector = getPortalSelector(sessionId, spuId);
        if (selector == null || selector.getSkuList() == null || selector.getSkuList().isEmpty()) {
            return null;
        }

        ProductDTO.SkuViewInfo targetSku = selector.getSkuList().stream()
                .filter(Objects::nonNull)
                .filter(item -> item.getSku() != null && Objects.equals(item.getSku().getId(), skuId))
                .findFirst()
                .orElse(null);
        if (targetSku == null || targetSku.getSku() == null) {
            return null;
        }

        ProductDTO.SkuStockInfo stockInfo = deepCopyStockInfo(targetSku.getStock());
        String cachedStock = typedRedisService.getString(FlashRedisKeys.stockKey(sessionId, skuId));
        if (cachedStock != null && !cachedStock.isBlank()) {
            try {
                int stock = Integer.parseInt(cachedStock);
                if (stockInfo == null) {
                    stockInfo = new ProductDTO.SkuStockInfo();
                }
                stockInfo.setInStock(stock > 0);
                stockInfo.setStockStatus(stock > 0 ? 1 : 0);
                stockInfo.setLowStock(Boolean.FALSE);
            } catch (NumberFormatException ex) {
                throw new ApiException("秒杀库存缓存格式非法");
            }
        }

        return FlashPortalSkuSelectedRespVO.builder()
                .sessionId(sessionId)
                .spuId(spuId)
                .sku(targetSku.getSku())
                .specValues(targetSku.getSpecValues())
                .stock(stockInfo)
                .build();
    }

    private ProductDTO loadFlashDetailSnapshot(Long sessionId, Long spuId) {
        if (sessionId == null || spuId == null) {
            return null;
        }

        String detailKey = FlashRedisKeys.detailKey(sessionId, spuId);
        ProductDTO cachedSnapshot = typedRedisService.getJson(detailKey, ProductDTO.class);
        if (cachedSnapshot != null) {
            return cachedSnapshot;
        }

        List<FlashProduct> products = flashService.listProductsBySessionAndSpuId(sessionId, spuId);
        if (products == null || products.isEmpty()) {
            return null;
        }

        ProductDTO baseSnapshot = loadProductSnapshotMap(List.of(spuId)).get(spuId);
        ProductDTO flashSnapshot = buildFlashProductSnapshot(baseSnapshot, products);
        if (flashSnapshot == null) {
            return null;
        }

        FlashSession session = flashService.getSessionById(sessionId);
        typedRedisService.setJson(detailKey, flashSnapshot,
                calcExpireSeconds(session != null ? session.getEndTime() : null));
        return flashSnapshot;
    }

    private FlashPortalSelectorRespVO buildSelectorSnapshot(Long sessionId, Long spuId, ProductDTO snapshot) {
        return FlashPortalSelectorRespVO.builder()
                .sessionId(sessionId)
                .spuId(spuId)
                .selection(snapshot.getSelection())
                .specGroups(snapshot.getSpecGroups())
                .skuList(snapshot.getSkuList())
                .build();
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

    private long calcExpireSeconds(LocalDateTime endTime) {
        if (endTime == null) {
            return 60L;
        }
        long seconds = Duration.between(LocalDateTime.now(), endTime.plusMinutes(CACHE_BUFFER_MINUTES)).getSeconds();
        return Math.max(seconds, 60L);
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

    private ProductDTO.SkuStockInfo deepCopyStockInfo(ProductDTO.SkuStockInfo stockInfo) {
        if (stockInfo == null) {
            return null;
        }
        return objectMapper.convertValue(stockInfo, ProductDTO.SkuStockInfo.class);
    }

    private <T> T getJsonList(String key, TypeReference<T> typeReference) {
        String json = typedRedisService.getString(key);
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, typeReference);
        } catch (Exception ex) {
            log.warn("读取秒杀缓存失败，key: {}", key, ex);
            return null;
        }
    }
}
