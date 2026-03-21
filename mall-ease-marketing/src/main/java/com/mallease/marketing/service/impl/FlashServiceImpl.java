package com.mallease.marketing.service.impl;

import com.mallease.common.api.R;
import com.mallease.common.dto.remote.FlashCurrentDTO;
import com.mallease.common.dto.remote.SkuSimpleDTO;
import com.mallease.common.dto.remote.SpuMatchQueryDTO;
import com.mallease.common.enums.FlashRouteType;
import com.mallease.common.exception.ApiException;
import com.mallease.marketing.converter.FlashConverter;
import com.mallease.marketing.dao.FlashProductDao;
import com.mallease.marketing.dao.FlashSessionDao;
import com.mallease.marketing.feign.ProductFeignClient;
import com.mallease.marketing.model.client.query.FlashProductQuery;
import com.mallease.marketing.model.client.query.FlashSessionQuery;
import com.mallease.marketing.model.client.vo.FlashProductVO;
import com.mallease.marketing.model.data.entity.FlashProduct;
import com.mallease.marketing.model.data.entity.FlashSession;
import com.mallease.marketing.model.enums.FlashSessionStatus;
import com.mallease.marketing.service.FlashService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FlashServiceImpl implements FlashService {
    private final FlashSessionDao flashSessionDao;
    private final FlashProductDao flashProductDao;
    private final ProductFeignClient productFeignClient;
    private final FlashConverter flashConverter;

    // 场次

    @Override
    public int createFlashSession(FlashSession session) {
        validateSession(session, null);
        return flashSessionDao.insert(session);
    }

    @Override
    public int updateFlashSession(FlashSession session) {
        FlashSession current = requireSession(session.getId());
        FlashSession candidate = mergeSession(current, session);
        validateSession(candidate, current.getId());
        return flashSessionDao.updateByPrimaryKeySelective(session);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFlashSession(Long id) {
        FlashSession current = requireSession(id);
        if (isOngoing(current, LocalDateTime.now()) && FlashSessionStatus.ENABLED.codeEquals(current.getSessionStatus())) {
            throw new ApiException("进行中的场次不能删除，请先下架");
        }
        flashProductDao.deleteBySessionId(id);
        return flashSessionDao.deleteByPrimaryKey(id);
    }

    @Override
    public FlashSession getFlashSessionById(Long id) {
        return requireSession(id);
    }

    @Override
    public List<FlashSession> listFlashSessions(FlashSessionQuery query) {
        return flashSessionDao.listByConditions(
                query.getName(),
                query.getSessionStatus(),
                query.getStartTimeFrom(),
                query.getStartTimeTo()
        );
    }

    @Override
    public List<FlashSession> listPublishedFlashSessions(LocalDateTime nowDateTime) {
        return flashSessionDao.selectPublishedSessions(FlashSessionStatus.ENABLED.getCode(), nowDateTime);
    }

    @Override
    public List<FlashSession> listSessionsToWarmUp(LocalDateTime from, LocalDateTime to) {
        if (from == null || to == null) {
            throw new ApiException("预热时间范围不能为空");
        }
        if (from.isAfter(to)) {
            throw new ApiException("预热开始时间不能晚于结束时间");
        }
        return flashSessionDao.selectSessionsToWarmUp(FlashSessionStatus.ENABLED.getCode(), from, to);
    }

    // 活动、场次关联商品

    @Override
    public int addFlashProduct(FlashProduct flashProduct) {
        validateProduct(flashProduct, null, null);
        return flashProductDao.insert(flashProduct);
    }

    @Override
    public int addFlashProductBatch(List<FlashProduct> flashProducts) {
        if (flashProducts == null || flashProducts.isEmpty()) {
            return 0;
        }

        List<Long> skuIds = flashProducts.stream()
                .map(FlashProduct::getSkuId)
                .distinct()
                .toList();

        R<List<SkuSimpleDTO>> result = productFeignClient.listSkuSimpleByIds(skuIds);
        if (result == null || result.getData() == null) {
            throw new ApiException("获取SKU信息失败");
        }

        Map<Long, SkuSimpleDTO> skuMap = result.getData().stream()
                .collect(Collectors.toMap(SkuSimpleDTO::getId, dto -> dto, (a, b) -> a));

        Set<String> sessionSkuKeys = new HashSet<>();
        Map<String, Integer> batchRouteTypeMap = new HashMap<>();
        for (FlashProduct fp : flashProducts) {
            String sessionSkuKey = fp.getFlashSessionId() + "_" + fp.getSkuId();
            if (!sessionSkuKeys.add(sessionSkuKey)) {
                throw new ApiException("批量添加中存在重复的场次SKU，sessionId=" + fp.getFlashSessionId() + ", skuId=" + fp.getSkuId());
            }
            validateProduct(fp, null, skuMap);
            ensureBatchRouteTypeConsistent(fp, batchRouteTypeMap);
        }
        return flashProductDao.insertBatch(flashProducts);
    }

    @Override
    public int updateFlashProduct(FlashProduct flashProduct) {
        FlashProduct current = requireProduct(flashProduct.getId());
        FlashProduct candidate = mergeProduct(current, flashProduct);
        SkuSimpleDTO sku = validateProduct(candidate, current.getId(), null);
        if (flashProduct.getSkuId() != null) {
            flashProduct.setSpuId(sku.getSpuId());
        }
        return flashProductDao.updateByPrimaryKeySelective(flashProduct);
    }

    @Override
    public int deleteFlashProduct(Long id) {
        return flashProductDao.deleteByPrimaryKey(id);
    }

    @Override
    public int deleteFlashProductBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        return flashProductDao.deleteBatch(ids);
    }

    @Override
    public FlashProduct getFlashProductById(Long id) {
        return requireProduct(id);
    }

    @Override
    public List<FlashProduct> listFlashProducts(FlashProductQuery query) {
        List<Long> matchedSpuIds = resolveMatchedSpuIds(query);
        if (matchedSpuIds != null && matchedSpuIds.isEmpty()) {
            return emptyFlashProductPage(query);
        }
        return flashProductDao.listByConditions(
                query.getSessionId(),
                matchedSpuIds,
                query.getSpuId(),
                query.getSkuId(),
                query.getRouteType()
        );
    }

    @Override
    public List<FlashProduct> listFlashProductBySessionId(Long sessionId) {
        requireSession(sessionId);
        return flashProductDao.selectBySessionId(sessionId);
    }

    @Override
    public List<FlashProductVO> enrichWithSkuInfo(List<FlashProduct> products) {
        if (products == null || products.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> skuIds = products.stream().map(FlashProduct::getSkuId).distinct().toList();
        Map<Long, SkuSimpleDTO> skuMap = batchGetSkuMap(skuIds);
        Map<Long, FlashSession> sessionMap = batchGetSessionMap(products);

        return products.stream().map(p -> {
            FlashProductVO vo = flashConverter.productToVo(p);
            SkuSimpleDTO sku = skuMap.get(p.getSkuId());
            FlashSession session = sessionMap.get(p.getFlashSessionId());
            if (sku != null) {
                vo.setSpuName(sku.getSpuName());
                vo.setSpuPic(sku.getSpuPic());
                vo.setSkuPic(sku.getSkuPic());
                vo.setAttrValues(sku.getAttrValues());
                vo.setCompareAtPrice(sku.getCompareAtPrice());
            }
            if (session != null) {
                vo.setSessionName(session.getName());
                vo.setSessionStartTime(session.getStartTime());
                vo.setSessionEndTime(session.getEndTime());
                vo.setSessionStatus(session.getSessionStatus());
                vo.setTimeStatus(flashConverter.resolveTimeStatus(session.getStartTime(), session.getEndTime()));
            }
            return vo;
        }).toList();
    }

    @Override
    public int updateSessionStatusBatch(List<Long> ids, Integer sessionStatus) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        if (FlashSessionStatus.ENABLED.codeEquals(sessionStatus)) {
            List<FlashSession> sessions = ids.stream()
                    .map(this::requireSession)
                    .toList();
            validateSessionBatchEnable(sessions);
        }
        return flashSessionDao.updateSessionStatusBatch(ids, sessionStatus);
    }

    @Override
    public List<FlashProduct> getCurrentFlashProducts() {
        LocalDateTime nowDateTime = LocalDateTime.now();
        FlashSession currentSession = flashSessionDao.getCurrentSession(FlashSessionStatus.ENABLED.getCode(), nowDateTime);
        if (currentSession == null) {
            return null;
        }
        return flashProductDao.selectBySessionId(currentSession.getId());
    }

    @Override
    public FlashCurrentDTO getCurrentFlashData() {
        LocalDateTime nowDateTime = LocalDateTime.now();
        FlashSession currentSession = flashSessionDao.getCurrentSession(FlashSessionStatus.ENABLED.getCode(), nowDateTime);
        if (currentSession == null) {
            return FlashCurrentDTO.builder()
                    .serverTime(System.currentTimeMillis())
                    .products(Collections.emptyList())
                    .build();
        }

        List<FlashProduct> products = flashProductDao.selectBySessionId(currentSession.getId());

        // Feign 批量补齐商品展示信息
        List<Long> skuIds = products.stream().map(FlashProduct::getSkuId).distinct().toList();
        Map<Long, SkuSimpleDTO> skuMap = batchGetSkuMap(skuIds);

        List<FlashCurrentDTO.FlashProduct> productDTOList = products.stream()
                .map(p -> {
                    SkuSimpleDTO sku = skuMap.get(p.getSkuId());
                    return FlashCurrentDTO.FlashProduct.builder()
                            .id(p.getId())
                            .spuId(p.getSpuId())
                            .spuName(sku != null ? sku.getSpuName() : null)
                            .spuPic(sku != null ? sku.getSpuPic() : null)
                            .compareAtPrice(sku != null ? sku.getCompareAtPrice() : null)
                            .flashPrice(p.getFlashPrice())
                            .discountPercent(sku != null
                                    ? calculateDiscount(sku.getBasePrice(), p.getFlashPrice())
                                    : null)
                            .build();
                })
                .toList();

        return FlashCurrentDTO.builder()
                .sessionId(currentSession.getId())
                .name(currentSession.getName())
                .startTime(currentSession.getStartTime())
                .endTime(currentSession.getEndTime())
                .timeStatus(flashConverter.resolveTimeStatus(currentSession.getStartTime(), currentSession.getEndTime()))
                .serverTime(System.currentTimeMillis())
                .products(productDTOList)
                .build();
    }

    @Override
    public List<FlashProduct> listFlashProductBySessionIds(List<Long> sessionIds) {
        return flashProductDao.selectBySessionIds(sessionIds);
    }

    @Override
    public FlashSession getCurrentFlashSessions() {
        return flashSessionDao.getCurrentSession(FlashSessionStatus.ENABLED.getCode(), LocalDateTime.now());
    }

    /**
     * 批量查询 SKU 信息
     */
    private Map<Long, SkuSimpleDTO> batchGetSkuMap(List<Long> skuIds) {
        if (skuIds.isEmpty()) {
            return Collections.emptyMap();
        }
        try {
            R<List<SkuSimpleDTO>> result = productFeignClient.listSkuSimpleByIds(skuIds);
            if (result == null || result.getData() == null) {
                return Collections.emptyMap();
            }
            return result.getData().stream()
                    .collect(Collectors.toMap(SkuSimpleDTO::getId, dto -> dto, (a, b) -> a));
        } catch (Exception e) {
            log.warn("批量查询SKU信息失败，商品展示信息将缺失", e);
            return Collections.emptyMap();
        }
    }

    private Map<Long, FlashSession> batchGetSessionMap(List<FlashProduct> products) {
        List<Long> sessionIds = products.stream()
                .map(FlashProduct::getFlashSessionId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (sessionIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return flashSessionDao.selectByIds(sessionIds).stream()
                .collect(Collectors.toMap(FlashSession::getId, session -> session, (a, b) -> a));
    }

    /**
     * 计算折扣百分比：(原价-秒杀价)/原价 * 100
     */
    private Integer calculateDiscount(BigDecimal basePrice, BigDecimal flashPrice) {
        if (basePrice == null || flashPrice == null || basePrice.compareTo(BigDecimal.ZERO) == 0) {
            return 0;
        }
        return basePrice.subtract(flashPrice)
                .divide(basePrice, 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .intValue();
    }

    private FlashSession requireSession(Long id) {
        if (id == null) {
            throw new ApiException("场次ID不能为空");
        }
        FlashSession session = flashSessionDao.selectByPrimaryKey(id);
        if (session == null) {
            throw new ApiException("秒杀场次不存在，id=" + id);
        }
        return session;
    }

    private FlashProduct requireProduct(Long id) {
        if (id == null) {
            throw new ApiException("秒杀商品ID不能为空");
        }
        FlashProduct product = flashProductDao.selectByPrimaryKey(id);
        if (product == null) {
            throw new ApiException("秒杀商品不存在，id=" + id);
        }
        return product;
    }

    private FlashSession mergeSession(FlashSession current, FlashSession incoming) {
        FlashSession merged = new FlashSession();
        merged.setId(current.getId());
        merged.setName(incoming.getName() != null ? incoming.getName() : current.getName());
        merged.setStartTime(incoming.getStartTime() != null ? incoming.getStartTime() : current.getStartTime());
        merged.setEndTime(incoming.getEndTime() != null ? incoming.getEndTime() : current.getEndTime());
        merged.setSessionStatus(incoming.getSessionStatus() != null ? incoming.getSessionStatus() : current.getSessionStatus());
        return merged;
    }

    private FlashProduct mergeProduct(FlashProduct current, FlashProduct incoming) {
        FlashProduct merged = new FlashProduct();
        merged.setId(current.getId());
        merged.setFlashSessionId(incoming.getFlashSessionId() != null ? incoming.getFlashSessionId() : current.getFlashSessionId());
        merged.setSpuId(incoming.getSpuId() != null ? incoming.getSpuId() : current.getSpuId());
        merged.setSkuId(incoming.getSkuId() != null ? incoming.getSkuId() : current.getSkuId());
        merged.setFlashPrice(incoming.getFlashPrice() != null ? incoming.getFlashPrice() : current.getFlashPrice());
        merged.setFlashStock(incoming.getFlashStock() != null ? incoming.getFlashStock() : current.getFlashStock());
        merged.setFlashLimit(incoming.getFlashLimit() != null ? incoming.getFlashLimit() : current.getFlashLimit());
        merged.setRouteType(incoming.getRouteType() != null ? incoming.getRouteType() : current.getRouteType());
        merged.setSort(incoming.getSort() != null ? incoming.getSort() : current.getSort());
        return merged;
    }

    private void validateSession(FlashSession session, Long excludeId) {
        if (session.getStartTime() == null || session.getEndTime() == null) {
            throw new ApiException("场次开始时间和结束时间不能为空");
        }
        if (!session.getStartTime().isBefore(session.getEndTime())) {
            throw new ApiException("场次开始时间必须早于结束时间");
        }
        if (FlashSessionStatus.ENABLED.codeEquals(session.getSessionStatus())) {
            List<FlashSession> overlaps = flashSessionDao.selectOverlappingEnabledSessions(
                    FlashSessionStatus.ENABLED.getCode(),
                    excludeId,
                    session.getStartTime(),
                    session.getEndTime()
            );
            if (!overlaps.isEmpty()) {
                throw new ApiException("启用中的秒杀场次时间不能重叠");
            }
        }
    }

    private void validateSessionBatchEnable(List<FlashSession> sessions) {
        for (int i = 0; i < sessions.size(); i++) {
            FlashSession current = sessions.get(i);
            validateSession(current, current.getId());
            for (int j = i + 1; j < sessions.size(); j++) {
                FlashSession other = sessions.get(j);
                if (hasOverlap(current.getStartTime(), current.getEndTime(), other.getStartTime(), other.getEndTime())) {
                    throw new ApiException("批量启用的秒杀场次存在时间重叠");
                }
            }
        }
    }

    private SkuSimpleDTO validateProduct(FlashProduct product, Long excludeId, Map<Long, SkuSimpleDTO> preloadedSkuMap) {
        if (product.getFlashSessionId() == null) {
            throw new ApiException("秒杀场次ID不能为空");
        }
        FlashSession session = requireSession(product.getFlashSessionId());
        if (session.getEndTime() != null && session.getEndTime().isBefore(LocalDateTime.now())) {
            throw new ApiException("已结束的场次不能配置秒杀商品");
        }
        if (product.getSkuId() == null) {
            throw new ApiException("SKU ID不能为空");
        }
        SkuSimpleDTO sku = preloadedSkuMap != null ? preloadedSkuMap.get(product.getSkuId()) : getSku(product.getSkuId());
        if (sku == null || sku.getSpuId() == null) {
            throw new ApiException("SKU不存在或关联SPU无效，skuId=" + product.getSkuId());
        }
        if (product.getFlashPrice() == null || product.getFlashPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ApiException("秒杀价格必须大于0");
        }
        if (sku.getBasePrice() != null && product.getFlashPrice().compareTo(sku.getBasePrice()) >= 0) {
            throw new ApiException("秒杀价格必须低于商品基础成交价");
        }
        if (product.getFlashStock() == null || product.getFlashStock() < 1) {
            throw new ApiException("秒杀库存必须大于0");
        }
        if (product.getFlashLimit() != null && product.getFlashLimit() < 1) {
            throw new ApiException("限购数量必须大于0");
        }
        Integer routeType = normalizeRouteType(product);
        if (!FlashRouteType.isValid(routeType)) {
            throw new ApiException("路由类型非法，仅支持 0-非热点秒杀 或 1-热点秒杀");
        }
        FlashProduct existed = flashProductDao.selectBySessionAndSku(product.getFlashSessionId(), product.getSkuId());
        if (existed != null && !Objects.equals(existed.getId(), excludeId)) {
            throw new ApiException("同一场次下SKU不能重复配置");
        }
        product.setSpuId(sku.getSpuId());
        product.setRouteType(routeType);
        ensureStoredRouteTypeConsistent(product, excludeId);
        return sku;
    }

    private Integer normalizeRouteType(FlashProduct product) {
        return product.getRouteType() != null ? product.getRouteType() : FlashRouteType.NORMAL.getCode();
    }

    private void ensureStoredRouteTypeConsistent(FlashProduct product, Long excludeId) {
        List<FlashProduct> sameSpuProducts = flashProductDao.listByConditions(
                product.getFlashSessionId(),
                null,
                product.getSpuId(),
                null,
                null
        );
        if (sameSpuProducts == null || sameSpuProducts.isEmpty()) {
            return;
        }
        for (FlashProduct sameSpuProduct : sameSpuProducts) {
            if (Objects.equals(sameSpuProduct.getId(), excludeId)) {
                continue;
            }
            Integer sameRouteType = sameSpuProduct.getRouteType() != null
                    ? sameSpuProduct.getRouteType()
                    : FlashRouteType.NORMAL.getCode();
            if (!Objects.equals(sameRouteType, product.getRouteType())) {
                throw new ApiException("同一场次下同一SPU的多个SKU必须使用相同的路由类型");
            }
        }
    }

    private void ensureBatchRouteTypeConsistent(FlashProduct product, Map<String, Integer> batchRouteTypeMap) {
        String sessionSpuKey = product.getFlashSessionId() + "_" + product.getSpuId();
        Integer existingRouteType = batchRouteTypeMap.putIfAbsent(sessionSpuKey, product.getRouteType());
        if (existingRouteType != null && !Objects.equals(existingRouteType, product.getRouteType())) {
            throw new ApiException("批量添加中同一场次下同一SPU的多个SKU必须使用相同的路由类型");
        }
    }

    private SkuSimpleDTO getSku(Long skuId) {
        R<List<SkuSimpleDTO>> result = productFeignClient.listSkuSimpleByIds(List.of(skuId));
        if (result == null || result.getData() == null || result.getData().isEmpty()) {
            return null;
        }
        return result.getData().get(0);
    }

    private boolean isOngoing(FlashSession session, LocalDateTime now) {
        return session.getStartTime() != null
                && session.getEndTime() != null
                && !now.isBefore(session.getStartTime())
                && !now.isAfter(session.getEndTime());
    }

    private boolean hasOverlap(LocalDateTime startTime, LocalDateTime endTime, LocalDateTime otherStartTime, LocalDateTime otherEndTime) {
        return startTime.isBefore(otherEndTime) && endTime.isAfter(otherStartTime);
    }

    private List<Long> resolveMatchedSpuIds(FlashProductQuery query) {
        if (!hasProductFilters(query)) {
            return null;
        }

        List<Long> candidateSpuIds = flashProductDao.selectDistinctSpuIds(query.getSessionId());
        if (candidateSpuIds == null || candidateSpuIds.isEmpty()) {
            return Collections.emptyList();
        }

        SpuMatchQueryDTO matchQuery = new SpuMatchQueryDTO();
        matchQuery.setKeyword(query.getKeyword());
        matchQuery.setBrandId(query.getBrandId());
        matchQuery.setCategoryId(query.getCategoryId());
        matchQuery.setCandidateSpuIds(candidateSpuIds);

        R<List<Long>> result = productFeignClient.matchSpuIds(matchQuery);
        if (result == null || result.getData() == null) {
            throw new ApiException("查询商品匹配结果失败");
        }
        return result.getData();
    }

    private boolean hasProductFilters(FlashProductQuery query) {
        return query != null
                && ((query.getKeyword() != null && !query.getKeyword().isBlank())
                || query.getBrandId() != null
                || query.getCategoryId() != null);
    }

    private List<FlashProduct> emptyFlashProductPage(FlashProductQuery query) {
        if (query == null || query.getPageNum() == null || query.getPageSize() == null) {
            return Collections.emptyList();
        }
        com.github.pagehelper.Page<FlashProduct> page =
                new com.github.pagehelper.Page<>(query.getPageNum(), query.getPageSize());
        page.setTotal(0);
        return page;
    }
}
