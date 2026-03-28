package com.mallease.product.controller.admin.brand;

import com.github.pagehelper.PageHelper;
import com.mallease.common.api.Page;
import com.mallease.common.api.PageUtils;
import com.mallease.common.api.R;
import com.mallease.common.api.ResultCode;
import com.mallease.common.dto.remote.BrandDTO;
import com.mallease.product.controller.admin.brand.vo.BrandDetailRespVO;
import com.mallease.product.controller.admin.brand.vo.BrandListRespVO;
import com.mallease.product.controller.admin.brand.vo.BrandPageReqVO;
import com.mallease.product.controller.admin.brand.vo.BrandRelationBatchUnbindReqVO;
import com.mallease.product.controller.admin.brand.vo.BrandSaveReqVO;
import com.mallease.product.controller.admin.brand.vo.CategoryBrandRelationSaveReqVO;
import com.mallease.product.convert.brand.BrandConvert;
import com.mallease.product.dal.entity.Brand;
import com.mallease.product.service.brand.BrandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 后台品牌管理
 */
@Tag(name = "后台品牌管理", description = "品牌增删改查、状态管理")
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/brand")
public class BrandAdminController {

    private final BrandService brandService;
    private final BrandConvert brandConvert;

    @Operation(summary = "查询品牌列表", description = "支持分页、模糊搜索、状态筛选")
    @GetMapping("/list")
    public R<Page<BrandListRespVO>> page(@Validated @ModelAttribute BrandPageReqVO reqVO) {
        PageHelper.startPage(reqVO.getPageNum(), reqVO.getPageSize());
        List<Brand> brandList = brandService.page(reqVO);
        return R.success(PageUtils.convertPage(brandList, brandConvert::toBrandListRespList));
    }

    @Operation(summary = "创建品牌")
    @PostMapping("/create")
    public R<Long> create(@Validated(BrandSaveReqVO.Create.class) @RequestBody BrandSaveReqVO reqVO) {
        return R.success(brandService.create(reqVO));
    }

    @Operation(summary = "获取品牌详情")
    @GetMapping("/{id}")
    public R<BrandDetailRespVO> get(@Parameter(description = "品牌ID") @PathVariable Long id) {
        return R.success(brandConvert.toBrandDetailResp(brandService.get(id)));
    }

    @Operation(summary = "更新品牌")
    @PostMapping("/update/{id}")
    public R<Integer> update(@Parameter(description = "品牌ID") @PathVariable Long id,
                             @Validated(BrandSaveReqVO.Update.class) @RequestBody BrandSaveReqVO reqVO) {
        reqVO.setId(id);
        return R.success(brandService.update(reqVO));
    }

    @Operation(summary = "删除品牌")
    @DeleteMapping("/delete/{id}")
    public R<Integer> delete(@Parameter(description = "品牌ID") @PathVariable Long id) {
        return R.success(brandService.delete(id));
    }

    @Operation(summary = "批量更新显示状态", description = "批量修改品牌的显示/隐藏状态")
    @PostMapping("/update/showStatus")
    public R<Integer> updateShowStatus(@Parameter(description = "品牌ID列表") @RequestParam("ids") List<Long> ids,
                                       @Parameter(description = "显示状态(0:隐藏 1:显示)") @RequestParam("showStatus") Integer showStatus) {
        int count = brandService.updateShowStatusBatch(ids, showStatus);
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "批量更新制造商状态", description = "批量修改品牌的制造商标识")
    @PostMapping("/update/factoryStatus")
    public R<Integer> updateFactoryStatus(@Parameter(description = "品牌ID列表") @RequestParam("ids") List<Long> ids,
                                          @Parameter(description = "制造商状态(0:否 1:是)") @RequestParam("factoryStatus") Integer factoryStatus) {
        int count = brandService.updateFactoryStatusBatch(ids, factoryStatus);
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "为分类关联品牌")
    @PostMapping("/bindCategory")
    public R<Integer> bindCategory(@Validated @RequestBody CategoryBrandRelationSaveReqVO reqVO) {
        return R.success(brandService.bindCategory(reqVO));
    }

    @Operation(summary = "批量为分类关联品牌")
    @PostMapping("/bindCategory/batch/{categoryId}")
    public R<Integer> bindCategoryBatch(@PathVariable Long categoryId,
                                        @RequestBody List<Long> brandIds) {
        return R.success(brandService.bindCategoryBatch(categoryId, brandIds));
    }

    @Operation(summary = "解除分类与品牌的关联")
    @PostMapping("/unbindCategory")
    public R<Integer> unbindCategory(@RequestParam Long categoryId, @RequestParam Long brandId) {
        return R.success(brandService.unbindCategory(categoryId, brandId));
    }

    @Operation(summary = "查询分类已关联的品牌")
    @GetMapping("/listByCategory/{categoryId}")
    public R<List<BrandListRespVO>> listByCategory(@PathVariable Long categoryId) {
        return R.success(brandConvert.toBrandListRespList(brandService.listByCategory(categoryId)));
    }

    @Operation(summary = "查询分类未关联的品牌（供勾选弹窗）")
    @GetMapping("/listUnbind/{categoryId}")
    public R<List<BrandListRespVO>> listUnbindByCategory(@PathVariable Long categoryId) {
        return R.success(brandConvert.toBrandListRespList(brandService.listUnbindByCategory(categoryId)));
    }

    @Operation(summary = "从父分类复制品牌关联")
    @PostMapping("/copyFromParent")
    public R<Integer> copyFromParent(@RequestParam Long parentCategoryId, @RequestParam Long childCategoryId) {
        return R.success(brandService.copyFromParent(parentCategoryId, childCategoryId));
    }

    @Operation(summary = "批量解绑品牌")
    @PostMapping("/unbindCategory/batch")
    public R<Integer> unbindCategoryBatch(@Validated @RequestBody BrandRelationBatchUnbindReqVO reqVO) {
        return R.success(brandService.unbindCategoryBatch(reqVO));
    }
}
