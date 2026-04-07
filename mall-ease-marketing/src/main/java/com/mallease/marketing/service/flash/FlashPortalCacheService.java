package com.mallease.marketing.service.flash;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import com.mallease.marketing.enums.FlashTimeStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class FlashPortalCacheService {

    private final TypedRedisService typedRedisService;
    private final FlashWarmUpService flashWarmUpService;
    private final FlashConvert flashConvert;
    private final ObjectMapper objectMapper;

    public FlashPortalSessionsRespVO getPortalSessions(LocalDate date) {
        if (date == null) {
            return FlashPortalSessionsRespVO.builder()
                    .serverTime(System.currentTimeMillis())
                    .sessions(List.of())
                    .build();
        }

        String key = FlashRedisKeys.sessionsKey(date.toString());
        List<Long> cachedSessionIds = getJsonList(key, new TypeReference<List<Long>>() {
        });
        if (cachedSessionIds != null) {
            return FlashPortalSessionsRespVO.builder()
                    .serverTime(System.currentTimeMillis())
                    .sessions(loadPortalSessionsByIds(cachedSessionIds))
                    .build();
        }

        flashWarmUpService.rebuildPortalCacheByDate(date);
        List<Long> reloadedSessionIds = getJsonList(key, new TypeReference<List<Long>>() {
        });
        return FlashPortalSessionsRespVO.builder()
                .serverTime(System.currentTimeMillis())
                .sessions(loadPortalSessionsByIds(reloadedSessionIds))
                .build();
    }

    public List<FlashPortalProductRespVO> getPortalProducts(Long sessionId) {
        if (sessionId == null) {
            return List.of();
        }

        String key = FlashRedisKeys.productsKey(sessionId);
        List<FlashPortalProductRespVO> cached = getJsonList(key, new TypeReference<List<FlashPortalProductRespVO>>() {
        });
        if (cached != null) {
            return cached;
        }

        flashWarmUpService.refreshSessionCache(sessionId);
        List<FlashPortalProductRespVO> reloaded = getJsonList(key, new TypeReference<List<FlashPortalProductRespVO>>() {
        });
        return reloaded != null ? reloaded : List.of();
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

        flashWarmUpService.refreshSessionCache(sessionId);
        return typedRedisService.getJson(selectorKey, FlashPortalSelectorRespVO.class);
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

    private List<FlashPortalSessionRespVO> loadPortalSessionsByIds(List<Long> sessionIds) {
        if (sessionIds == null || sessionIds.isEmpty()) {
            return List.of();
        }

        List<String> keys = sessionIds.stream()
                .filter(Objects::nonNull)
                .map(FlashRedisKeys::sessionKey)
                .toList();
        Map<String, String> sessionMap = typedRedisService.multiGetString(keys);
        List<FlashPortalSessionRespVO> sessions = new ArrayList<>(sessionIds.size());
        for (Long sessionId : sessionIds) {
            if (sessionId == null) {
                continue;
            }
            String json = sessionMap.get(FlashRedisKeys.sessionKey(sessionId));
            if (json == null || json.isBlank()) {
                continue;
            }
            try {
                FlashPortalSessionRespVO session = objectMapper.readValue(json, FlashPortalSessionRespVO.class);
                session.setTimeStatus(flashConvert.resolveTimeStatus(session.getStartTime(), session.getEndTime()));
                if (Objects.equals(session.getTimeStatus(), FlashTimeStatus.ENDED.getCode())) {
                    continue;
                }
                sessions.add(session);
            } catch (Exception ex) {
                log.warn("读取秒杀场次缓存失败，sessionId: {}", sessionId, ex);
            }
        }
        return sessions;
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

        flashWarmUpService.refreshSessionCache(sessionId);
        return typedRedisService.getJson(detailKey, ProductDTO.class);
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