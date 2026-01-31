package com.mallease.bff.controller;

import com.mallease.bff.converter.CategoryConverter;
import com.mallease.bff.converter.HomeConverter;
import com.mallease.bff.feign.ProductFeignClient;
import com.mallease.bff.feign.SearchFeignClient;
import com.mallease.bff.model.client.vo.CategoryTreeVO;
import com.mallease.bff.model.client.vo.HomeRecommendVO;
import com.mallease.common.api.R;
import com.mallease.common.dto.remote.CategoryTreeDTO;
import com.mallease.common.dto.remote.SpuRecommendDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * BFF 分类页控制器
 *
 * @author: Aulen
 * @create: 2026-01-08
 */
@Tag(name = "BFF分类页", description = "分类页聚合数据接口")
@RestController
@RequestMapping("/bff/category")
@RequiredArgsConstructor
@Slf4j
public class CategoryController {
    private final ProductFeignClient productFeignClient;
    private final SearchFeignClient searchFeignClient;
    private final CategoryConverter categoryConverter;
    private final HomeConverter homeConverter;

    @Operation(summary = "分类页数据", description = "获取分类树及各分类推荐商品")
    @GetMapping("/tree")
    public R<List<CategoryTreeVO>> portalTreeWithProducts() {

        List<CategoryTreeDTO> treeDTOS = productFeignClient.portalTree().getData();

        List<CategoryTreeVO> categoryTreeVOS = categoryConverter.categoryTreeListDTOToVOList(treeDTOS);

        List<SpuRecommendDTO> spuRecommendDTOS = searchFeignClient.listRecommend(6).getData();

        List<HomeRecommendVO> homeRecommendVOS = homeConverter.spuRecommendDTOListToVOList(spuRecommendDTOS);

        Map<Long, List<HomeRecommendVO>> groupMap = homeRecommendVOS.stream()
                .filter(v -> v.getCategoryPath() != null && v.getCategoryPath().length() > 1)
                .collect(Collectors.groupingBy(
                        v -> Long.parseLong(v.getCategoryPath().split("/")[1])
                ));

        for (CategoryTreeVO categoryTreeVO : categoryTreeVOS) {
            categoryTreeVO.setRecommends(groupMap.get(categoryTreeVO.getId()));
        }

        return R.success(categoryTreeVOS);
    }
}
