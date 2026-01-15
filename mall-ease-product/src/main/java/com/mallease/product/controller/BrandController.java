package com.mallease.product.controller;

import com.github.pagehelper.PageHelper;
import com.mallease.common.api.Page;
import com.mallease.common.api.PageUtils;
import com.mallease.common.api.R;
import com.mallease.common.api.ResultCode;
import com.mallease.common.dto.remote.BrandDTO;
import com.mallease.product.converter.BrandConverter;
import com.mallease.product.model.client.cmd.BatchUnbindBrandCmd;
import com.mallease.product.model.client.cmd.BrandCmd;
import com.mallease.product.model.client.query.BrandQuery;
import com.mallease.product.model.client.vo.BrandDetailVO;

import com.mallease.product.model.client.vo.BrandListVO;
import com.mallease.product.model.data.entity.Brand;
import com.mallease.product.service.BrandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 品牌管理 Controller
 *
 * @author: Aulen
 * @create: 2025-11-15
 */
@Tag(name = "品牌管理", description = "品牌增删改查、状态管理")
@Slf4j
@RestController
@RequestMapping("/product/brand")
public class BrandController {

    @Autowired
    private BrandService brandService;

    @Autowired
    private BrandConverter brandConverter;

    /**
     * 获取品牌列表（支持分页和模糊搜索品牌名）
     *
     * @param query 查询条件
     * @return 分页结果
     */
    @Operation(summary = "查询品牌列表", description = "支持分页、模糊搜索、状态筛选")
    @GetMapping("/list")
    public R<Page<BrandListVO>> list(@Validated @ModelAttribute BrandQuery query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<Brand> brandList = brandService.list(query.getKeyword());
        Page<BrandListVO> result = PageUtils.convertPage(brandList, brandConverter::entityListToListVoList);
        return R.success(result);
    }

    @Operation(summary = "创建品牌")
    @PostMapping("/create")
    public R<Long> create(@Validated(BrandCmd.Create.class) @RequestBody BrandCmd cmd) {
        Brand brand = brandConverter.cmdToEntity(cmd);
        Long brandId = brandService.create(brand);
        return R.success(brandId);
    }

    /**
     * 根据ID获取品牌详情
     *
     * @param id 品牌ID
     * @return 品牌详情信息
     */
    @Operation(summary = "获取品牌详情")
    @GetMapping("/{id}")
    public R<BrandDetailVO> getById(@Parameter(description = "品牌ID") @PathVariable Long id) {
        Brand brand = brandService.getById(id);
        if (brand == null) {
            return R.failed(ResultCode.FAILED);
        }

        BrandDetailVO detailVO = brandConverter.entityToDetailVo(brand);
        return R.success(detailVO);
    }

    @Operation(summary = "更新品牌")
    @PostMapping("/update/{id}")
    public R<Integer> update(@Parameter(description = "品牌ID") @PathVariable Long id,
                             @Validated(BrandCmd.Update.class) @RequestBody BrandCmd cmd) {
        Brand brand = brandService.getById(id);
        if (brand == null) {
            return R.failed(ResultCode.FAILED);
        }
        brandConverter.updateEntityFromCmd(brand, cmd);
        int count = brandService.update(brand);
        return R.success(count);
    }

    /**
     * 删除品牌
     *
     * @param id 品牌ID
     * @return 删除结果
     */
    @Operation(summary = "删除品牌")
    @DeleteMapping("/delete/{id}")
    public R<Integer> delete(@Parameter(description = "品牌ID") @PathVariable Long id) {
        return R.success(brandService.delete(id));
    }

