package com.mallease.product.controller;

import com.mallease.common.api.R;
import com.mallease.common.api.ResultCode;
import com.mallease.common.dto.remote.CategoryDTO;
import com.mallease.common.dto.remote.CategoryTreeDTO;
import com.mallease.product.converter.CategoryConverter;
import com.mallease.product.model.client.cmd.CategoryCmd;
import com.mallease.product.model.client.query.CategoryQuery;
import com.mallease.product.model.client.vo.CategoryDetailVO;

import com.mallease.product.model.client.vo.CategoryListVO;

import com.mallease.product.model.client.vo.CategoryTreeVO;
import com.mallease.product.model.client.vo.CategoryConfigSnapshotVO;
import com.mallease.product.model.data.entity.Category;
import com.mallease.product.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商品分类
 *
 * @author: Aulen
 * @create: 2025-12-13
 */
@Tag(name = "商品分类管理", description = "分类增删改查、树形结构、导航管理")
@RestController
@RequestMapping("/product/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    private final CategoryConverter categoryConverter;

    @Operation(summary = "创建分类")
    @PostMapping("/create")
    public R<Long> create(@Validated(CategoryCmd.Create.class) @RequestBody CategoryCmd cmd) {
        Category entity = categoryConverter.saveCmdToEntity(cmd);
        Long id = categoryService.create(entity, cmd.getParentId());
        return R.success(id);
    }

    @Operation(summary = "更新分类")
    @PostMapping("/update")
    public R<Integer> update(@Validated(CategoryCmd.Update.class) @RequestBody CategoryCmd cmd) {
        Category entity = categoryService.getById(cmd.getId());
        if (entity == null) {
            return R.failed(ResultCode.FAILED, "分类不存在");
        }
        categoryConverter.updateEntityFromCmd(entity, cmd);
        int count = categoryService.update(entity, cmd.getParentId());
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "删除分类")
    @PostMapping("/delete/{id}")
    public R<Integer> delete(
            @Parameter(description = "分类ID") @PathVariable Long id) {
        int count = categoryService.delete(id);
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "批量删除分类")
    @PostMapping("/delete/batch")
    public R<Integer> deleteBatch(@RequestBody List<Long> ids) {
        int count = categoryService.deleteBatch(ids);
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "获取分类详情")
    @GetMapping("/{id}")
    public R<CategoryDetailVO> getById(
            @Parameter(description = "分类ID") @PathVariable Long id) {
        Category category = categoryService.getById(id);
        if (category == null) {
            return R.success(null);
        }

        CategoryDetailVO vo = categoryConverter.entityToDetailVo(category);
        if (category.getParentId() != null && category.getParentId() > 0) {
            Category parent = categoryService.getById(category.getParentId());
            if (parent != null) {
                vo.setParentName(parent.getName());
            }
        }

        vo.setBreadcrumb(toBreadcrumbItems(categoryService.listAncestors(id)));
        return R.success(vo);
    }

    @Operation(summary = "获取子分类列表")
    @GetMapping("/children/{parentId}")
    public R<List<CategoryListVO>> listByParentId(
            @Parameter(description = "父分类ID，0表示一级分类") @PathVariable Long parentId) {
        List<Category> categories = categoryService.listByParentId(parentId);
        List<CategoryListVO> voList = toListVoWithChildCount(categories);
        return R.success(voList);
    }

    @Operation(summary = "获取所有子分类")
    @GetMapping("/descendants/{id}")
    public R<List<CategoryListVO>> listDescendants(
            @Parameter(description = "分类ID") @PathVariable Long id) {
        List<Category> descendants = categoryService.listDescendants(id);
        List<CategoryListVO> voList = categoryConverter.entityListToListVoList(descendants);
        return R.success(voList);
    }

    @Operation(summary = "按层级查询分类")
    @GetMapping("/level/{level}")
    public R<List<CategoryListVO>> listByLevel(
            @Parameter(description = "层级：0=一级，1=二级，2=三级") @PathVariable Integer level) {
        List<Category> categories = categoryService.listByLevel(level);
        List<CategoryListVO> voList = categoryConverter.entityListToListVoList(categories);
        return R.success(voList);
    }

    @Operation(summary = "获取完整分类树")
    @GetMapping("/tree")
    public R<List<CategoryTreeVO>> getFullTree() {
        List<Category> allCategories = categoryService.listAll();
        List<CategoryTreeVO> tree = categoryConverter.buildTree(allCategories);
        return R.success(tree);
    }

    @Operation(summary = "获取分类树（支持筛选）")
    @GetMapping("/tree/query")
    public R<List<CategoryTreeVO>> getTree(CategoryQuery query) {
        List<Category> categories = categoryService.listByQuery(query);
        List<CategoryTreeVO> tree = categoryConverter.buildTree(categories);
        return R.success(tree);
    }

    @Operation(summary = "获取导航分类树")
    @GetMapping("/tree/nav")
    public R<List<CategoryTreeVO>> getNavTree() {
        List<Category> navCategories = categoryService.listByQuery(CategoryQuery.builder().isNav(1).enableStatus(1).build());
        List<CategoryTreeVO> tree = categoryConverter.buildTree(navCategories);
        return R.success(tree);
    }

    @Operation(summary = "获取面包屑路径")
    @GetMapping("/breadcrumb/{id}")
    public R<List<CategoryDetailVO.BreadcrumbItem>> getBreadcrumb(
            @Parameter(description = "分类ID") @PathVariable Long id) {
        List<Category> ancestors = categoryService.listAncestors(id);
        List<CategoryDetailVO.BreadcrumbItem> breadcrumb = toBreadcrumbItems(ancestors);
        return R.success(breadcrumb);
    }

    @Operation(summary = "更新分类启用状态")
    @PostMapping("/enable-status/{id}")
    public R<Integer> updateEnableStatus(
            @Parameter(description = "分类ID") @PathVariable Long id,
            @Parameter(description = "启用状态：0-禁用，1-启用") @RequestParam Integer enableStatus) {
        int count = categoryService.updateEnableStatus(id, enableStatus);
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "批量更新分类启用状态")
    @PostMapping("/enable-status/batch")
    public R<Integer> updateEnableStatusBatch(
            @RequestBody List<Long> ids,
            @Parameter(description = "启用状态：0-禁用，1-启用") @RequestParam Integer enableStatus) {
        int count = categoryService.updateEnableStatusBatch(ids, enableStatus);
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "更新导航显示状态")
    @PostMapping("/nav/{id}")
    public R<Integer> updateNavStatus(
            @Parameter(description = "分类ID") @PathVariable Long id,
            @Parameter(description = "是否导航显示：0-否，1-是") @RequestParam Integer isNav) {
        int count = categoryService.updateNavStatus(id, isNav);
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "金刚区分类", description = "内部调用")
    @GetMapping("/internal/nav")
    public R<List<CategoryDTO>> listNavCategories() {
        return R.success(categoryService.listNavCategories());
    }

    @Operation(summary = "完整分类树", description = "内部调用")
    @GetMapping("/internal/portalTree")
    public R<List<CategoryTreeDTO>> portalTree() {
        return R.success(categoryService.portalTree());
    }

    // 分类配置\类目配置

    @Operation(summary = "获取分类快照", description = "一次请求返回 category + specs + params + brands")
    @GetMapping("/snapshot/{categoryId}")
    public R<CategoryConfigSnapshotVO> getCategoryConfigSnapshot(
            @Parameter(description = "分类ID") @PathVariable Long categoryId) {
        return R.success(categoryService.getCategoryConfigSnapshot(categoryId));
    }

    /**
     * 将分类实体列表转换为ListVO列表并填充子分类数量
     */
    private List<CategoryListVO> toListVoWithChildCount(List<Category> categories) {
        if (categories == null || categories.isEmpty()) {
            return new ArrayList<>();
        }
        List<CategoryListVO> voList = categoryConverter.entityListToListVoList(categories);

        // 批量统计子分类数量
        List<Long> categoryIds = categories.stream()
                .map(Category::getId)
                .collect(Collectors.toList());
        Map<Long, Long> childCountMap = categoryService.countChildrenByParentIds(categoryIds);
        for (CategoryListVO vo : voList) {
            vo.setChildCount(childCountMap.getOrDefault(vo.getId(), 0L).intValue());
        }

        return voList;
    }

    /**
     * 将祖先分类列表转换为面包屑项列表
     */
    private List<CategoryDetailVO.BreadcrumbItem> toBreadcrumbItems(List<Category> ancestors) {
        if (ancestors == null || ancestors.isEmpty()) {
            return new ArrayList<>();
        }

        return ancestors.stream()
                .map(c -> CategoryDetailVO.BreadcrumbItem.builder()
                        .id(c.getId())
                        .name(c.getName())
                        .level(c.getLevel())
                        .build())
                .collect(Collectors.toList());
    }
}
