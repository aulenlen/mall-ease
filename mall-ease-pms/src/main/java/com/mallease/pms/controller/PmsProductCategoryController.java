package com.mallease.pms.controller;

import com.github.pagehelper.PageHelper;
import com.mallease.common.api.Page;
import com.mallease.common.api.PageUtils;
import com.mallease.common.api.ResultCode;
import com.mallease.common.api.R;
import com.mallease.pms.converter.PmsProductCategoryConverter;
import com.mallease.pms.dto.cmd.CreateProductCategoryCmd;
import com.mallease.pms.dto.cmd.UpdateProductCategoryCmd;
import com.mallease.pms.dto.vo.PmsProductCategoryDetailVO;
import com.mallease.pms.dto.vo.PmsProductCategoryListVO;
import com.mallease.pms.dto.vo.PmsProductCategoryWithChildrenVO;
import com.mallease.pms.pojo.PmsProductCategory;
import com.mallease.pms.service.PmsProductCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品分类控制器
 *
 * @author: Aulen
 * @create: 2025-11-15
 */
@Tag(name = "商品分类管理", description = "商品分类增删改查、状态管理")
@RestController
@RequestMapping("/pms/productCategory")
public class PmsProductCategoryController {

    @Autowired
    private PmsProductCategoryService productCategoryService;

    @Autowired
    private PmsProductCategoryConverter categoryConverter;

    @Operation(summary = "创建商品分类")
    @PostMapping("/create")
    public R<Integer> create(@Validated @RequestBody CreateProductCategoryCmd cmd) {
        Integer count = productCategoryService.create(cmd);
        return R.success(count);
    }

    @Operation(summary = "获取商品分类详情")
    @GetMapping("/{id}")
    public R<PmsProductCategoryDetailVO> getById(@Parameter(description = "分类ID") @PathVariable Long id) {
        PmsProductCategory category = productCategoryService.getById(id);
        PmsProductCategoryDetailVO detailVO = categoryConverter.entityToDetailVo(category);
        return R.success(detailVO);
    }

    @Operation(summary = "分页查询商品分类", description = "根据父级ID分页查询商品分类")
    @GetMapping("/list/{parentId}")
    public R<Page<PmsProductCategoryListVO>> list(
            @Parameter(description = "父级ID") @PathVariable Long parentId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "5") Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<PmsProductCategory> categoryList = productCategoryService.listByParentId(parentId);
        Page<PmsProductCategoryListVO> result = PageUtils.convertPage(categoryList, categoryConverter::entityListToListVoList);
        return R.success(result);
    }

    @Operation(summary = "批量更新导航栏显示状态")
    @PostMapping("/update/navStatus")
    public R<Integer> updateNavStatus(
            @Parameter(description = "分类ID列表") @RequestParam List<Long> ids,
            @Parameter(description = "导航栏显示状态(0:不显示 1:显示)") @RequestParam Integer navStatus) {
        int count = productCategoryService.updateNavStatusBatch(ids, navStatus);
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "批量更新显示状态")
    @PostMapping("/update/showStatus")
    public R<Integer> updateShowStatus(
            @Parameter(description = "分类ID列表") @RequestParam List<Long> ids,
            @Parameter(description = "显示状态(0:不显示 1:显示)") @RequestParam Integer showStatus) {
        int count = productCategoryService.updateShowStatusBatch(ids, showStatus);
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "更新商品分类")
    @PostMapping("/update/{id}")
    public R<Integer> update(
            @Parameter(description = "分类ID") @PathVariable Long id,
            @Validated @RequestBody UpdateProductCategoryCmd cmd) {
        Integer count = productCategoryService.update(id, cmd);
        return R.success(count);
    }

    @Operation(summary = "删除商品分类")
    @DeleteMapping("/delete/{id}")
    public R<Integer> delete(@Parameter(description = "分类ID") @PathVariable Long id) {
        Integer count = productCategoryService.delete(id);
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "查询所有一级分类及其子分类")
    @GetMapping("/list/withChildren")
    public R<List<PmsProductCategoryWithChildrenVO>> listWithChildren() {
        List<PmsProductCategoryWithChildrenVO> list = productCategoryService.listWithChildren();
        return R.success(list);
    }
}
