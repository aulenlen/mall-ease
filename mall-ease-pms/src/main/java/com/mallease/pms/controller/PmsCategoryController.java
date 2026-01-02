package com.mallease.pms.controller;

import com.mallease.common.api.R;
import com.mallease.common.api.ResultCode;
import com.mallease.common.constant.PmsRedisKeys;
import com.mallease.common.service.RedisService;
import com.mallease.pms.converter.PmsCategoryConverter;
import com.mallease.pms.dto.cmd.CreatePmsCategoryCmd;
import com.mallease.pms.dto.cmd.UpdatePmsCategoryCmd;
import com.mallease.pms.dto.query.PmsCategoryQuery;
import com.mallease.pms.dto.vo.PmsCategoryDetailVO;
import com.mallease.pms.dto.vo.PmsCategoryListVO;
import com.mallease.pms.dto.vo.PmsCategoryTreeVO;
import com.mallease.pms.pojo.PmsCategory;
import com.mallease.pms.service.PmsCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/pms/category")
public class PmsCategoryController {

    @Autowired
    private PmsCategoryService categoryService;

    @Autowired
    private PmsCategoryConverter categoryConverter;

    @Operation(summary = "创建分类")
    @PostMapping("/create")
    public R<Long> create(@Validated @RequestBody CreatePmsCategoryCmd cmd) {
        PmsCategory entity = categoryConverter.createCmdToEntity(cmd);
        Long id = categoryService.create(entity, cmd.getParentId());
        return R.success(id);
    }

    @Operation(summary = "更新分类")
    @PostMapping("/update")
    public R<Integer> update(@Validated @RequestBody UpdatePmsCategoryCmd cmd) {
        PmsCategory entity = categoryService.getById(cmd.getId());
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
    public R<PmsCategoryDetailVO> getById(
            @Parameter(description = "分类ID") @PathVariable Long id) {
        PmsCategory category = categoryService.getById(id);
        if (category == null) {
            return R.success(null);
        }

        PmsCategoryDetailVO vo = categoryConverter.entityToDetailVo(category);

        if (category.getParentId() != null && category.getParentId() > 0) {
            PmsCategory parent = categoryService.getById(category.getParentId());
            if (parent != null) {
                vo.setParentName(parent.getName());
            }
        }

        vo.setBreadcrumb(toBreadcrumbItems(categoryService.listAncestors(id)));

        return R.success(vo);
    }

    @Operation(summary = "获取子分类列表")
    @GetMapping("/children/{parentId}")
    public R<List<PmsCategoryListVO>> listByParentId(
            @Parameter(description = "父分类ID，0表示一级分类") @PathVariable Long parentId) {
        List<PmsCategory> categories = categoryService.listByParentId(parentId);
        List<PmsCategoryListVO> voList = toListVoWithChildCount(categories);
        return R.success(voList);
    }

    @Operation(summary = "获取所有子分类")
    @GetMapping("/descendants/{id}")
    public R<List<PmsCategoryListVO>> listDescendants(
            @Parameter(description = "分类ID") @PathVariable Long id) {
        List<PmsCategory> descendants = categoryService.listDescendants(id);
        List<PmsCategoryListVO> voList = categoryConverter.entityListToListVoList(descendants);
        return R.success(voList);
    }

    @Operation(summary = "按层级查询分类")
    @GetMapping("/level/{level}")
    public R<List<PmsCategoryListVO>> listByLevel(
            @Parameter(description = "层级：0=一级，1=二级，2=三级") @PathVariable Integer level) {
        List<PmsCategory> categories = categoryService.listByLevel(level);
        List<PmsCategoryListVO> voList = categoryConverter.entityListToListVoList(categories);
        return R.success(voList);
    }

    @Operation(summary = "获取完整分类树")
    @GetMapping("/tree")
    public R<List<PmsCategoryTreeVO>> getFullTree() {
        List<PmsCategory> allCategories = categoryService.listAll();
        List<PmsCategoryTreeVO> tree = categoryConverter.buildTree(allCategories);
        return R.success(tree);
    }

    @Operation(summary = "获取分类树（支持筛选）")
    @GetMapping("/tree/query")
    public R<List<PmsCategoryTreeVO>> getTree(PmsCategoryQuery query) {
        List<PmsCategory> categories = categoryService.listByQuery(query);
        List<PmsCategoryTreeVO> tree = categoryConverter.buildTree(categories);
        return R.success(tree);
    }

    @Operation(summary = "金刚区分类")
    @GetMapping("/nav")
    public R<List<PmsCategoryListVO>> listNavCategories() {
        List<PmsCategory> categoryList = categoryService.listNavCategories();
        return R.success(categoryConverter.entityListToListVoList(categoryList));
    }

    @Operation(summary = "获取导航分类树")
    @GetMapping("/tree/nav")
    public R<List<PmsCategoryTreeVO>> getNavTree() {
        List<PmsCategory> navCategories = categoryService.listNavCategories();
        List<PmsCategoryTreeVO> tree = categoryConverter.buildTree(navCategories);
        return R.success(tree);
    }

    @Operation(summary = "获取面包屑路径")
    @GetMapping("/breadcrumb/{id}")
    public R<List<PmsCategoryDetailVO.BreadcrumbItem>> getBreadcrumb(
            @Parameter(description = "分类ID") @PathVariable Long id) {
        List<PmsCategory> ancestors = categoryService.listAncestors(id);
        List<PmsCategoryDetailVO.BreadcrumbItem> breadcrumb = toBreadcrumbItems(ancestors);
        return R.success(breadcrumb);
    }

    @Operation(summary = "更新分类状态")
    @PostMapping("/status/{id}")
    public R<Integer> updateStatus(
            @Parameter(description = "分类ID") @PathVariable Long id,
            @Parameter(description = "状态：0-禁用，1-启用") @RequestParam Integer status) {
        int count = categoryService.updateStatus(id, status);
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "批量更新分类状态")
    @PostMapping("/status/batch")
    public R<Integer> updateStatusBatch(
            @RequestBody List<Long> ids,
            @Parameter(description = "状态：0-禁用，1-启用") @RequestParam Integer status) {
        int count = categoryService.updateStatusBatch(ids, status);
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

    /**
     * 将分类实体列表转换为ListVO列表并填充子分类数量
     */
    private List<PmsCategoryListVO> toListVoWithChildCount(List<PmsCategory> categories) {
        if (categories == null || categories.isEmpty()) {
            return new ArrayList<>();
        }

        List<PmsCategoryListVO> voList = categoryConverter.entityListToListVoList(categories);

        // 批量统计子分类数量
        List<Long> categoryIds = categories.stream()
                .map(PmsCategory::getId)
                .collect(Collectors.toList());
        Map<Long, Long> childCountMap = categoryService.countChildrenByParentIds(categoryIds);

        for (PmsCategoryListVO vo : voList) {
            vo.setChildCount(childCountMap.getOrDefault(vo.getId(), 0L).intValue());
        }

        return voList;
    }

    /**
     * 将祖先分类列表转换为面包屑项列表
     */
    private List<PmsCategoryDetailVO.BreadcrumbItem> toBreadcrumbItems(List<PmsCategory> ancestors) {
        if (ancestors == null || ancestors.isEmpty()) {
            return new ArrayList<>();
        }

        return ancestors.stream()
                .map(c -> PmsCategoryDetailVO.BreadcrumbItem.builder()
                        .id(c.getId())
                        .name(c.getName())
                        .level(c.getLevel())
                        .build())
                .collect(Collectors.toList());
    }
}