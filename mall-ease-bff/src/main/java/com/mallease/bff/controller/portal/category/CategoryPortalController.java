package com.mallease.bff.controller.portal.category;

import com.mallease.bff.controller.portal.category.vo.CategoryTreeRespVO;
import com.mallease.bff.service.category.CategoryService;
import com.mallease.common.api.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * BFF 分类页控制器
 *
 * @author: Aulen
 * @create: 2026-01-08
 */
@Tag(name = "BFF分类页", description = "分类页聚合数据接口")
@RestController
@RequestMapping("/portal/categories")
@RequiredArgsConstructor
public class CategoryPortalController {

    private final CategoryService categoryService;

    @Operation(summary = "分类页数据", description = "获取分类树及各分类推荐商品")
    @GetMapping
    public R<List<CategoryTreeRespVO>> portalTreeWithProducts() {
        return R.success(categoryService.getCategoryTree());
    }
}
