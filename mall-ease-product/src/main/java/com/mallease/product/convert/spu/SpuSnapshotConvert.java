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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SpuSnapshotConvert {

    ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

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
        return SnapshotVO.builder()
                .spu(toSpuVO(spu))
                .detail(toDetailVO(spuDetail))
                .params(toParamVOS(params))
                .specs(toSpecVOS(specs))
                .skus(toSkuVOS(skus))
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
        snapshotSpu.setMinPrice(spu.getMinPrice());
        snapshotSpu.setMaxPrice(spu.getMaxPrice());
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
        return detail;
    }

    default SnapshotVO.AttrValue toAttrValueVO(AttributeValue param) {
        if (param == null) {
            return null;
        }
        SnapshotVO.AttrValue attrValue = new SnapshotVO.AttrValue();
        attrValue.setAttrId(param.getAttrId());
        attrValue.setAttrName(param.getAttrName());
        attrValue.setAttrValue(param.getAttrValue());
        return attrValue;
    }

    default List<SnapshotVO.AttrValue> toParamVOS(List<AttributeValue> params) {
        if (params == null || params.isEmpty()) {
            return List.of();
        }
        return params.stream()
                .map(this::toAttrValueVO)
                .toList();
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
        snapshotSku.setAttrValues(parseSkuAttrValues(sku.getAttrValues()));
        snapshotSku.setName(buildSkuName(snapshotSku.getAttrValues()));
        return snapshotSku;
    }

    default List<SnapshotVO.Sku> toSkuVOS(List<Sku> skus) {
        if (skus == null || skus.isEmpty()) {
            return List.of();
        }
        return skus.stream()
                .map(this::toSkuVO)
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
                .spuBasic(toProductSpuBasic(snapshot.getSpu()))
                .spuDetail(toProductSpuDetail(snapshot))
                .brand(toProductBrand(snapshot.getSpu()))
                .category(toProductCategory(snapshot.getSpu()))
                .skuList(toProductSkuList(snapshot.getSkus()))
                .cacheTime(resolveCacheTime(snapshot.getPublishMeta()))
                .version(snapshot.getPublishMeta() != null ? snapshot.getPublishMeta().getVersion() : null)
                .build();
    }

    default ProductDTO toProductDTO(SnapshotVO snapshot) {
        if (snapshot == null) {
            return null;
        }
        return ProductDTO.builder()
                .spuBasic(toProductDtoSpuBasic(snapshot.getSpu()))
                .spuDetail(toProductDtoSpuDetail(snapshot))
                .brand(toProductDtoBrand(snapshot.getSpu()))
                .category(toProductDtoCategory(snapshot.getSpu()))
                .skuList(toProductDtoSkuList(snapshot.getSkus()))
                .cacheTime(resolveCacheTime(snapshot.getPublishMeta()))
                .version(snapshot.getPublishMeta() != null ? snapshot.getPublishMeta().getVersion() : null)
                .build();
    }

    default List<ProductDTO> toProductDTOList(List<SnapshotVO> snapshots) {
        if (snapshots == null || snapshots.isEmpty()) {
            return List.of();
        }
        return snapshots.stream()
                .map(this::toProductDTO)
                .toList();
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

    default List<SnapshotVO.AttrValue> parseSkuAttrValues(String attrValues) {
        if (!StringUtils.hasText(attrValues)) {
            return List.of();
        }
        try {
            return OBJECT_MAPPER.readValue(attrValues, new TypeReference<List<SnapshotVO.AttrValue>>() {
            });
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("SKU规格快照反序列化失败", e);
        }
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

    default String serializeSkuAttrValues(List<SnapshotVO.AttrValue> attrValues) {
        if (attrValues == null || attrValues.isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(attrValues);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("SKU规格快照序列化失败", e);
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

    private ProductDetailRespVO.SpuBasicInfo toProductSpuBasic(SnapshotVO.Spu spu) {
        if (spu == null) {
            return null;
        }
        return ProductDetailRespVO.SpuBasicInfo.builder()
                .id(spu.getId())
                .spuCode(spu.getSpuCode())
                .name(spu.getName())
                .subTitle(spu.getSubTitle())
                .description(spu.getDescription())
                .keywords(spu.getKeywords())
                .pic(spu.getPic())
                .albumPicList(spu.getAlbumPics())
                .unit(spu.getUnit())
                .weight(spu.getWeight())
                .publishStatus(spu.getPublishStatus())
                .newStatus(spu.getNewStatus())
                .recommendStatus(spu.getRecommendStatus())
                .sort(spu.getSort())
                .build();
    }

    private ProductDetailRespVO.SpuDetailInfo toProductSpuDetail(SnapshotVO snapshot) {
        return ProductDetailRespVO.SpuDetailInfo.builder()
                .detailTitle(snapshot.getDetail() != null ? snapshot.getDetail().getDetailTitle() : null)
                .detailDesc(snapshot.getDetail() != null ? snapshot.getDetail().getDetailDesc() : null)
                .serviceList(snapshot.getServices() == null || snapshot.getServices().getServiceIds() == null
                        ? List.of()
                        : snapshot.getServices().getServiceIds().stream().map(String::valueOf).toList())
                .totalSale(0)
                .minPrice(snapshot.getSpu() != null ? snapshot.getSpu().getMinPrice() : null)
                .maxPrice(snapshot.getSpu() != null ? snapshot.getSpu().getMaxPrice() : null)
                .inStock(Boolean.FALSE)
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

    private List<ProductDetailRespVO.SkuInfo> toProductSkuList(List<SnapshotVO.Sku> skus) {
        if (skus == null || skus.isEmpty()) {
            return List.of();
        }
        return skus.stream()
                .map(sku -> ProductDetailRespVO.SkuInfo.builder()
                        .basic(ProductDetailRespVO.SkuBasicInfo.builder()
                                .id(sku.getSkuId())
                                .skuCode(sku.getSkuCode())
                                .attrValues(serializeSkuAttrValues(sku.getAttrValues()))
                                .pic(sku.getPic())
                                .enableStatus(sku.getEnableStatus())
                                .build())
                        .price(ProductDetailRespVO.SkuPriceInfo.builder()
                                .basePrice(sku.getBasePrice())
                                .compareAtPrice(sku.getCompareAtPrice())
                                .build())
                        .config(ProductDetailRespVO.SkuConfigInfo.builder().build())
                        .build())
                .toList();
    }

    private ProductDTO.SpuBasicInfo toProductDtoSpuBasic(SnapshotVO.Spu spu) {
        if (spu == null) {
            return null;
        }
        return ProductDTO.SpuBasicInfo.builder()
                .id(spu.getId())
                .spuCode(spu.getSpuCode())
                .name(spu.getName())
                .subTitle(spu.getSubTitle())
                .description(spu.getDescription())
                .keywords(spu.getKeywords())
                .pic(spu.getPic())
                .albumPicList(spu.getAlbumPics())
                .unit(spu.getUnit())
                .weight(spu.getWeight())
                .publishStatus(spu.getPublishStatus())
                .newStatus(spu.getNewStatus())
                .recommendStatus(spu.getRecommendStatus())
                .sort(spu.getSort())
                .build();
    }

    private ProductDTO.SpuDetailInfo toProductDtoSpuDetail(SnapshotVO snapshot) {
        return ProductDTO.SpuDetailInfo.builder()
                .detailTitle(snapshot.getDetail() != null ? snapshot.getDetail().getDetailTitle() : null)
                .detailDesc(snapshot.getDetail() != null ? snapshot.getDetail().getDetailDesc() : null)
                .serviceList(snapshot.getServices() == null || snapshot.getServices().getServiceIds() == null
                        ? List.of()
                        : snapshot.getServices().getServiceIds().stream().map(String::valueOf).toList())
                .totalSale(0)
                .minPrice(snapshot.getSpu() != null ? snapshot.getSpu().getMinPrice() : null)
                .maxPrice(snapshot.getSpu() != null ? snapshot.getSpu().getMaxPrice() : null)
                .inStock(Boolean.FALSE)
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

    private List<ProductDTO.SkuInfo> toProductDtoSkuList(List<SnapshotVO.Sku> skus) {
        if (skus == null || skus.isEmpty()) {
            return List.of();
        }
        return skus.stream()
                .map(sku -> ProductDTO.SkuInfo.builder()
                        .basic(ProductDTO.SkuBasicInfo.builder()
                                .id(sku.getSkuId())
                                .skuCode(sku.getSkuCode())
                                .attrValues(serializeSkuAttrValues(sku.getAttrValues()))
                                .pic(sku.getPic())
                                .enableStatus(sku.getEnableStatus())
                                .build())
                        .price(ProductDTO.SkuPriceInfo.builder()
                                .basePrice(sku.getBasePrice())
                                .compareAtPrice(sku.getCompareAtPrice())
                                .build())
                        .config(ProductDTO.SkuConfigInfo.builder().build())
                        .build())
                .toList();
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