package com.mallease.product.service.spu;

import cn.hutool.core.util.IdUtil;
import com.mallease.common.api.Page;
import com.mallease.common.dto.remote.ProductDTO;
import com.mallease.common.dto.remote.SearchFilterDTO;
import com.mallease.common.dto.remote.SpuMatchQueryDTO;
import com.mallease.common.dto.remote.SpuRecommendDTO;
import com.mallease.common.dto.remote.SpuSearchQuery;
import com.mallease.common.dto.remote.SpuSearchResultDTO;
import com.mallease.common.exception.ApiException;
import com.mallease.common.service.TypedRedisService;
import com.mallease.product.constant.ProductCacheKeys;
import com.mallease.product.controller.admin.spu.vo.SnapshotVO;
import com.mallease.product.controller.portal.spu.vo.ProductDetailRespVO;
import com.mallease.product.controller.portal.spu.vo.ProductSkuSelectedRespVO;
import com.mallease.product.controller.portal.spu.vo.ProductSelectorRespVO;
import com.mallease.product.controller.admin.spu.vo.SkuSaveReqVO;
import com.mallease.product.controller.admin.spu.vo.SpuDetailRespVO;
import com.mallease.product.controller.admin.spu.vo.SpuPageReqVO;
import com.mallease.product.controller.admin.spu.vo.SpuSaveReqVO;
import com.mallease.product.controller.admin.spu.vo.SpuStatsRespVO;
import com.mallease.product.convert.spu.SpuConvert;
import com.mallease.product.convert.spu.SpuSnapshotConvert;
import com.mallease.product.dal.entity.*;
import com.mallease.product.dal.mapper.AttributeDao;
import com.mallease.product.dal.mapper.AttributeValueDao;
import com.mallease.product.dal.mapper.SkuDao;
import com.mallease.product.dal.mapper.SkuStockDao;
import com.mallease.product.dal.mapper.SpuDao;
import com.mallease.product.dal.mapper.SpuDetailDao;
import com.mallease.product.dal.mapper.SpuSnapshotDao;
import com.mallease.product.service.brand.BrandService;
import com.mallease.product.service.category.CategoryService;
import com.mallease.product.service.stock.SkuStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
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

