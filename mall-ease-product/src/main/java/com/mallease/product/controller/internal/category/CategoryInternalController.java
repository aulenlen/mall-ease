package com.mallease.product.controller.internal.category;

import com.mallease.common.api.R;
import com.mallease.common.dto.remote.CategoryDTO;
import com.mallease.common.dto.remote.CategoryTreeDTO;
import com.mallease.product.service.category.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 分类内部接口
 */
@Tag(name = "分类内部接口", description = "供 BFF、营销等服务调用")
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/category")
public class CategoryInternalController {

    private final CategoryService categoryService;

    @Operation(summary = "获取金刚区分类", description = "内部调用")
    @GetMapping("/nav")
    public R<List<CategoryDTO>> listNavCategories() {
        return R.success(categoryService.listNavCategories());
    }

    @Operation(summary = "获取完整分类树", description = "内部调用")
    @GetMapping("/portalTree")
    public R<List<CategoryTreeDTO>> portalTree() {
        return R.success(categoryService.portalTree());
    }
}
