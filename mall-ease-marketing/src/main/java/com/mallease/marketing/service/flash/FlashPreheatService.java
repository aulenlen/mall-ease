package com.mallease.marketing.service.flash;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mallease.common.api.R;
import com.mallease.common.constant.FlashRedisKeys;
import com.mallease.common.dto.remote.ProductDTO;
import com.mallease.common.service.TypedRedisService;
import com.mallease.marketing.controller.portal.flash.vo.FlashPortalDetailRespVO;
import com.mallease.marketing.dal.entity.FlashProduct;
import com.mallease.marketing.dal.entity.FlashSession;
import com.mallease.marketing.feign.ProductFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
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

        Map<Long, FlashSession> sessions = flashService.listSessionsToWarmUp(now, deadline).stream()
                .filter(session -> session.getId() != null)
                .collect(Collectors.toMap(FlashSession::getId, session -> session, (left, right) -> left));

        if (sessions.isEmpty()) {
            log.info("未来15分钟内无待预热秒杀场次");
            return;
        }

        warmUpSessions(sessions);
    }

    public void warmUpSessions(Map<Long, FlashSession> sessions) {

        if (sessions == null || sessions.isEmpty()) {
            return;
        }

        List<Long> sessionIds = sessions.keySet().stream().filter(Objects::nonNull).toList();
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


        for (Map.Entry<Long, FlashSession> entry : sessions.entrySet()) {
            Long sessionId = entry.getKey();
            FlashSession session = entry.getValue();
            List<FlashProduct> products = productsBySession.getOrDefault(sessionId, List.of());

            List<Long> spuIds = products.stream().filter(Objects::nonNull).map(FlashProduct::getSpuId).distinct().toList();

            warmUpSessionProducts(session, products, spuIds);
        }
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

    private void warmUpSessionProducts(FlashSession session, List<FlashProduct> flashProducts, List<Long> spuIds) {
        if (session == null || session.getId() == null) {
            return;
        }

        long expireSeconds = calcExpireSeconds(session.getEndTime());
        Map<Long, ProductDTO> productDTOs = loadProductSnapshotMap(spuIds);
        Map<Long, List<FlashProduct>> productsBySpu = flashProducts.stream()
                .filter(product -> product.getSpuId() != null)
                .collect(Collectors.groupingBy(FlashProduct::getSpuId));
        HashMap<String, ProductDTO> productsJson = new HashMap<>(productDTOs.size());

        for (Map.Entry<Long, ProductDTO> entry : productDTOs.entrySet()) {
            Long spuId = entry.getKey();
            ProductDTO productDTO = entry.getValue();
            List<FlashProduct> spuFlashProducts = productsBySpu.getOrDefault(spuId, List.of());
            ProductDTO flashSnapshot = buildFlashProductSnapshot(productDTO, spuFlashProducts);
            String key = FlashRedisKeys.detailKey(session.getId(), spuId);
            productsJson.put(key, flashSnapshot);
        }
        typedRedisService.multiSetJsonWithExpire(productsJson, expireSeconds);
        int stockCount = warmUpFlashStock(session, flashProducts, expireSeconds);

        log.info("秒杀缓存预热完成，sessionId: {}, spu数量: {}, stock数量: {}",
                session.getId(), productsBySpu.size(), stockCount);
    }

    private ProductDTO buildFlashProductSnapshot(ProductDTO source, List<FlashProduct> spuFlashProducts) {
        if (source == null || spuFlashProducts == null || spuFlashProducts.isEmpty()) {
            return null;
        }

        Map<Long, FlashProduct> flashSkuMap = spuFlashProducts.stream()
                .filter(product -> product.getSkuId() != null)
                .collect(Collectors.toMap(FlashProduct::getSkuId, product -> product, (left, right) -> left));
        if (flashSkuMap.isEmpty()) {
            return null;
        }

        ProductDTO snapshot = deepCopyProduct(source);
        List<ProductDTO.SkuViewInfo> originalSkuList = snapshot.getSkuList();
        if (originalSkuList == null || originalSkuList.isEmpty()) {
            return null;
        }

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

        ProductDTO.SkuViewInfo defaultSku = filteredSkuList.isEmpty() ? null : filteredSkuList.get(0);
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

    private int warmUpFlashStock(FlashSession session, List<FlashProduct> flashProducts, long expireSeconds) {
        if (session == null || session.getId() == null) {
            return 0;
        }
        HashMap<String, String> stocks = new HashMap<>();
        for (FlashProduct flashProduct : flashProducts) {
            stocks.put(FlashRedisKeys.stockKey(session.getId(), flashProduct.getSkuId()), String.valueOf(flashProduct.getFlashStock()));
        }

        if (!stocks.isEmpty()) {
            typedRedisService.multiSetStringWithExpire(stocks, expireSeconds);
        }
        return stocks.size();
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
        typedRedisService.setJson(detailKey, flashSnapshot, calcExpireSeconds(session != null ? session.getEndTime() : null));
        return flashSnapshot;
    }
}