import static com.mallease.product.constant.ProductStatusConstants.ATTR_TYPE_PARAM;
import static com.mallease.product.constant.ProductStatusConstants.ATTR_TYPE_SPEC;
import static com.mallease.product.constant.ProductStatusConstants.DEFAULT_SORT;
import static com.mallease.product.constant.ProductStatusConstants.DELETED_NO;
import static com.mallease.product.constant.ProductStatusConstants.FLAG_DISABLED;
import static com.mallease.product.constant.ProductStatusConstants.FLAG_ENABLED;
import static com.mallease.product.constant.ProductStatusConstants.INITIAL_SALE;
import static com.mallease.product.constant.ProductStatusConstants.INITIAL_VERSION;
import static com.mallease.product.constant.ProductStatusConstants.NEW_STATUS_NO;
import static com.mallease.product.constant.ProductStatusConstants.PUBLISHED_VERSION_INITIAL;
import static com.mallease.product.constant.ProductStatusConstants.PUBLISH_STATUS_DRAFT;
import static com.mallease.product.constant.ProductStatusConstants.PUBLISH_STATUS_PUBLISHED;
import static com.mallease.product.constant.ProductStatusConstants.RECOMMEND_STATUS_NO;
import static com.mallease.product.constant.ProductStatusConstants.STAGED_CHANGES_YES;
import static com.mallease.product.constant.ProductStatusConstants.STOCK_STATUS_IN_STOCK;
import static com.mallease.product.constant.ProductStatusConstants.STOCK_STATUS_OUT_OF_STOCK;
import static com.mallease.product.constant.ProductStatusConstants.VERIFY_STATUS_PENDING;

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
    private final SkuStockService skuStockService;
    private final SpuSnapshotDao spuSnapshotDao;
    private final SpuConvert spuConvert;
    private final SpuSnapshotConvert spuSnapshotConvert;
    private final TypedRedisService typedRedisService;

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
        existingSpu.setHasStagedChanges(STAGED_CHANGES_YES);
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
                reqVO.getRecommendStatus(),
                reqVO.getHasStagedChanges()
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

        AttrValueBundle attrValueBundle = loadAttrValueBundle(spuId);
        respVO.setAttrValueList(spuConvert.toAttrValueRespList(attrValueBundle.params()));

        List<Sku> skuList = skuDao.selectEnabledBySpuId(spuId);
        List<SpuDetailRespVO.SkuRespVO> skuRespList = spuConvert.toSkuRespList(skuList);
        for (SpuDetailRespVO.SkuRespVO skuRespVO : skuRespList) {
            List<AttributeValue> specValues = attrValueBundle.skuSpecMap().get(skuRespVO.getId());
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
        ProductDetailRespVO product = spuSnapshotConvert.toProductDetailRespVO(snapshot);
        product.getCurrentSku().setDisplayPrice(resolveDetailSkuDisplayPrice(product.getCurrentSku()));
        product.getSelection().setDefaultSkuId(product.getCurrentSku().getId());
        return product;
    }

    @Override
    public ProductSelectorRespVO getPortalSelector(Long spuId) {
        SnapshotVO snapshot = getPublishedProductDetail(spuId);
        ProductSelectorRespVO selector = spuSnapshotConvert.toProductSelectorRespVO(snapshot);
        enrichPortalSelector(selector, spuId);
        return selector;
    }

    @Override
    public ProductSkuSelectedRespVO getPortalSkuSelected(Long spuId, Long skuId) {
        if (spuId == null || skuId == null) {
            return null;
        }
        SnapshotVO snapshot = getPublishedProductDetail(spuId);
        if (snapshot == null || snapshot.getSkus() == null || snapshot.getSkus().isEmpty()) {
            return null;
        }
        SnapshotVO.Sku snapshotSku = snapshot.getSkus().stream()
                .filter(Objects::nonNull)
                .filter(item -> Objects.equals(item.getSkuId(), skuId))
                .findFirst()
                .orElse(null);
        if (snapshotSku == null) {
            return null;
        }
        Map<Long, Boolean> stockMap = skuStockService.mapAvailabilityBySkuIds(spuId, List.of(skuId));
        boolean inStock = Boolean.TRUE.equals(stockMap.get(skuId));
        ProductDetailRespVO.SkuInfo skuInfo = ProductDetailRespVO.SkuInfo.builder()
                .id(snapshotSku.getSkuId())
                .skuCode(snapshotSku.getSkuCode())
                .pic(snapshotSku.getPic())
                .basePrice(snapshotSku.getBasePrice())
                .compareAtPrice(snapshotSku.getCompareAtPrice())
                .displayPrice(snapshotSku.getBasePrice())
                .enableStatus(snapshotSku.getEnableStatus())
                .build();
        skuInfo.setDisplayPrice(resolveDetailSkuDisplayPrice(skuInfo));
        return ProductSkuSelectedRespVO.builder()
                .sku(skuInfo)
                .specValues(snapshotSku.getAttrValues().stream()
                        .filter(Objects::nonNull)
                        .map(attrValue -> ProductDetailRespVO.AttrValueInfo.builder()
                                .attrId(attrValue.getAttrId())
                                .attrName(attrValue.getAttrName())
                                .attrValue(attrValue.getAttrValue())
                                .build())
                        .toList())
                .stock(ProductSelectorRespVO.StockInfo.builder()
                        .inStock(inStock)
                        .stockStatus(inStock ? STOCK_STATUS_IN_STOCK : STOCK_STATUS_OUT_OF_STOCK)
                        .lowStock(null)
                        .build())
                .build();
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
        List<ProductDTO> products = spuSnapshotConvert.toProductDTOList(snapshots);
        enrichProductSnapshots(products);
        return products;
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
                        .isNew(spu.getNewStatus() != null && spu.getNewStatus() == FLAG_ENABLED)
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
        SnapshotVO snapshotVO = typedRedisService.getJson(ProductCacheKeys.spuDetailKey(spuId), SnapshotVO.class);
        if (snapshotVO != null) {
            return snapshotVO;
        }

        SpuSnapshot snapshot = spuSnapshotDao.selectBySpuId(spuId);
        if (snapshot == null
                || !Integer.valueOf(PUBLISH_STATUS_PUBLISHED).equals(snapshot.getPublishStatus())
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

    private BigDecimal resolveDetailSkuDisplayPrice(ProductDetailRespVO.SkuInfo skuInfo) {
        return skuInfo.getPromotionPrice() != null ? skuInfo.getPromotionPrice() : skuInfo.getBasePrice();
    }

    private void enrichPortalSelector(ProductSelectorRespVO selector, Long spuId) {
        List<Long> skuIds = selector.getSkuList().stream()
                .map(ProductSelectorRespVO.SkuItem::getSkuId)
                .toList();
        Map<Long, Boolean> stockMap = skuStockService.mapAvailabilityBySkuIds(spuId, skuIds);

        for (ProductSelectorRespVO.SkuItem skuItem : selector.getSkuList()) {
            if (skuItem == null || skuItem.getSkuId() == null) {
                continue;
            }
            if (skuItem.getStock() == null) {
                skuItem.setStock(new ProductSelectorRespVO.StockInfo());
            }

            boolean inStock = Boolean.TRUE.equals(stockMap.get(skuItem.getSkuId()));
            skuItem.getStock().setInStock(inStock);
            skuItem.getStock().setStockStatus(inStock ? 1 : 0);
            skuItem.getStock().setLowStock(null);
        }

        refreshPortalSelectorSelection(selector);
    }

    private void refreshPortalSelectorSelection(ProductSelectorRespVO selector) {
        ProductSelectorRespVO.SkuItem selectedSku = selector.getSkuList().stream()
                .filter(this::isSellableSku)
                .findFirst()
                .orElse(selector.getSkuList().get(0));

        if (selector.getSelection() == null) {
            selector.setSelection(new ProductDetailRespVO.SelectionInfo());
        }
        selector.getSelection().setDefaultSkuId(selectedSku.getSkuId());
        selector.getSelection().setSelectedSpecValues(selectedSku.getSpecValues() == null ? List.of() : selectedSku.getSpecValues());
    }

    private boolean isSellableSku(ProductSelectorRespVO.SkuItem skuView) {
        Integer stockStatus = skuView.getStock().getStockStatus();
        return (stockStatus != null && stockStatus == STOCK_STATUS_IN_STOCK) || Boolean.TRUE.equals(skuView.getStock().getInStock());
    }

    private void enrichProductSnapshots(List<ProductDTO> products) {
        if (products == null || products.isEmpty()) {
            return;
        }

        List<Long> brandIds = products.stream()
                .map(ProductDTO::getBrand)
                .filter(Objects::nonNull)
                .map(ProductDTO.BrandInfo::getId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        Map<Long, Brand> brandMap = brandIds.isEmpty()
                ? Map.of()
                : brandService.listByIds(brandIds).stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Brand::getId, brand -> brand, (left, right) -> left));

        List<Long> spuIds = products.stream()
                .map(ProductDTO::getId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        Map<Long, List<SkuStock>> stockGroupMap = spuIds.isEmpty()
                ? Map.of()
                : skuStockDao.selectBySpuIds(spuIds).stream()
                .filter(Objects::nonNull)
                .filter(stock -> stock.getSpuId() != null)
                .collect(Collectors.groupingBy(SkuStock::getSpuId));

        for (ProductDTO product : products) {
            enrichProductSnapshotReferenceInfo(product, brandMap);
            enrichProductSnapshotSaleInfo(product, stockGroupMap.getOrDefault(product.getId(), List.of()));
            refreshProductSnapshotSelection(product);
        }
    }

    private void enrichProductSnapshotReferenceInfo(ProductDTO product, Map<Long, Brand> brandMap) {
        if (product == null) {
            return;
        }

        if (product.getBrand() != null && product.getBrand().getId() != null) {
            Brand brand = brandMap.get(product.getBrand().getId());
            if (brand != null) {
                product.getBrand().setLogo(brand.getLogo());
            }
        }

        if (product.getCategory() != null && product.getCategory().getId() != null
                && (product.getCategory().getBreadcrumb() == null || product.getCategory().getBreadcrumb().isEmpty())) {
            List<Category> ancestors = categoryService.listAncestors(product.getCategory().getId());
            if (ancestors != null && !ancestors.isEmpty()) {
                product.getCategory().setBreadcrumb(ancestors.stream()
                        .map(category -> ProductDTO.BreadcrumbItem.builder()
                                .id(category.getId())
                                .name(category.getName())
                                .build())
                        .toList());
            }
        }
    }

    private void enrichProductSnapshotSaleInfo(ProductDTO product, List<SkuStock> skuStocks) {
        if (product == null) {
            return;
        }

        Map<Long, SkuStock> stockMap = skuStocks == null
                ? Map.of()
                : skuStocks.stream()
                .filter(stock -> stock.getSkuId() != null)
                .collect(Collectors.toMap(SkuStock::getSkuId, stock -> stock, (left, right) -> left));

        if (product.getSkuList() != null) {
            for (ProductDTO.SkuViewInfo skuView : product.getSkuList()) {
                if (skuView == null || skuView.getSku() == null || skuView.getSku().getId() == null) {
                    continue;
                }

                skuView.getSku().setDisplayPrice(resolveProductDtoDisplayPrice(skuView));
                if (skuView.getStock() == null) {
                    skuView.setStock(new ProductDTO.SkuStockInfo());
                }

                SkuStock stock = stockMap.get(skuView.getSku().getId());
                if (stock == null) {
                    continue;
                }

                Integer stockStatus = resolveSkuStockStatus(stock);
                boolean inStock = stockStatus != null && stockStatus == STOCK_STATUS_IN_STOCK;
                boolean lowStock = stock.getLowStock() != null
                        && stock.getStock() != null
                        && stockStatus != null
                        && stockStatus == STOCK_STATUS_IN_STOCK
                        && stock.getStock() <= stock.getLowStock();
                skuView.getStock().setInStock(inStock);
                skuView.getStock().setStockStatus(stockStatus);
                skuView.getStock().setLowStock(lowStock);
            }
        }

        refreshProductSnapshotSpuStock(product, skuStocks == null ? List.of() : skuStocks);
    }

    private void refreshProductSnapshotSpuStock(ProductDTO product, List<SkuStock> skuStocks) {
        if (product.getStock() == null) {
            product.setStock(new ProductDTO.SpuStockInfo());
        }
        boolean hasInStock = skuStocks != null && skuStocks.stream()
                .map(this::resolveSkuStockStatus)
                .anyMatch(status -> status != null && status == STOCK_STATUS_IN_STOCK);
        product.getStock().setInStock(hasInStock);
        product.getStock().setStockStatus(hasInStock ? STOCK_STATUS_IN_STOCK : STOCK_STATUS_OUT_OF_STOCK);
    }

    private void refreshProductSnapshotSelection(ProductDTO product) {
        if (product == null || product.getSkuList() == null || product.getSkuList().isEmpty()) {
            return;
        }

        ProductDTO.SkuViewInfo selectedSku = product.getSkuList().stream()
                .filter(this::isSellableSku)
                .findFirst()
                .orElse(product.getSkuList().get(0));
        if (selectedSku == null || selectedSku.getSku() == null) {
            return;
        }

        if (product.getSelection() == null) {
            product.setSelection(new ProductDTO.SelectionInfo());
        }
        product.getSelection().setDefaultSkuId(selectedSku.getSku().getId());
        product.getSelection().setSelectedSpecValues(selectedSku.getSpecValues() == null
                ? List.of()
                : selectedSku.getSpecValues());
        product.setCurrentSku(selectedSku);
    }

    private boolean isSellableSku(ProductDTO.SkuViewInfo skuView) {
        if (skuView == null || skuView.getStock() == null) {
            return false;
        }
        Integer stockStatus = skuView.getStock().getStockStatus();
        return (stockStatus != null && stockStatus == STOCK_STATUS_IN_STOCK) || Boolean.TRUE.equals(skuView.getStock().getInStock());
    }

    private BigDecimal resolveProductDtoDisplayPrice(ProductDTO.SkuViewInfo skuView) {
        if (skuView == null || skuView.getSku() == null) {
            return null;
        }
        return skuView.getSku().getPromotionPrice() != null ? skuView.getSku().getPromotionPrice() : skuView.getSku().getBasePrice();
    }

    private Integer resolveSkuStockStatus(SkuStock stock) {
        if (stock == null) {
            return null;
        }
        if (stock.getStockStatus() != null) {
            return stock.getStockStatus();
        }
        if (stock.getStock() == null) {
            return STOCK_STATUS_OUT_OF_STOCK;
        }
        return stock.getStock() > 0 ? STOCK_STATUS_IN_STOCK : STOCK_STATUS_OUT_OF_STOCK;
    }

    private void initSpuForCreate(Spu spu) {
        spu.setDeleted(DELETED_NO);
        spu.setPublishStatus(PUBLISH_STATUS_DRAFT);
        spu.setVerifyStatus(VERIFY_STATUS_PENDING);
        spu.setNewStatus(NEW_STATUS_NO);
        spu.setRecommendStatus(RECOMMEND_STATUS_NO);
        spu.setSale(INITIAL_SALE);
        spu.setInStock(Boolean.FALSE);
        spu.setVersion(INITIAL_VERSION);
        spu.setHasStagedChanges(STAGED_CHANGES_YES);
        spu.setPublishedVersion(PUBLISHED_VERSION_INITIAL);
        spu.setPublishedAt(null);
        if (spu.getSort() == null) {
            spu.setSort(DEFAULT_SORT);
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
            if (!Integer.valueOf(ATTR_TYPE_PARAM).equals(attribute.getType())) {
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
                if (!Integer.valueOf(ATTR_TYPE_SPEC).equals(attribute.getType())) {
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

    private AttrValueBundle loadAttrValueBundle(Long spuId) {
        List<AttributeValue> attrValues = attributeValueDao.selectBySpuId(spuId);
        if (attrValues == null || attrValues.isEmpty()) {
            return new AttrValueBundle(List.of(), Map.of());
        }

        List<AttributeValue> params = attrValues.stream()
                .filter(item -> item.getSkuId() == null)
                .toList();
        Map<Long, List<AttributeValue>> skuSpecMap = attrValues.stream()
                .filter(item -> item.getSkuId() != null)
                .collect(Collectors.groupingBy(AttributeValue::getSkuId));
        return new AttrValueBundle(params, skuSpecMap);
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
            skuDao.updateEnableStatusBatch(syncPlan.disableSkuIds(), FLAG_DISABLED);
        }

        rebuildAttrValues(spuId, reqVO.getAttrValueList(), syncPlan.activeSkuSpecs());
    }

    private Sku buildSku(Long spuId, SkuSaveReqVO skuReqVO) {
        Sku sku = spuConvert.toSku(skuReqVO);
        sku.setDeleted(DELETED_NO);
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
            item.setDeleted(DELETED_NO);
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
            item.setDeleted(DELETED_NO);
        });
        return attrValues;
    }

    private int normalizeEnableStatus(Integer enableStatus) {
        return Integer.valueOf(FLAG_DISABLED).equals(enableStatus) ? FLAG_DISABLED : FLAG_ENABLED;
    }

    private boolean isEnabledSku(SkuSaveReqVO skuReqVO) {
        return normalizeEnableStatus(skuReqVO.getEnableStatus()) == FLAG_ENABLED;
    }

    private boolean isEnabledSku(Sku sku) {
        return normalizeEnableStatus(sku.getEnableStatus()) == FLAG_ENABLED;
    }

    private SpuStatsRespVO emptyStats() {
        return SpuStatsRespVO.builder()
                .allCount(0L)
                .publishedCount(0L)
                .unpublishedCount(0L)
                .unverifiedCount(0L)
                .build();
    }

    private record AttrValueBundle(
            List<AttributeValue> params,
            Map<Long, List<AttributeValue>> skuSpecMap
    ) {
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
