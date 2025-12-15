package com.mallease.pms.controller;

import com.mallease.common.api.R;
import com.mallease.common.api.ResultCode;
import com.mallease.pms.dto.cmd.CreatePmsCategoryCmd;
import com.mallease.pms.dto.cmd.UpdatePmsCategoryCmd;
import com.mallease.pms.dto.query.PmsCategoryQuery;
import com.mallease.pms.dto.vo.PmsCategoryDetailVO;
import com.mallease.pms.dto.vo.PmsCategoryListVO;
import com.mallease.pms.dto.vo.PmsCategoryTreeVO;
import com.mallease.pms.service.PmsCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品分类管理控制器
 * <p>
 * 提供分类的 CRUD、树形查询、状态管理等 API
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

    @Operation(summary = "创建分类")
    @PostMapping("/create")
    public R<Long> create(@Validated @RequestBody CreatePmsCategoryCmd cmd) {
        Long id = categoryService.create(cmd);
        return R.success(id);
    }

    @Operation(summary = "更新分类")
    @PostMapping("/update")
    public R<Integer> update(@Validated @RequestBody UpdatePmsCategoryCmd cmd) {
        int count = categoryService.update(cmd);
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
        PmsCategoryDetailVO vo = categoryService.getById(id);
        return R.success(vo);
    }

    @Operation(summary = "获取子分类列表")
    @GetMapping("/children/{parentId}")
    public R<List<PmsCategoryListVO>> listByParentId(
            @Parameter(description = "父分类ID，0表示一级分类") @PathVariable Long parentId) {
        List<PmsCategoryListVO> list = categoryService.listByParentId(parentId);
        return R.success(list);
    }

    @Operation(summary = "获取所有子分类")
    @GetMapping("/descendants/{id}")
    public R<List<PmsCategoryListVO>> listDescendants(
            @Parameter(description = "分类ID") @PathVariable Long id) {
        List<PmsCategoryListVO> list = categoryService.listDescendants(id);
        return R.success(list);
    }

    @Operation(summary = "按层级查询分类")
    @GetMapping("/level/{level}")
    public R<List<PmsCategoryListVO>> listByLevel(
            @Parameter(description = "层级：0=一级，1=二级，2=三级") @PathVariable Integer level) {
        List<PmsCategoryListVO> list = categoryService.listByLevel(level);
        return R.success(list);
    }

    @Operation(summary = "获取完整分类树")
    @GetMapping("/tree")
    public R<List<PmsCategoryTreeVO>> getFullTree() {
        List<PmsCategoryTreeVO> tree = categoryService.getFullTree();
        return R.success(tree);
    }

    @Operation(summary = "获取分类树（支持筛选）")
    @GetMapping("/tree/query")
    public R<List<PmsCategoryTreeVO>> getTree(PmsCategoryQuery query) {
        List<PmsCategoryTreeVO> tree = categoryService.getTree(query);
        return R.success(tree);
    }

    @Operation(summary = "获取导航分类树")
    @GetMapping("/tree/nav")
    public R<List<PmsCategoryTreeVO>> getNavTree() {
        List<PmsCategoryTreeVO> tree = categoryService.getNavTree();
        return R.success(tree);
    }

    @Operation(summary = "获取面包屑路径")
    @GetMapping("/breadcrumb/{id}")
    public R<List<PmsCategoryDetailVO.BreadcrumbItem>> getBreadcrumb(
            @Parameter(description = "分类ID") @PathVariable Long id) {
        List<PmsCategoryDetailVO.BreadcrumbItem> breadcrumb = categoryService.getBreadcrumb(id);
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
}