package com.mallease.product.convert.spu;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mallease.common.dto.remote.ProductDTO;
import com.mallease.product.controller.admin.spu.vo.SnapshotVO;
import com.mallease.product.controller.portal.spu.vo.ProductDetailRespVO;
import com.mallease.product.dal.entity.AttributeValue;
import com.mallease.product.dal.entity.Sku;
import com.mallease.product.dal.entity.Spu;
import com.mallease.product.dal.entity.SpuDetail;
import com.mallease.product.dal.entity.SpuSnapshot;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.util.StringUtils;

import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SpuSnapshotConvert {

    ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    Map<Integer, String> SERVICE_NAME_MAP = Map.of(
            1, "无忧退货",
            2, "快速退款",
            3, "免费包邮"
    );

    default SpuSnapshot toSnapshot(Spu spu) {
        if (spu == null) {
            return SpuSnapshot.builder().build();
        }
        SpuSnapshot snapshot = new SpuSnapshot();
        snapshot.setSpuId(spu.getId());
        snapshot.setName(spu.getName());
        snapshot.setSubTitle(spu.getSubTitle());
        snapshot.setPic(spu.getPic());
        snapshot.setMinPrice(spu.getMinPrice());
        snapshot.setMaxPrice(spu.getMaxPrice());
        snapshot.setBrandId(spu.getBrandId());
        snapshot.setBrandName(spu.getBrandName());
        snapshot.setCategoryId(spu.getCategoryId());
        snapshot.setCategoryName(spu.getCategoryName());
        snapshot.setCategoryIds(spu.getCategoryIds());
        return snapshot;
    }

    default SnapshotVO toSnapshotVO(Spu spu, SpuDetail spuDetail, List<AttributeValue> params, List<AttributeValue> specs, List<Sku> skus) {
        return toSnapshotVO(spu, spuDetail, params, specs, skus, Map.of());
    }

    default SnapshotVO toSnapshotVO(Spu spu, SpuDetail spuDetail, List<AttributeValue> params, List<AttributeValue> specs,
                                    List<Sku> skus, Map<Long, List<AttributeValue>> skuSpecMap) {
        return SnapshotVO.builder()
                .spu(toSpuVO(spu))
                .detail(toDetailVO(spuDetail))
                .params(toParamVOS(params))
                .specs(toSpecVOS(specs))
                .skus(toSkuVOS(skus, skuSpecMap))
                .services(toServicesVO(spuDetail))
                .build();
    }

    default SnapshotVO.Spu toSpuVO(Spu spu) {
        if (spu == null) {
            return null;
        }
        SnapshotVO.Brand brand = new SnapshotVO.Brand();
        brand.setId(spu.getBrandId());
        brand.setName(spu.getBrandName());

        SnapshotVO.Category category = new SnapshotVO.Category();
        category.setId(spu.getCategoryId());
        category.setName(spu.getCategoryName());
        category.setPath(spu.getCategoryIds());

        SnapshotVO.Spu snapshotSpu = new SnapshotVO.Spu();
        snapshotSpu.setId(spu.getId());
        snapshotSpu.setSpuCode(spu.getSpuCode());
        snapshotSpu.setName(spu.getName());
        snapshotSpu.setSubTitle(spu.getSubTitle());
        snapshotSpu.setDescription(spu.getDescription());
        snapshotSpu.setKeywords(spu.getKeywords());
        snapshotSpu.setPic(spu.getPic());
        snapshotSpu.setAlbumPics(splitAlbumPics(spu.getAlbumPics()));
        snapshotSpu.setUnit(spu.getUnit());
        snapshotSpu.setWeight(spu.getWeight());
        snapshotSpu.setPublishStatus(spu.getPublishStatus());
        snapshotSpu.setNewStatus(spu.getNewStatus());
        snapshotSpu.setRecommendStatus(spu.getRecommendStatus());
        snapshotSpu.setSort(spu.getSort());
        snapshotSpu.setSale(spu.getSale());
        snapshotSpu.setMinPrice(spu.getMinPrice());
        snapshotSpu.setMaxPrice(spu.getMaxPrice());
        snapshotSpu.setInStock(spu.getInStock());
        snapshotSpu.setBrand(brand);
        snapshotSpu.setCategory(category);
        return snapshotSpu;
    }

    default SnapshotVO.Detail toDetailVO(SpuDetail spuDetail) {
        if (spuDetail == null) {
            return null;
        }
        SnapshotVO.Detail detail = new SnapshotVO.Detail();
        detail.setDetailTitle(spuDetail.getDetailTitle());
        detail.setDetailDesc(spuDetail.getDetailDesc());
        detail.setDetailHtml(spuDetail.getDetailHtml());
        detail.setDetailMobileHtml(spuDetail.getDetailMobileHtml());
        detail.setPackingList(spuDetail.getPackingList());
        detail.setAfterSaleService(spuDetail.getAfterSaleService());
        return detail;
    }

    default SnapshotVO.AttrValue toAttrValueVO(AttributeValue attrValue) {
        if (attrValue == null) {
            return null;
        }
        SnapshotVO.AttrValue snapshotValue = new SnapshotVO.AttrValue();
        snapshotValue.setAttrId(attrValue.getAttrId());
        snapshotValue.setAttrName(attrValue.getAttrName());
        snapshotValue.setAttrValue(attrValue.getAttrValue());
        return snapshotValue;
    }

    default List<SnapshotVO.AttrValue> toParamVOS(List<AttributeValue> params) {
        return toAttrValueVOS(params);
    }

    default List<SnapshotVO.AttrValue> toAttrValueVOS(List<AttributeValue> attrValues) {
        if (attrValues == null || attrValues.isEmpty()) {
            return List.of();
        }
        return attrValues.stream().map(this::toAttrValueVO).toList();
    }

    default List<SnapshotVO.SpecOption> toSpecVOS(List<AttributeValue> specs) {
        if (specs == null || specs.isEmpty()) {
            return List.of();
        }
        Map<String, SnapshotVO.SpecOption> specMap = new LinkedHashMap<>();
        for (AttributeValue specValue : specs) {
            String groupKey = specValue.getAttrId() != null ? String.valueOf(specValue.getAttrId()) : specValue.getAttrName();
            SnapshotVO.SpecOption spec = specMap.computeIfAbsent(groupKey, key -> {
                SnapshotVO.SpecOption snapshotSpec = new SnapshotVO.SpecOption();
                snapshotSpec.setAttrId(specValue.getAttrId());
                snapshotSpec.setAttrName(specValue.getAttrName());
                snapshotSpec.setValues(new ArrayList<>());
                return snapshotSpec;
            });
            if (StringUtils.hasText(specValue.getAttrValue()) && !spec.getValues().contains(specValue.getAttrValue())) {
                spec.getValues().add(specValue.getAttrValue());
            }
        }
        return List.copyOf(specMap.values());
    }

    default SnapshotVO.Sku toSkuVO(Sku sku) {
        return toSkuVO(sku, List.of());
    }

    default SnapshotVO.Sku toSkuVO(Sku sku, List<AttributeValue> specValues) {
        if (sku == null) {
            return null;
        }
        SnapshotVO.Sku snapshotSku = new SnapshotVO.Sku();
        snapshotSku.setSkuId(sku.getId());
        snapshotSku.setSkuCode(sku.getSkuCode());
        snapshotSku.setPic(sku.getPic());
        snapshotSku.setBasePrice(sku.getBasePrice());
        snapshotSku.setCompareAtPrice(sku.getCompareAtPrice());
        snapshotSku.setEnableStatus(sku.getEnableStatus());
        snapshotSku.setAttrValues(toAttrValueVOS(specValues));
        snapshotSku.setName(buildSkuName(snapshotSku.getAttrValues()));
        return snapshotSku;
    }

    default List<SnapshotVO.Sku> toSkuVOS(List<Sku> skus) {
        return toSkuVOS(skus, Map.of());
    }

    default List<SnapshotVO.Sku> toSkuVOS(List<Sku> skus, Map<Long, List<AttributeValue>> skuSpecMap) {
        if (skus == null || skus.isEmpty()) {
            return List.of();
        }
        return skus.stream()
                .map(sku -> toSkuVO(sku, skuSpecMap != null ? skuSpecMap.getOrDefault(sku.getId(), List.of()) : List.of()))
                .toList();
    }

    default SnapshotVO.Services toServicesVO(SpuDetail spuDetail) {
        if (spuDetail == null || !StringUtils.hasText(spuDetail.getServiceIds())) {
            return null;
        }
        SnapshotVO.Services services = new SnapshotVO.Services();
        services.setServiceIds(Arrays.stream(spuDetail.getServiceIds().split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .map(Integer::valueOf)
                .toList());
        return services;
    }

    default ProductDetailRespVO toProductDetailRespVO(SnapshotVO snapshot) {
        if (snapshot == null) {
            return null;
        }
        return ProductDetailRespVO.builder()
                .spu(toProductSpu(snapshot.getSpu()))
                .spuDetail(toProductSpuDetail(snapshot.getDetail(), snapshot.getServices()))
                .sale(toProductSale(snapshot.getSpu()))
                .brand(toProductBrand(snapshot.getSpu()))
                .category(toProductCategory(snapshot.getSpu()))
                .params(toProductAttrValues(snapshot.getParams()))
                .selection(toProductSelection(snapshot.getSkus()))
                .currentSku(toProductCurrentSku(snapshot.getSkus()))
                .build();
    }

    default com.mallease.product.controller.portal.spu.vo.ProductSelectorRespVO toProductSelectorRespVO(SnapshotVO snapshot) {
        if (snapshot == null) {
            return null;
        }
        return com.mallease.product.controller.portal.spu.vo.ProductSelectorRespVO.builder()
                .spuId(snapshot.getSpu() != null ? snapshot.getSpu().getId() : null)
                .selection(toProductSelection(snapshot.getSkus()))
                .specGroups(toProductSpecGroups(snapshot.getSpecs()))
                .skuList(toProductSelectorSkuItems(snapshot.getSkus()))
                .build();
    }

    default ProductDTO toProductDTO(SnapshotVO snapshot) {
        if (snapshot == null) {
            return null;
        }
        return ProductDTO.builder()
                .spu(toProductDtoSpu(snapshot.getSpu()))
                .spuDetail(toProductDtoSpuDetail(snapshot.getDetail(), snapshot.getServices()))
                .sale(toProductDtoSale(snapshot.getSpu()))
                .stock(toProductDtoStock(snapshot.getSpu()))
                .brand(toProductDtoBrand(snapshot.getSpu()))
                .category(toProductDtoCategory(snapshot.getSpu()))
                .params(toProductDtoAttrValues(snapshot.getParams()))
                .selection(toProductDtoSelection(snapshot.getSkus()))
                .specGroups(toProductDtoSpecGroups(snapshot.getSpecs()))
                .skuList(toProductDtoSkuViews(snapshot.getSkus()))
                .currentSku(toProductDtoCurrentSku(snapshot.getSkus()))
                .cacheMeta(toProductDtoCacheMeta(snapshot.getPublishMeta()))
                .build();
    }

    default List<ProductDTO> toProductDTOList(List<SnapshotVO> snapshots) {
        if (snapshots == null || snapshots.isEmpty()) {
            return List.of();
        }
        return snapshots.stream().map(this::toProductDTO).toList();
    }

    default List<String> splitAlbumPics(String albumPics) {
        if (!StringUtils.hasText(albumPics)) {
            return List.of();
        }
        return Arrays.stream(albumPics.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .toList();
    }

    default SnapshotVO parseSnapshot(String snapshotJson) {
        if (!StringUtils.hasText(snapshotJson)) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(snapshotJson, SnapshotVO.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("商品详情快照反序列化失败", e);
        }
    }

    default String buildSkuName(List<SnapshotVO.AttrValue> attrValues) {
        if (attrValues == null || attrValues.isEmpty()) {
            return null;
        }
        return attrValues.stream()
                .map(SnapshotVO.AttrValue::getAttrValue)
                .filter(StringUtils::hasText)
                .collect(Collectors.joining(" "));
    }

    private ProductDetailRespVO.SpuInfo toProductSpu(SnapshotVO.Spu spu) {
        if (spu == null) {
            return null;
        }
        return ProductDetailRespVO.SpuInfo.builder()
                .id(spu.getId())
                .spuCode(spu.getSpuCode())
                .brandId(spu.getBrand() != null ? spu.getBrand().getId() : null)
                .brandName(spu.getBrand() != null ? spu.getBrand().getName() : null)
                .categoryId(spu.getCategory() != null ? spu.getCategory().getId() : null)
                .categoryName(spu.getCategory() != null ? spu.getCategory().getName() : null)
                .categoryIds(spu.getCategory() != null ? spu.getCategory().getPath() : null)
                .name(spu.getName())
                .subTitle(spu.getSubTitle())
                .description(spu.getDescription())
                .keywords(spu.getKeywords())
                .pic(spu.getPic())
                .albumPics(spu.getAlbumPics())
                .unit(spu.getUnit())
                .weight(spu.getWeight())
                .publishStatus(spu.getPublishStatus())
                .newStatus(spu.getNewStatus())
                .recommendStatus(spu.getRecommendStatus())
                .sort(spu.getSort())
                .build();
    }

    private ProductDTO.SpuInfo toProductDtoSpu(SnapshotVO.Spu spu) {
        if (spu == null) {
            return null;
        }
        return ProductDTO.SpuInfo.builder()
                .id(spu.getId())
                .spuCode(spu.getSpuCode())
                .brandId(spu.getBrand() != null ? spu.getBrand().getId() : null)
                .brandName(spu.getBrand() != null ? spu.getBrand().getName() : null)
                .categoryId(spu.getCategory() != null ? spu.getCategory().getId() : null)
                .categoryName(spu.getCategory() != null ? spu.getCategory().getName() : null)
                .categoryIds(spu.getCategory() != null ? spu.getCategory().getPath() : null)
                .name(spu.getName())
                .subTitle(spu.getSubTitle())
                .description(spu.getDescription())
                .keywords(spu.getKeywords())
                .pic(spu.getPic())
                .albumPics(spu.getAlbumPics())
                .unit(spu.getUnit())
                .weight(spu.getWeight())
                .publishStatus(spu.getPublishStatus())
                .newStatus(spu.getNewStatus())
                .recommendStatus(spu.getRecommendStatus())
                .sort(spu.getSort())
                .build();
    }

    private ProductDetailRespVO.SpuDetailInfo toProductSpuDetail(SnapshotVO.Detail detail, SnapshotVO.Services services) {
        return ProductDetailRespVO.SpuDetailInfo.builder()
                .detailTitle(detail != null ? detail.getDetailTitle() : null)
                .detailDesc(detail != null ? detail.getDetailDesc() : null)
                .detailHtml(detail != null ? detail.getDetailHtml() : null)
                .detailMobileHtml(detail != null ? detail.getDetailMobileHtml() : null)
                .services(toProductServices(services))
                .packingList(detail != null ? detail.getPackingList() : null)
                .afterSaleService(detail != null ? detail.getAfterSaleService() : null)
                .build();
    }

    private ProductDTO.SpuDetailInfo toProductDtoSpuDetail(SnapshotVO.Detail detail, SnapshotVO.Services services) {
        return ProductDTO.SpuDetailInfo.builder()
                .detailTitle(detail != null ? detail.getDetailTitle() : null)
                .detailDesc(detail != null ? detail.getDetailDesc() : null)
                .detailHtml(detail != null ? detail.getDetailHtml() : null)
                .detailMobileHtml(detail != null ? detail.getDetailMobileHtml() : null)
                .services(toProductDtoServices(services))
                .packingList(detail != null ? detail.getPackingList() : null)
                .afterSaleService(detail != null ? detail.getAfterSaleService() : null)
                .build();
    }

    private ProductDetailRespVO.SpuSaleInfo toProductSale(SnapshotVO.Spu spu) {
        if (spu == null) {
            return null;
        }
        return ProductDetailRespVO.SpuSaleInfo.builder()
                .totalSale(spu.getSale())
                .minPrice(spu.getMinPrice())
                .maxPrice(spu.getMaxPrice())
                .build();
    }

    private ProductDTO.SpuSaleInfo toProductDtoSale(SnapshotVO.Spu spu) {
        if (spu == null) {
            return null;
        }
        return ProductDTO.SpuSaleInfo.builder()
                .totalSale(spu.getSale())
                .minPrice(spu.getMinPrice())
                .maxPrice(spu.getMaxPrice())
                .build();
    }

    private ProductDTO.SpuStockInfo toProductDtoStock(SnapshotVO.Spu spu) {
        if (spu == null) {
            return null;
        }
        return ProductDTO.SpuStockInfo.builder()
                .inStock(spu.getInStock())
                .stockStatus(resolveStockStatus(spu.getInStock()))
                .build();
    }

    private ProductDetailRespVO.BrandInfo toProductBrand(SnapshotVO.Spu spu) {
        if (spu == null || spu.getBrand() == null) {
            return null;
        }
        return ProductDetailRespVO.BrandInfo.builder()
                .id(spu.getBrand().getId())
                .name(spu.getBrand().getName())
                .build();
    }

    private ProductDTO.BrandInfo toProductDtoBrand(SnapshotVO.Spu spu) {
        if (spu == null || spu.getBrand() == null) {
            return null;
        }
        return ProductDTO.BrandInfo.builder()
                .id(spu.getBrand().getId())
                .name(spu.getBrand().getName())
                .build();
    }

    private ProductDetailRespVO.CategoryInfo toProductCategory(SnapshotVO.Spu spu) {
        if (spu == null || spu.getCategory() == null) {
            return null;
        }
        return ProductDetailRespVO.CategoryInfo.builder()
                .id(spu.getCategory().getId())
                .name(spu.getCategory().getName())
                .categoryIds(spu.getCategory().getPath())
                .build();
    }

    private ProductDTO.CategoryInfo toProductDtoCategory(SnapshotVO.Spu spu) {
        if (spu == null || spu.getCategory() == null) {
            return null;
        }
        return ProductDTO.CategoryInfo.builder()
                .id(spu.getCategory().getId())
                .name(spu.getCategory().getName())
                .categoryIds(spu.getCategory().getPath())
                .build();
    }

    private ProductDetailRespVO.SelectionInfo toProductSelection(List<SnapshotVO.Sku> skus) {
        SnapshotVO.Sku selectedSku = resolveDefaultSku(skus);
        if (selectedSku == null) {
            return null;
        }
        return ProductDetailRespVO.SelectionInfo.builder()
                .defaultSkuId(selectedSku.getSkuId())
                .selectedSpecValues(toProductAttrValues(selectedSku.getAttrValues()))
                .build();
    }

    private ProductDTO.SelectionInfo toProductDtoSelection(List<SnapshotVO.Sku> skus) {
        SnapshotVO.Sku selectedSku = resolveDefaultSku(skus);
        if (selectedSku == null) {
            return null;
        }
        return ProductDTO.SelectionInfo.builder()
                .defaultSkuId(selectedSku.getSkuId())
                .selectedSpecValues(toProductDtoAttrValues(selectedSku.getAttrValues()))
                .build();
    }

    private ProductDetailRespVO.SkuInfo toProductCurrentSku(List<SnapshotVO.Sku> skus) {
        SnapshotVO.Sku selectedSku = resolveDefaultSku(skus);
        return selectedSku == null ? null : toProductSku(selectedSku);
    }

    private ProductDTO.SkuViewInfo toProductDtoCurrentSku(List<SnapshotVO.Sku> skus) {
        SnapshotVO.Sku selectedSku = resolveDefaultSku(skus);
        return selectedSku == null ? null : toProductDtoSkuView(selectedSku);
    }

    private List<ProductDetailRespVO.SpecGroupInfo> toProductSpecGroups(List<SnapshotVO.SpecOption> specs) {
        if (specs == null || specs.isEmpty()) {
            return List.of();
        }
        return specs.stream()
                .map(spec -> ProductDetailRespVO.SpecGroupInfo.builder()
                        .attrId(spec.getAttrId())
                        .attrName(spec.getAttrName())
                        .build())
                .toList();
    }

    private List<ProductDTO.SpecGroupInfo> toProductDtoSpecGroups(List<SnapshotVO.SpecOption> specs) {
        if (specs == null || specs.isEmpty()) {
            return List.of();
        }
        return specs.stream()
                .map(spec -> ProductDTO.SpecGroupInfo.builder()
                        .attrId(spec.getAttrId())
                        .attrName(spec.getAttrName())
                        .build())
                .toList();
    }

    private List<com.mallease.product.controller.portal.spu.vo.ProductSelectorRespVO.SkuItem> toProductSelectorSkuItems(List<SnapshotVO.Sku> skus) {
        if (skus == null || skus.isEmpty()) {
            return List.of();
        }
        return skus.stream().map(this::toProductSelectorSkuItem).toList();
    }

    private List<ProductDTO.SkuViewInfo> toProductDtoSkuViews(List<SnapshotVO.Sku> skus) {
        if (skus == null || skus.isEmpty()) {
            return List.of();
        }
        return skus.stream().map(this::toProductDtoSkuView).toList();
    }

    private com.mallease.product.controller.portal.spu.vo.ProductSelectorRespVO.SkuItem toProductSelectorSkuItem(SnapshotVO.Sku sku) {
        if (sku == null) {
            return null;
        }
        return com.mallease.product.controller.portal.spu.vo.ProductSelectorRespVO.SkuItem.builder()
                .skuId(sku.getSkuId())
                .specValues(toProductAttrValues(sku.getAttrValues()))
                .stock(com.mallease.product.controller.portal.spu.vo.ProductSelectorRespVO.StockInfo.builder().build())
                .build();
    }

    private ProductDetailRespVO.SkuInfo toProductSku(SnapshotVO.Sku sku) {
        if (sku == null) {
            return null;
        }
        return ProductDetailRespVO.SkuInfo.builder()
                .id(sku.getSkuId())
                .skuCode(sku.getSkuCode())
                .pic(sku.getPic())
                .basePrice(sku.getBasePrice())
                .compareAtPrice(sku.getCompareAtPrice())
                .displayPrice(sku.getBasePrice())
                .enableStatus(sku.getEnableStatus())
                .build();
    }

    private ProductDTO.SkuViewInfo toProductDtoSkuView(SnapshotVO.Sku sku) {
        if (sku == null) {
            return null;
        }
        return ProductDTO.SkuViewInfo.builder()
                .sku(ProductDTO.SkuInfo.builder()
                        .id(sku.getSkuId())
                        .skuCode(sku.getSkuCode())
                        .pic(sku.getPic())
                        .basePrice(sku.getBasePrice())
                        .compareAtPrice(sku.getCompareAtPrice())
                        .displayPrice(sku.getBasePrice())
                        .enableStatus(sku.getEnableStatus())
                        .build())
                .specValues(toProductDtoAttrValues(sku.getAttrValues()))
                .stock(ProductDTO.SkuStockInfo.builder().build())
                .build();
    }

    private List<ProductDetailRespVO.AttrValueInfo> toProductAttrValues(List<SnapshotVO.AttrValue> attrValues) {
        if (attrValues == null || attrValues.isEmpty()) {
            return List.of();
        }
        return attrValues.stream()
                .filter(Objects::nonNull)
                .map(attrValue -> ProductDetailRespVO.AttrValueInfo.builder()
                        .attrId(attrValue.getAttrId())
                        .attrName(attrValue.getAttrName())
                        .attrValue(attrValue.getAttrValue())
                        .build())
                .toList();
    }

    private List<ProductDTO.AttrValueInfo> toProductDtoAttrValues(List<SnapshotVO.AttrValue> attrValues) {
        if (attrValues == null || attrValues.isEmpty()) {
            return List.of();
        }
        return attrValues.stream()
                .filter(Objects::nonNull)
                .map(attrValue -> ProductDTO.AttrValueInfo.builder()
                        .attrId(attrValue.getAttrId())
                        .attrName(attrValue.getAttrName())
                        .attrValue(attrValue.getAttrValue())
                        .build())
                .toList();
    }

    private List<ProductDetailRespVO.ServiceInfo> toProductServices(SnapshotVO.Services services) {
        if (services == null || services.getServiceIds() == null || services.getServiceIds().isEmpty()) {
            return List.of();
        }
        return services.getServiceIds().stream()
                .filter(Objects::nonNull)
                .map(serviceId -> ProductDetailRespVO.ServiceInfo.builder()
                        .code(serviceId)
                        .name(resolveServiceName(serviceId))
                        .build())
                .toList();
    }

    private List<ProductDTO.ServiceInfo> toProductDtoServices(SnapshotVO.Services services) {
        if (services == null || services.getServiceIds() == null || services.getServiceIds().isEmpty()) {
            return List.of();
        }
        return services.getServiceIds().stream()
                .filter(Objects::nonNull)
                .map(serviceId -> ProductDTO.ServiceInfo.builder()
                        .code(serviceId)
                        .name(resolveServiceName(serviceId))
                        .build())
                .toList();
    }

    private ProductDTO.CacheMetaInfo toProductDtoCacheMeta(SnapshotVO.PublishMeta publishMeta) {
        return ProductDTO.CacheMetaInfo.builder()
                .cacheTime(resolveCacheTime(publishMeta))
                .version(publishMeta != null ? publishMeta.getVersion() : null)
                .build();
    }

    private SnapshotVO.Sku resolveDefaultSku(List<SnapshotVO.Sku> skus) {
        if (skus == null || skus.isEmpty()) {
            return null;
        }
        return skus.stream().filter(Objects::nonNull).findFirst().orElse(null);
    }

    private String resolveServiceName(Integer serviceId) {
        if (serviceId == null) {
            return null;
        }
        return SERVICE_NAME_MAP.getOrDefault(serviceId, "服务" + serviceId);
    }

    private Integer resolveStockStatus(Boolean inStock) {
        if (inStock == null) {
            return null;
        }
        return Boolean.TRUE.equals(inStock) ? 1 : 0;
    }

    private Long resolveCacheTime(SnapshotVO.PublishMeta publishMeta) {
        if (publishMeta == null || publishMeta.getPublishedAt() == null) {
            return null;
        }
        return publishMeta.getPublishedAt()
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
    }
}
