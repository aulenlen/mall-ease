package com.mallease.product.service.spu;

import cn.hutool.core.util.IdUtil;
import com.mallease.common.api.Page;
import com.mallease.common.api.R;
import com.mallease.common.dto.remote.ProductDTO;
import com.mallease.common.dto.remote.SearchFilterDTO;
import com.mallease.common.dto.remote.SpuFlashOverlayDTO;
import com.mallease.common.dto.remote.SpuMatchQueryDTO;
import com.mallease.common.dto.remote.SpuRecommendDTO;
import com.mallease.common.dto.remote.SpuSearchQuery;
import com.mallease.common.dto.remote.SpuSearchResultDTO;
import com.mallease.common.exception.ApiException;
import com.mallease.common.service.TypedRedisService;
import com.mallease.product.constant.ProductCacheKeys;
import com.mallease.product.controller.admin.spu.vo.SnapshotVO;
import com.mallease.product.controller.portal.spu.vo.ProductDetailRespVO;
import com.mallease.product.controller.admin.spu.vo.SkuSaveReqVO;
import com.mallease.product.controller.admin.spu.vo.SpuDetailRespVO;
import com.mallease.product.controller.admin.spu.vo.SpuPageReqVO;
import com.mallease.product.controller.admin.spu.vo.SpuSaveReqVO;
import com.mallease.product.controller.admin.spu.vo.SpuStatsRespVO;
import com.mallease.product.convert.spu.SpuConvert;
import com.mallease.product.convert.spu.SpuSnapshotConvert;
import com.mallease.product.dal.mapper.AttributeDao;
import com.mallease.product.dal.mapper.AttributeValueDao;
import com.mallease.product.dal.mapper.SkuDao;
import com.mallease.product.dal.mapper.SkuStockDao;
import com.mallease.product.dal.mapper.SpuDao;
import com.mallease.product.dal.mapper.SpuDetailDao;
import com.mallease.product.dal.mapper.SpuSnapshotDao;
import com.mallease.product.feign.marketing.MarketingFlashFeignClient;
import com.mallease.product.dal.entity.Attribute;
import com.mallease.product.dal.entity.AttributeValue;
import com.mallease.product.dal.entity.AttrValueAggregation;
import com.mallease.product.dal.entity.Brand;
import com.mallease.product.dal.entity.Category;
import com.mallease.product.dal.entity.Sku;
import com.mallease.product.dal.entity.Spu;
import com.mallease.product.dal.entity.SpuDetail;
import com.mallease.product.dal.entity.SpuSnapshot;
import com.mallease.product.service.brand.BrandService;
import com.mallease.product.service.category.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpuServiceImpl implements SpuService {

    private final BrandService brandService;
    private final CategoryService categoryService;
    private final AttributeDao attributeDao;
    private final SpuDao spuDao;
    private final SpuDetailDao spuDetailDao;
    private final AttributeValueDao attributeValueDao;
    private final SkuDao skuDao;
    private final SkuStockDao skuStockDao;
    private final SpuSnapshotDao spuSnapshotDao;
    private final SpuConvert spuConvert;
    private final SpuSnapshotConvert spuSnapshotConvert;
    private final TypedRedisService typedRedisService;
    private final MarketingFlashFeignClient marketingFlashFeignClient;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(SpuSaveReqVO reqVO) {
        validateAttrTypes(reqVO);
        Brand brand = getRequiredBrand(reqVO.getBrandId());
        Category category = getRequiredCategory(reqVO.getCategoryId());

        Spu spu = spuConvert.toSpu(reqVO);
        initSpuForCreate(spu);
        applySpuSaveFields(spu, reqVO, brand, category);

        if (spuDao.insert(spu) <= 0) {
            throw new ApiException("商品保存失败");
        }

        Long spuId = spu.getId();
        saveSpuRelations(spuId, reqVO);
        return spuId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(Long spuId, SpuSaveReqVO reqVO) {
        if (spuId == null) {
            throw new ApiException("商品ID不能为空");
        }

        validateAttrTypes(reqVO);
        Spu existingSpu = getRequiredSpu(spuId);
        Brand brand = getRequiredBrand(reqVO.getBrandId());
        Category category = getRequiredCategory(reqVO.getCategoryId());

        spuConvert.copyToSpu(existingSpu, reqVO);
        applySpuSaveFields(existingSpu, reqVO, brand, category);
        existingSpu.setHasStagedChanges(1);
        if (!StringUtils.hasText(existingSpu.getSpuCode())) {
            existingSpu.setSpuCode("SN" + IdUtil.getSnowflakeNextIdStr());
        }

        int count = spuDao.updateByPrimaryKey(existingSpu);
        if (count <= 0) {
            throw new ApiException("商品更新失败");
        }

        saveSpuRelations(spuId, reqVO);
        return count;
    }

    @Override
    public List<Spu> page(SpuPageReqVO reqVO) {
        return spuDao.selectByConditions(
                reqVO.getKeyword(),
                reqVO.getBrandId(),
                reqVO.getCategoryId(),
                reqVO.getPublishStatus(),
                reqVO.getVerifyStatus(),
                reqVO.getNewStatus(),
                reqVO.getRecommendStatus()
        );
    }

    @Override
    public SpuDetailRespVO getDetail(Long spuId) {
        if (spuId == null) {
            throw new ApiException("商品ID不能为空");
        }

        Spu spu = getRequiredSpu(spuId);
        SpuDetailRespVO respVO = spuConvert.toDetailRespVO(spu);
        respVO.setSpuDetail(spuConvert.toDetailDataRespVO(spuDetailDao.selectBySpuId(spuId)));
        respVO.setAttrValueList(spuConvert.toAttrValueRespList(attributeValueDao.selectParamsBySpuId(spuId)));

        List<Sku> skuList = skuDao.selectEnabledBySpuId(spuId);
        Map<Long, List<AttributeValue>> skuSpecMap = loadSkuSpecMap(skuList);
        List<SpuDetailRespVO.SkuRespVO> skuRespList = spuConvert.toSkuRespList(skuList);
        for (SpuDetailRespVO.SkuRespVO skuRespVO : skuRespList) {
            List<AttributeValue> specValues = skuSpecMap.get(skuRespVO.getId());
            if (specValues == null || specValues.isEmpty()) {
                continue;
            }
            skuRespVO.setAttrValues(spuConvert.toAttrValueRespList(specValues));
        }
        respVO.setSkuList(skuRespList);
        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(Long spuId) {
        if (spuId == null) {
            throw new ApiException("商品ID不能为空");
        }

        getRequiredSpu(spuId);
        List<Long> skuIds = skuDao.selectBySpuId(spuId).stream()
                .map(Sku::getId)
                .toList();

        attributeValueDao.removeBySpuId(spuId);
        spuDetailDao.deleteBySpuId(spuId);
        if (!skuIds.isEmpty()) {
            skuStockDao.deleteBySkuIds(skuIds);
            skuDao.deleteBatch(skuIds);
        }
        return spuDao.deleteBatch(List.of(spuId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBatch(List<Long> spuIds) {
        if (spuIds == null || spuIds.isEmpty()) {
            return 0;
        }
        if (spuIds.stream().anyMatch(Objects::isNull)) {
            throw new ApiException("商品ID不能为空");
        }

        List<Long> normalizedSpuIds = spuIds.stream().distinct().toList();
        List<Spu> existingSpus = spuDao.selectByIds(normalizedSpuIds);
        if (existingSpus.size() != normalizedSpuIds.size()) {
            throw new ApiException("部分商品不存在");
        }

        int deletedCount = 0;
        for (Long spuId : normalizedSpuIds) {
            deletedCount += delete(spuId);
        }
        return deletedCount;
    }

    @Override
    public SpuStatsRespVO stats(SpuPageReqVO reqVO) {
        SpuPageReqVO safeReqVO = reqVO == null ? new SpuPageReqVO() : reqVO;
        SpuStatsRespVO stats = spuDao.selectStats(
                safeReqVO.getKeyword(),
                safeReqVO.getBrandId(),
                safeReqVO.getCategoryId(),
                safeReqVO.getNewStatus(),
                safeReqVO.getRecommendStatus()
        );
        return stats != null ? stats : emptyStats();
    }

    @Override
    public List<Spu> listByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return spuDao.selectByIds(ids);
    }

    @Override
    public List<SpuDetail> listDetailsBySpuIds(List<Long> spuIds) {
        if (spuIds == null || spuIds.isEmpty()) {
            return List.of();
        }
        return spuDetailDao.selectBySpuIds(spuIds);
    }

    @Override
    public SnapshotVO getPublishedProductDetail(Long spuId) {
        if (spuId == null) {
            return null;
        }
        return loadPublishedProductDetail(spuId);
    }

    @Override
    public ProductDetailRespVO getPortalDetail(Long spuId) {
        SnapshotVO snapshot = getPublishedProductDetail(spuId);
        if (snapshot == null) {
            return null;
        }
        return spuSnapshotConvert.toProductDetailRespVO(snapshot);
    }

    @Override
    public ProductDetailRespVO getFlashPortalDetail(Long spuId, Long sessionId) {
        ProductDetailRespVO product = getPortalDetail(spuId);
        if (product == null) {
            return null;
        }

        R<SpuFlashOverlayDTO> overlayResponse = marketingFlashFeignClient.getOverlay(spuId, sessionId);
        if (overlayResponse == null || !overlayResponse.isSuccess()) {
            return product;
        }

        SpuFlashOverlayDTO overlay = overlayResponse.getData();
        if (overlay == null) {
            return product;
        }

        mergeFlashOverlay(product, overlay);
        return product;
    }

    @Override
    public List<ProductDTO> listPublishedProductSnapshots(List<Long> spuIds) {
        if (spuIds == null || spuIds.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, SnapshotVO> snapshotMap = loadPublishedProductDetails(spuIds);
        List<SnapshotVO> snapshots = spuIds.stream()
                .filter(Objects::nonNull)
                .map(snapshotMap::get)
                .filter(Objects::nonNull)
                .toList();
        if (snapshots.isEmpty()) {
            return Collections.emptyList();
        }
        return spuSnapshotConvert.toProductDTOList(snapshots);
    }

    @Override
    public SpuSearchResultDTO searchSpus(SpuSearchQuery query) {
        List<Spu> spus = spuDao.search(query);

        List<SpuRecommendDTO> products = spus.stream()
                .map(spu -> SpuRecommendDTO.builder()
                        .spuId(spu.getId())
                        .name(spu.getName())
                        .subTitle(spu.getSubTitle())
                        .pic(spu.getPic())
                        .brandId(spu.getBrandId())
                        .brandName(spu.getBrandName())
                        .categoryId(spu.getCategoryId())
                        .categoryPath(spu.getCategoryIds())
                        .categoryName(spu.getCategoryName())
                        .minPrice(spu.getMinPrice())
                        .maxPrice(spu.getMaxPrice())
                        .sale(spu.getSale())
                        .inStock(Boolean.TRUE.equals(spu.getInStock()))
                        .isNew(spu.getNewStatus() != null && spu.getNewStatus() == 1)
                        .build())
                .toList();

        Page<SpuRecommendDTO> productPage = Page.restPage(spus, products);

        SearchFilterDTO filters = new SearchFilterDTO();
        if (Boolean.TRUE.equals(query.getNeedAggregation())) {
            CompletableFuture<List<SearchFilterDTO.FilterItem>> brandsFuture =
                    CompletableFuture.supplyAsync(() -> spuDao.aggregateBrands(query));
            CompletableFuture<List<SearchFilterDTO.FilterItem>> categoriesFuture =
                    CompletableFuture.supplyAsync(() -> spuDao.aggregateCategories(query));
            CompletableFuture<List<AttrValueAggregation>> attrsFuture =
                    CompletableFuture.supplyAsync(() -> spuDao.aggregateAttrs(query));
            CompletableFuture<SearchFilterDTO.PriceRange> priceRangeFuture =
                    CompletableFuture.supplyAsync(() -> spuDao.aggregatePriceRange(query));

            CompletableFuture.allOf(brandsFuture, categoriesFuture, attrsFuture, priceRangeFuture).join();

            List<SearchFilterDTO.AttrFilterItem> attrs = attrsFuture.join().stream()
                    .collect(Collectors.groupingBy(AttrValueAggregation::getAttrId))
                    .entrySet().stream()
                    .map(entry -> {
                        List<AttrValueAggregation> values = entry.getValue();
                        return SearchFilterDTO.AttrFilterItem.builder()
                                .attrId(entry.getKey())
                                .attrName(values.get(0).getAttrName())
                                .values(values.stream()
                                        .map(value -> SearchFilterDTO.AttrValue.builder()
                                                .value(value.getValue())
                                                .count(value.getCount())
                                                .build())
                                        .toList())
                                .build();
                    })
                    .toList();

            SearchFilterDTO.PriceRange priceRange = priceRangeFuture.join();
            if (priceRange == null || (priceRange.getMin() == null && priceRange.getMax() == null)) {
                priceRange = SearchFilterDTO.PriceRange.builder()
                        .min(BigDecimal.ZERO)
                        .max(BigDecimal.ZERO)
                        .build();
            }

            filters = SearchFilterDTO.builder()
                    .brands(brandsFuture.join())
                    .categories(categoriesFuture.join())
                    .attrs(attrs)
                    .priceRange(priceRange)
                    .build();
        }

        return SpuSearchResultDTO.builder()
                .products(productPage)
                .filters(filters)
                .build();
    }

    @Override
    public List<Long> matchSpuIds(SpuMatchQueryDTO query) {
        if (query == null || query.getCandidateSpuIds() == null || query.getCandidateSpuIds().isEmpty()) {
            return List.of();
        }
        return spuDao.selectMatchedIds(query);
    }

    private Brand getRequiredBrand(Long brandId) {
        Brand brand = brandService.get(brandId);
        if (brand == null) {
            throw new ApiException("品牌不存在");
        }
        return brand;
    }

    private Category getRequiredCategory(Long categoryId) {
        Category category = categoryService.getById(categoryId);
        if (category == null) {
            throw new ApiException("分类不存在");
        }
        return category;
    }

    private Spu getRequiredSpu(Long spuId) {
        Spu spu = spuDao.selectByPrimaryKey(spuId);
        if (spu == null) {
            throw new ApiException("商品不存在");
        }
        return spu;
    }

    private SnapshotVO loadPublishedProductDetail(Long spuId) {
        String snapshotJson = typedRedisService.getString(ProductCacheKeys.spuDetailKey(spuId));
        if (StringUtils.hasText(snapshotJson)) {
            return spuSnapshotConvert.parseSnapshot(snapshotJson);
        }

        SpuSnapshot snapshot = spuSnapshotDao.selectBySpuId(spuId);
        if (snapshot == null
                || !Integer.valueOf(1).equals(snapshot.getPublishStatus())
                || !StringUtils.hasText(snapshot.getSnapshotJson())) {
            return null;
        }

        typedRedisService.setString(
                ProductCacheKeys.spuDetailKey(spuId),
                snapshot.getSnapshotJson(),
                ProductCacheKeys.spuDetailTtlSeconds()
        );
        return spuSnapshotConvert.parseSnapshot(snapshot.getSnapshotJson());
    }

    private Map<Long, SnapshotVO> loadPublishedProductDetails(List<Long> spuIds) {
        List<Long> distinctSpuIds = spuIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (distinctSpuIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<String> cacheKeys = distinctSpuIds.stream()
                .map(ProductCacheKeys::spuDetailKey)
                .toList();
        Map<String, String> cachedJsonMap = typedRedisService.multiGetString(cacheKeys);
        if (cachedJsonMap == null) {
            cachedJsonMap = Map.of();
        }

        Map<Long, SnapshotVO> snapshotMap = new LinkedHashMap<>();
        List<Long> missIds = new ArrayList<>();
        for (Long spuId : distinctSpuIds) {
            String snapshotJson = cachedJsonMap.get(ProductCacheKeys.spuDetailKey(spuId));
            if (!StringUtils.hasText(snapshotJson)) {
                missIds.add(spuId);
                continue;
            }
            snapshotMap.put(spuId, spuSnapshotConvert.parseSnapshot(snapshotJson));
        }

        if (!missIds.isEmpty()) {
            List<SpuSnapshot> snapshots = spuSnapshotDao.selectPublishedCacheBySpuIds(missIds);
            Map<String, String> cacheBackfill = new LinkedHashMap<>();
            for (SpuSnapshot snapshot : snapshots) {
                if (snapshot.getSpuId() == null || !StringUtils.hasText(snapshot.getSnapshotJson())) {
                    continue;
                }
                snapshotMap.put(snapshot.getSpuId(), spuSnapshotConvert.parseSnapshot(snapshot.getSnapshotJson()));
                cacheBackfill.put(ProductCacheKeys.spuDetailKey(snapshot.getSpuId()), snapshot.getSnapshotJson());
            }
            if (!cacheBackfill.isEmpty()) {
                typedRedisService.multiSetStringWithExpire(cacheBackfill, ProductCacheKeys.spuDetailTtlSeconds());
            }
        }
        return snapshotMap;
    }

    private void mergeFlashOverlay(ProductDetailRespVO product, SpuFlashOverlayDTO overlay) {
        if (product == null
                || product.getSkuList() == null
                || product.getSkuList().isEmpty()
                || overlay == null
                || overlay.getSkuFlashList() == null
                || overlay.getSkuFlashList().isEmpty()) {
            return;
        }

        Map<Long, SpuFlashOverlayDTO.SkuFlashOverlayDTO> overlaySkuMap = overlay.getSkuFlashList().stream()
                .filter(item -> item.getSkuId() != null)
                .collect(Collectors.toMap(SpuFlashOverlayDTO.SkuFlashOverlayDTO::getSkuId, item -> item, (left, right) -> left));

        LocalDateTime now = LocalDateTime.now();
        for (ProductDetailRespVO.SkuInfo skuInfo : product.getSkuList()) {
            if (skuInfo == null || skuInfo.getBasic() == null || skuInfo.getBasic().getId() == null) {
                continue;
            }

            SpuFlashOverlayDTO.SkuFlashOverlayDTO overlaySku = overlaySkuMap.get(skuInfo.getBasic().getId());
            if (overlaySku == null) {
                continue;
            }

            if (skuInfo.getPrice() == null) {
                skuInfo.setPrice(new ProductDetailRespVO.SkuPriceInfo());
            }
            if (overlaySku.getCompareAtPrice() != null && skuInfo.getPrice().getCompareAtPrice() == null) {
                skuInfo.getPrice().setCompareAtPrice(overlaySku.getCompareAtPrice());
            }

            if (shouldUseFlashPrice(overlay, overlaySku, now)) {
                skuInfo.getPrice().setPromotionPrice(overlaySku.getFlashPrice());
            }
        }

        refreshSpuPriceRange(product);
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

    private void refreshSpuPriceRange(ProductDetailRespVO product) {
        if (product == null || product.getSkuList() == null || product.getSkuList().isEmpty()) {
            return;
        }

        List<BigDecimal> prices = product.getSkuList().stream()
                .map(ProductDetailRespVO.SkuInfo::getPrice)
                .filter(Objects::nonNull)
                .map(this::resolveDisplayPrice)
                .filter(Objects::nonNull)
                .toList();
        if (prices.isEmpty()) {
            return;
        }

        if (product.getSpuDetail() == null) {
            product.setSpuDetail(new ProductDetailRespVO.SpuDetailInfo());
        }
        product.getSpuDetail().setMinPrice(prices.stream().min(BigDecimal::compareTo).orElse(null));
        product.getSpuDetail().setMaxPrice(prices.stream().max(BigDecimal::compareTo).orElse(null));
    }

    private BigDecimal resolveDisplayPrice(ProductDetailRespVO.SkuPriceInfo priceInfo) {
        if (priceInfo == null) {
            return null;
        }
        return priceInfo.getPromotionPrice() != null ? priceInfo.getPromotionPrice() : priceInfo.getBasePrice();
    }

    private void initSpuForCreate(Spu spu) {
        spu.setDeleted(0);
        spu.setPublishStatus(0);
        spu.setVerifyStatus(0);
        spu.setNewStatus(0);
        spu.setRecommendStatus(0);
        spu.setSale(0);
        spu.setInStock(Boolean.FALSE);
        spu.setVersion(1);
        spu.setHasStagedChanges(1);
        spu.setPublishedVersion(0);
        spu.setPublishedAt(null);
        if (spu.getSort() == null) {
            spu.setSort(0);
        }
        if (!StringUtils.hasText(spu.getSpuCode())) {
            spu.setSpuCode("SN" + IdUtil.getSnowflakeNextIdStr());
        }
    }

    private void applySpuSaveFields(Spu spu, SpuSaveReqVO reqVO, Brand brand, Category category) {
        fillSpuPriceRange(spu, reqVO.getSkuList());
        spu.setCategoryIds(category.getPath());
        spu.setCategoryName(category.getName());
        spu.setBrandName(brand.getName());
    }

    private void saveSpuRelations(Long spuId, SpuSaveReqVO reqVO) {
        upsertSpuDetail(spuId, reqVO.getSpuDetail());
        syncSkuSnapshot(spuId, reqVO);
    }

    private void validateAttrTypes(SpuSaveReqVO reqVO) {
        Map<Long, Attribute> attrMap = loadAttrMap(reqVO);
        validateSpuParams(reqVO.getAttrValueList(), attrMap);
        validateSkuSpecs(reqVO.getSkuList(), attrMap);
    }

    private Map<Long, Attribute> loadAttrMap(SpuSaveReqVO reqVO) {
        Set<Long> attrIds = new HashSet<>();
        if (reqVO.getAttrValueList() != null) {
            reqVO.getAttrValueList().stream()
                    .map(SpuSaveReqVO.AttrValueReqVO::getAttrId)
                    .filter(Objects::nonNull)
                    .forEach(attrIds::add);
        }
        if (reqVO.getSkuList() != null) {
            reqVO.getSkuList().stream()
                    .filter(sku -> sku.getAttrValues() != null)
                    .flatMap(sku -> sku.getAttrValues().stream())
                    .map(SkuSaveReqVO.AttrValueReqVO::getAttrId)
                    .filter(Objects::nonNull)
                    .forEach(attrIds::add);
        }
        if (attrIds.isEmpty()) {
            return Map.of();
        }

        List<Attribute> attributes = attributeDao.selectByIds(new ArrayList<>(attrIds));
        Map<Long, Attribute> attrMap = attributes.stream()
                .collect(Collectors.toMap(Attribute::getId, attribute -> attribute, (left, right) -> left));
        for (Long attrId : attrIds) {
            if (!attrMap.containsKey(attrId)) {
                throw new ApiException("属性不存在: " + attrId);
            }
        }
        return attrMap;
    }

    private void validateSpuParams(List<SpuSaveReqVO.AttrValueReqVO> attrValueReqList, Map<Long, Attribute> attrMap) {
        if (attrValueReqList == null || attrValueReqList.isEmpty()) {
            return;
        }
        Set<Long> seenAttrIds = new HashSet<>();
        for (SpuSaveReqVO.AttrValueReqVO reqVO : attrValueReqList) {
            Long attrId = reqVO.getAttrId();
            if (attrId == null) {
                throw new ApiException("商品参数属性ID不能为空");
            }
            if (!seenAttrIds.add(attrId)) {
                throw new ApiException("商品参数存在重复属性: " + attrId);
            }
            Attribute attribute = attrMap.get(attrId);
            if (attribute == null) {
                throw new ApiException("属性不存在: " + attrId);
            }
            if (!Integer.valueOf(0).equals(attribute.getType())) {
                throw new ApiException("商品参数只能使用参数类型属性: " + attrId);
            }
        }
    }

    private void validateSkuSpecs(List<SkuSaveReqVO> skuReqList, Map<Long, Attribute> attrMap) {
        if (skuReqList == null || skuReqList.isEmpty()) {
            return;
        }
        for (int i = 0; i < skuReqList.size(); i++) {
            SkuSaveReqVO skuReqVO = skuReqList.get(i);
            if (skuReqVO.getAttrValues() == null || skuReqVO.getAttrValues().isEmpty()) {
                continue;
            }
            Set<String> seenAttrNames = new HashSet<>();
            for (SkuSaveReqVO.AttrValueReqVO attrValueReqVO : skuReqVO.getAttrValues()) {
                String attrName = attrValueReqVO.getAttrName().trim();
                if (!seenAttrNames.add(attrName)) {
                    throw new ApiException("第" + (i + 1) + "个SKU存在重复规格属性: " + attrName);
                }

                Long attrId = attrValueReqVO.getAttrId();
                if (attrId == null) {
                    continue;
                }

                Attribute attribute = attrMap.get(attrId);
                if (attribute == null) {
                    throw new ApiException("属性不存在: " + attrId);
                }
                if (!Integer.valueOf(1).equals(attribute.getType())) {
                    throw new ApiException("SKU规格只能使用规格类型属性: " + attrId);
                }
            }
        }
    }

    private void fillSpuPriceRange(Spu spu, List<SkuSaveReqVO> skuReqList) {
        if (skuReqList == null || skuReqList.isEmpty()) {
            throw new ApiException("SKU列表不能为空");
        }
        List<SkuSaveReqVO> enabledSkuReqList = skuReqList.stream()
                .filter(this::isEnabledSku)
                .toList();
        List<SkuSaveReqVO> priceSource = enabledSkuReqList.isEmpty() ? skuReqList : enabledSkuReqList;

        BigDecimal minPrice = priceSource.stream()
                .map(SkuSaveReqVO::getBasePrice)
                .min(BigDecimal::compareTo)
                .orElseThrow(() -> new ApiException("SKU销售价不能为空"));
        BigDecimal maxPrice = priceSource.stream()
                .map(SkuSaveReqVO::getBasePrice)
                .max(BigDecimal::compareTo)
                .orElseThrow(() -> new ApiException("SKU销售价不能为空"));
        spu.setMinPrice(minPrice);
        spu.setMaxPrice(maxPrice);
    }

    private Map<Long, List<AttributeValue>> loadSkuSpecMap(List<Sku> skuList) {
        if (skuList == null || skuList.isEmpty()) {
            return Map.of();
        }
        List<Long> skuIds = skuList.stream()
                .map(Sku::getId)
                .toList();
        return attributeValueDao.selectSpecsBySkuIds(skuIds).stream()
                .collect(Collectors.groupingBy(AttributeValue::getSkuId));
    }

    private void upsertSpuDetail(Long spuId, SpuSaveReqVO.SpuDetailReqVO detailReqVO) {
        if (detailReqVO == null) {
            return;
        }
        SpuDetail existingDetail = spuDetailDao.selectBySpuId(spuId);
        if (existingDetail == null) {
            SpuDetail detail = spuConvert.toSpuDetail(detailReqVO);
            detail.setSpuId(spuId);
            spuDetailDao.insert(detail);
            return;
        }
        spuConvert.copyToSpuDetail(existingDetail, detailReqVO);
        spuDetailDao.updateByPrimaryKey(existingDetail);
    }

    private void syncSkuSnapshot(Long spuId, SpuSaveReqVO reqVO) {
        SkuSyncPlan syncPlan = buildSkuSyncPlan(spuId, reqVO.getSkuList());

        if (!syncPlan.createSkuList().isEmpty()) {
            skuDao.insertBatch(syncPlan.createSkuList());
        }
        if (!syncPlan.updateSkuList().isEmpty()) {
            skuDao.updateBatch(syncPlan.updateSkuList());
        }
        if (!syncPlan.disableSkuIds().isEmpty()) {
            skuDao.updateEnableStatusBatch(syncPlan.disableSkuIds(), 0);
        }

        rebuildAttrValues(spuId, reqVO.getAttrValueList(), syncPlan.activeSkuSpecs());
    }

    private Sku buildSku(Long spuId, SkuSaveReqVO skuReqVO) {
        Sku sku = spuConvert.toSku(skuReqVO);
        sku.setDeleted(0);
        sku.setSpuId(spuId);
        sku.setEnableStatus(normalizeEnableStatus(skuReqVO.getEnableStatus()));
        if (!StringUtils.hasText(sku.getSkuCode())) {
            sku.setSkuCode(IdUtil.getSnowflakeNextIdStr());
        }
        return sku;
    }

    private List<AttributeValue> buildSkuSpecValues(Long spuId, Long skuId, List<SkuSaveReqVO.AttrValueReqVO> attrValues) {
        List<AttributeValue> specValues = spuConvert.toSkuAttrValueList(attrValues);
        if (specValues == null || specValues.isEmpty()) {
            return List.of();
        }
        specValues.forEach(item -> {
            item.setSpuId(spuId);
            item.setSkuId(skuId);
            item.setDeleted(0);
        });
        return specValues;
    }

    private SkuSyncPlan buildSkuSyncPlan(Long spuId, List<SkuSaveReqVO> skuReqList) {
        List<Sku> existingSkuList = skuDao.selectBySpuId(spuId);
        Map<Long, Sku> existingSkuMap = existingSkuList.stream()
                .collect(Collectors.toMap(Sku::getId, sku -> sku, (left, right) -> left));

        List<Sku> createSkuList = new ArrayList<>();
        List<Sku> updateSkuList = new ArrayList<>();
        List<SkuSpecCarrier> activeSkuSpecs = new ArrayList<>();
        Set<Long> incomingExistingSkuIds = new HashSet<>();

        for (SkuSaveReqVO skuReqVO : skuReqList) {
            Long skuId = skuReqVO.getId();
            if (skuId == null) {
                Sku newSku = buildSku(spuId, skuReqVO);
                createSkuList.add(newSku);
                if (isEnabledSku(newSku)) {
                    activeSkuSpecs.add(new SkuSpecCarrier(newSku, skuReqVO.getAttrValues()));
                }
                continue;
            }
            if (!incomingExistingSkuIds.add(skuId)) {
                throw new ApiException("SKU ID重复");
            }
            Sku existingSku = existingSkuMap.get(skuId);
            if (existingSku == null) {
                throw new ApiException("SKU不存在或不属于当前商品: " + skuId);
            }
            spuConvert.copyToSku(existingSku, skuReqVO);
            existingSku.setSpuId(spuId);
            existingSku.setEnableStatus(normalizeEnableStatus(skuReqVO.getEnableStatus()));
            if (!StringUtils.hasText(existingSku.getSkuCode())) {
                existingSku.setSkuCode(IdUtil.getSnowflakeNextIdStr());
            }
            updateSkuList.add(existingSku);
            if (isEnabledSku(existingSku)) {
                activeSkuSpecs.add(new SkuSpecCarrier(existingSku, skuReqVO.getAttrValues()));
            }
        }

        List<Long> disableSkuIds = existingSkuList.stream()
                .map(Sku::getId)
                .filter(id -> !incomingExistingSkuIds.contains(id))
                .toList();

        return new SkuSyncPlan(createSkuList, updateSkuList, disableSkuIds, activeSkuSpecs);
    }

    private void rebuildAttrValues(Long spuId, List<SpuSaveReqVO.AttrValueReqVO> spuParamReqList, List<SkuSpecCarrier> activeSkuSpecs) {
        attributeValueDao.removeBySpuId(spuId);

        List<AttributeValue> attrValues = new ArrayList<>(buildSpuParamValues(spuId, spuParamReqList));
        for (SkuSpecCarrier activeSkuSpec : activeSkuSpecs) {
            attrValues.addAll(buildSkuSpecValues(spuId, activeSkuSpec.sku().getId(), activeSkuSpec.attrValues()));
        }
        if (!attrValues.isEmpty()) {
            attributeValueDao.insertBatch(attrValues);
        }
    }

    private List<AttributeValue> buildSpuParamValues(Long spuId, List<SpuSaveReqVO.AttrValueReqVO> attrValueReqList) {
        List<AttributeValue> attrValues = spuConvert.toAttrValueList(attrValueReqList);
        if (attrValues == null || attrValues.isEmpty()) {
            return List.of();
        }
        attrValues.forEach(item -> {
            item.setSpuId(spuId);
            item.setSkuId(null);
            item.setDeleted(0);
        });
        return attrValues;
    }

    private int normalizeEnableStatus(Integer enableStatus) {
        return Integer.valueOf(0).equals(enableStatus) ? 0 : 1;
    }

    private boolean isEnabledSku(SkuSaveReqVO skuReqVO) {
        return normalizeEnableStatus(skuReqVO.getEnableStatus()) == 1;
    }

    private boolean isEnabledSku(Sku sku) {
        return normalizeEnableStatus(sku.getEnableStatus()) == 1;
    }

    private SpuStatsRespVO emptyStats() {
        return SpuStatsRespVO.builder()
                .allCount(0L)
                .publishedCount(0L)
                .unpublishedCount(0L)
                .unverifiedCount(0L)
                .build();
    }

    private record SkuSyncPlan(
            List<Sku> createSkuList,
            List<Sku> updateSkuList,
            List<Long> disableSkuIds,
            List<SkuSpecCarrier> activeSkuSpecs
    ) {
    }

    private record SkuSpecCarrier(Sku sku, List<SkuSaveReqVO.AttrValueReqVO> attrValues) {
    }
}