    /**
     * 批量更新品牌显示状态
     *
     * @param ids        品牌ID列表
     * @param showStatus 显示状态（0->隐藏；1->显示）
     * @return 更新结果
     */
    @Operation(summary = "批量更新显示状态", description = "批量修改品牌的显示/隐藏状态")
    @PostMapping("/update/showStatus")
    public R<Integer> updateShowStatus(@Parameter(description = "品牌ID列表") @RequestParam(value = "ids") List<Long> ids,
                                       @Parameter(description = "显示状态(0:隐藏 1:显示)") @RequestParam(value = "showStatus") Integer showStatus) {
        int count = brandService.updateShowStatusBatch(ids, showStatus);
        if (count > 0) {
            return R.success(count);
        }
        return R.failed(ResultCode.FAILED);
    }

    /**
     * 批量更新品牌厂家制造商状态
     *
     * @param ids           品牌ID列表
     * @param factoryStatus 厂家制造商状态（0->不是；1->是）
     * @return 更新结果
     */
    @Operation(summary = "批量更新制造商状态", description = "批量修改品牌的制造商标识")
    @PostMapping("/update/factoryStatus")
    public R<Integer> updateFactoryStatus(
            @Parameter(description = "品牌ID列表") @RequestParam(value = "ids") List<Long> ids,
            @Parameter(description = "制造商状态(0:否 1:是)") @RequestParam(value = "factoryStatus") Integer factoryStatus) {
        int count = brandService.updateFactoryStatusBatch(ids, factoryStatus);
        if (count > 0) {
            return R.success(count);
        }
        return R.failed(ResultCode.FAILED);
    }

    // 分类关联品牌

    @Operation(summary = "为分类关联品牌")
    @PostMapping("/bindCategory")
    public R<Integer> bindCategory(@RequestParam Long categoryId, @RequestParam Long brandId) {
        return R.success(brandService.bindCategory(categoryId, brandId));
    }

    @Operation(summary = "批量为分类关联品牌")
    @PostMapping("/bindCategory/batch/{categoryId}")
    public R<Integer> bindCategoryBatch(@PathVariable Long categoryId, @RequestBody List<Long> brandIds) {
        return R.success(brandService.bindCategoryBatch(categoryId, brandIds));
    }

    @Operation(summary = "解除分类与品牌的关联")
    @PostMapping("/unbindCategory")
    public R<Integer> unbindCategory(@RequestParam Long categoryId, @RequestParam Long brandId) {
        return R.success(brandService.unbindCategory(categoryId, brandId));
    }

    @Operation(summary = "查询分类已关联的品牌")
    @GetMapping("/listByCategory/{categoryId}")
    public R<List<BrandListVO>> listByCategory(@PathVariable Long categoryId) {
        List<Brand> brands = brandService.listByCategory(categoryId);
        return R.success(brandConverter.entityListToListVoList(brands));
    }

    @Operation(summary = "查询分类未关联的品牌（供勾选弹窗）")
    @GetMapping("/listUnbind/{categoryId}")
    public R<List<BrandListVO>> listUnbindByCategory(@PathVariable Long categoryId) {
        List<Brand> brands = brandService.listUnbindByCategory(categoryId);
        return R.success(brandConverter.entityListToListVoList(brands));
    }

    @Operation(summary = "从父分类复制品牌关联")
    @PostMapping("/copyFromParent")
    public R<Integer> copyFromParent(@RequestParam Long parentCategoryId, @RequestParam Long childCategoryId) {
        return R.success(brandService.copyFromParent(parentCategoryId, childCategoryId));
    }

    @Operation(summary = "批量解绑品牌")
    @PostMapping("/unbindCategory/batch")
    public R<Integer> unbindCategoryBatch(@Validated @RequestBody BatchUnbindBrandCmd cmd) {
        return R.success(brandService.unbindCategoryBatch(cmd.getCategoryId(), cmd.getBrandIds()));
    }

    // 内部调用
    @Operation(summary = "获取启用的品牌列表", description = "内部调用")
    @GetMapping("/internal/list")
    public R<List<BrandDTO>> listEnabledBrands() {
        return R.success(brandService.listEnabledBrands());
    }

}
