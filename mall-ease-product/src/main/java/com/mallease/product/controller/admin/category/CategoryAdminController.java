package com.mallease.product.controller.admin.category;

import com.mallease.common.api.R;
import com.mallease.common.api.ResultCode;
import com.mallease.common.dto.remote.CategoryDTO;
import com.mallease.common.dto.remote.CategoryTreeDTO;
import com.mallease.product.controller.admin.category.vo.CategoryConfigSnapshotRespVO;
import com.mallease.product.controller.admin.category.vo.CategoryDetailRespVO;
import com.mallease.product.controller.admin.category.vo.CategoryListRespVO;
import com.mallease.product.controller.admin.category.vo.CategoryQueryReqVO;
import com.mallease.product.controller.admin.category.vo.CategorySaveReqVO;
import com.mallease.product.controller.admin.category.vo.CategoryTreeRespVO;
import com.mallease.product.convert.category.CategoryConvert;
import com.mallease.product.dal.entity.Category;
import com.mallease.product.service.category.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 后台商品分类管理
 */
@Tag(name = "后台商品分类管理", description = "分类增删改查、树形结构、导航管理")
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/category")
public class CategoryAdminController {

    private final CategoryService categoryService;
    private final CategoryConvert categoryConvert;

    @Operation(summary = "创建分类")
    @PostMapping("/create")
    public R<Long> create(@Validated(CategorySaveReqVO.Create.class) @RequestBody CategorySaveReqVO reqVO) {
        return R.success(categoryService.create(reqVO));
    }

    @Operation(summary = "更新分类")
    @PostMapping("/update")
    public R<Integer> update(@Validated(CategorySaveReqVO.Update.class) @RequestBody CategorySaveReqVO reqVO) {
        int count = categoryService.update(reqVO);
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "删除分类")
    @PostMapping("/delete/{id}")
    public R<Integer> delete(@Parameter(description = "分类ID") @PathVariable Long id) {
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
    public R<CategoryDetailRespVO> getById(@Parameter(description = "分类ID") @PathVariable Long id) {
        Category category = categoryService.getById(id);
        if (category == null) {
            return R.success(null);
        }

        CategoryDetailRespVO respVO = categoryConvert.entityToDetailRespVO(category);
        if (category.getParentId() != null && category.getParentId() > 0) {
            Category parent = categoryService.getById(category.getParentId());
            if (parent != null) {
                respVO.setParentName(parent.getName());
            }
        }
        respVO.setBreadcrumb(toBreadcrumbItems(categoryService.listAncestors(id)));
        return R.success(respVO);
    }

    @Operation(summary = "获取子分类列表")
    @GetMapping("/children/{parentId}")
    public R<List<CategoryListRespVO>> listByParentId(@Parameter(description = "父分类ID，0表示一级分类") @PathVariable Long parentId) {
        List<Category> categories = categoryService.listByParentId(parentId);
        return R.success(toListRespVOWithChildCount(categories));
    }

    @Operation(summary = "获取所有子分类")
    @GetMapping("/descendants/{id}")
    public R<List<CategoryListRespVO>> listDescendants(@Parameter(description = "分类ID") @PathVariable Long id) {
        List<Category> descendants = categoryService.listDescendants(id);
        return R.success(categoryConvert.entityListToListRespVOList(descendants));
    }

    @Operation(summary = "按层级查询分类")
    @GetMapping("/level/{level}")
    public R<List<CategoryListRespVO>> listByLevel(@Parameter(description = "层级：0=一级，1=二级，2=三级") @PathVariable Integer level) {
        List<Category> categories = categoryService.listByLevel(level);
        return R.success(categoryConvert.entityListToListRespVOList(categories));
    }

    @Operation(summary = "获取完整分类树")
    @GetMapping("/tree")
    public R<List<CategoryTreeRespVO>> getFullTree() {
        return R.success(categoryConvert.buildTree(categoryService.listAll()));
    }

    @Operation(summary = "获取分类树（支持筛选）")
    @GetMapping("/tree/query")
    public R<List<CategoryTreeRespVO>> getTree(@Validated @ModelAttribute CategoryQueryReqVO reqVO) {
        return R.success(categoryConvert.buildTree(categoryService.listByQuery(reqVO)));
    }

    @Operation(summary = "获取导航分类树")
    @GetMapping("/tree/nav")
    public R<List<CategoryTreeRespVO>> getNavTree() {
        CategoryQueryReqVO reqVO = CategoryQueryReqVO.builder()
                .isNav(1)
                .enableStatus(1)
                .build();
        return R.success(categoryConvert.buildTree(categoryService.listByQuery(reqVO)));
    }

    @Operation(summary = "获取面包屑路径")
    @GetMapping("/breadcrumb/{id}")
    public R<List<CategoryDetailRespVO.BreadcrumbItem>> getBreadcrumb(@Parameter(description = "分类ID") @PathVariable Long id) {
        return R.success(toBreadcrumbItems(categoryService.listAncestors(id)));
    }

    @Operation(summary = "更新分类启用状态")
    @PostMapping("/enable-status/{id}")
    public R<Integer> updateEnableStatus(@Parameter(description = "分类ID") @PathVariable Long id,
                                         @Parameter(description = "启用状态：0-禁用，1-启用") @RequestParam Integer enableStatus) {
        int count = categoryService.updateEnableStatus(id, enableStatus);
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "批量更新分类启用状态")
    @PostMapping("/enable-status/batch")
    public R<Integer> updateEnableStatusBatch(@RequestBody List<Long> ids,
                                              @Parameter(description = "启用状态：0-禁用，1-启用") @RequestParam Integer enableStatus) {
        int count = categoryService.updateEnableStatusBatch(ids, enableStatus);
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "更新导航显示状态")
    @PostMapping("/nav/{id}")
    public R<Integer> updateNavStatus(@Parameter(description = "分类ID") @PathVariable Long id,
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

    @Operation(summary = "获取分类快照", description = "一次请求返回 category + specs + params + brands")
    @GetMapping("/snapshot/{categoryId}")
    public R<CategoryConfigSnapshotRespVO> getCategoryConfigSnapshot(@Parameter(description = "分类ID") @PathVariable Long categoryId) {
        return R.success(categoryService.getCategoryConfigSnapshot(categoryId));
    }

    private List<CategoryListRespVO> toListRespVOWithChildCount(List<Category> categories) {
        if (categories == null || categories.isEmpty()) {
            return new ArrayList<>();
        }
        List<CategoryListRespVO> respVOList = categoryConvert.entityListToListRespVOList(categories);
        List<Long> categoryIds = categories.stream().map(Category::getId).collect(Collectors.toList());
        Map<Long, Long> childCountMap = categoryService.countChildrenByParentIds(categoryIds);
        for (CategoryListRespVO respVO : respVOList) {
            respVO.setChildCount(childCountMap.getOrDefault(respVO.getId(), 0L).intValue());
        }
        return respVOList;
    }

    private List<CategoryDetailRespVO.BreadcrumbItem> toBreadcrumbItems(List<Category> ancestors) {
        if (ancestors == null || ancestors.isEmpty()) {
            return new ArrayList<>();
        }
        return ancestors.stream()
                .map(category -> CategoryDetailRespVO.BreadcrumbItem.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .level(category.getLevel())
                        .build())
                .collect(Collectors.toList());
    }
}
