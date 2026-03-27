package com.mallease.bff.service.category;

import com.mallease.bff.controller.portal.category.vo.CategoryTreeRespVO;
import com.mallease.bff.controller.portal.common.vo.RecommendProductRespVO;
import com.mallease.bff.convert.CategoryConvert;
import com.mallease.bff.convert.HomeConvert;
import com.mallease.bff.feign.product.ProductFeignClient;
import com.mallease.bff.feign.search.SearchFeignClient;
import com.mallease.bff.service.support.RemoteCallSupport;
import com.mallease.common.dto.remote.CategoryTreeDTO;
import com.mallease.common.dto.remote.SpuRecommendDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 分类页聚合服务实现。
 *
 * @author: Aulen
 * @create: 2026-03-28
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final ProductFeignClient productFeignClient;
    private final SearchFeignClient searchFeignClient;
    private final CategoryConvert categoryConvert;
    private final HomeConvert homeConvert;
    private final RemoteCallSupport remoteCallSupport;

    @Override
    public List<CategoryTreeRespVO> getCategoryTree() {
        List<CategoryTreeDTO> categoryTreeDTOList = remoteCallSupport.getList(productFeignClient::portalTree, "分类树");
        List<SpuRecommendDTO> recommendDTOList = remoteCallSupport.getList(() -> searchFeignClient.listRecommend(6), "分类推荐商品");

        List<CategoryTreeRespVO> categoryTreeRespVOList = categoryConvert.categoryTreeListDTOToRespVOList(categoryTreeDTOList);
        List<RecommendProductRespVO> recommendRespVOList = homeConvert.spuRecommendDTOListToRespVOList(recommendDTOList);

        Map<Long, List<RecommendProductRespVO>> recommendMap = recommendRespVOList.stream()
                .map(respVO -> Map.entry(parseRootCategoryId(respVO.getCategoryPath()), respVO))
                .filter(entry -> entry.getKey() != null)
                .collect(Collectors.groupingBy(Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())));

        for (CategoryTreeRespVO categoryTreeRespVO : categoryTreeRespVOList) {
            categoryTreeRespVO.setRecommends(recommendMap.get(categoryTreeRespVO.getId()));
        }

        return categoryTreeRespVOList;
    }

    private Long parseRootCategoryId(String categoryPath) {
        if (categoryPath == null || categoryPath.isBlank()) {
            return null;
        }
        String[] categoryIds = categoryPath.split("/");
        if (categoryIds.length < 2 || categoryIds[1].isBlank()) {
            return null;
        }
        try {
            return Long.parseLong(categoryIds[1]);
        } catch (NumberFormatException e) {
            log.warn("分类路径格式不合法: {}", categoryPath);
            return null;
        }
    }
}