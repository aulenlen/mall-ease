package com.mallease.marketing.service.flash;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mallease.common.dto.remote.ProductDTO;
import com.mallease.marketing.controller.portal.flash.vo.FlashPortalProductRespVO;
import com.mallease.marketing.controller.portal.flash.vo.FlashPortalSelectorRespVO;
import com.mallease.marketing.dal.entity.FlashProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FlashSnapshotBuilder {

    private final ObjectMapper objectMapper;

    public List<FlashPortalProductRespVO> buildProductListVO(List<FlashProduct> products,
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

    public ProductDTO buildFlashProductSnapshot(ProductDTO source, List<FlashProduct> spuFlashProducts) {
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

    public FlashPortalSelectorRespVO buildSelectorSnapshot(Long sessionId, Long spuId, ProductDTO snapshot) {
        return FlashPortalSelectorRespVO.builder()
                .sessionId(sessionId)
                .spuId(spuId)
                .selection(snapshot.getSelection())
                .specGroups(snapshot.getSpecGroups())
                .skuList(snapshot.getSkuList())
                .build();
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
}