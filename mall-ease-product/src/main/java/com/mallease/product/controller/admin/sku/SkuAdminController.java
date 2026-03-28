package com.mallease.product.controller.admin.sku;

import com.github.pagehelper.PageHelper;
import com.mallease.common.api.Page;
import com.mallease.common.api.PageUtils;
import com.mallease.common.api.R;
import com.mallease.common.api.ResultCode;
import com.mallease.product.controller.admin.sku.vo.SkuPageReqVO;
import com.mallease.product.controller.admin.sku.vo.SkuRespVO;
import com.mallease.product.controller.admin.sku.vo.SkuSaveReqVO;
import com.mallease.product.convert.sku.SkuConvert;
import com.mallease.product.dal.entity.Sku;
import com.mallease.product.service.sku.SkuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台 SKU 管理
 */
@Tag(name = "后台 SKU 管理", description = "SKU 增删改查")
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/skus")
public class SkuAdminController {

    private final SkuService skuService;
    private final SkuConvert skuConvert;

    @Operation(summary = "创建 SKU", description = "在指定 SPU 下创建单个 SKU（含库存）")
    @PostMapping("/spus/{spuId}")
    public R<Long> create(@Parameter(description = "SPU ID") @PathVariable Long spuId,
                          @Validated(SkuSaveReqVO.Create.class) @RequestBody SkuSaveReqVO reqVO) {
        return R.success(skuService.create(spuId, reqVO));
    }

    @Operation(summary = "更新 SKU", description = "更新 SKU 基础信息（不含库存、促销）")
    @PutMapping
    public R<Integer> update(@Validated(SkuSaveReqVO.Update.class) @RequestBody SkuSaveReqVO reqVO) {
        return R.success(skuService.update(reqVO));
    }

    @Operation(summary = "SKU 列表", description = "支持分页、多条件查询")
    @GetMapping
    public R<Page<SkuRespVO>> page(@Validated @ModelAttribute SkuPageReqVO reqVO) {
        PageHelper.startPage(reqVO.getPageNum(), reqVO.getPageSize());
        List<Sku> skuList = skuService.page(reqVO);
        List<SkuRespVO> respVOList = skuConvert.toSkuRespList(skuList);
        Page<SkuRespVO> result = PageUtils.buildPage(skuList, respVOList);
        return R.success(result);
    }

    @Operation(summary = "获取 SKU 详情")
    @GetMapping("/{id}")
    public R<SkuRespVO> get(@Parameter(description = "SKU ID") @PathVariable Long id) {
        Sku sku = skuService.get(id);
        if (sku == null) {
            return R.failed("SKU不存在");
        }
        return R.success(skuConvert.toSkuResp(sku));
    }

    @Operation(summary = "删除 SKU", description = "级联删除库存、促销、价格策略")
    @DeleteMapping("/{id}")
    public R<Integer> delete(@Parameter(description = "SKU ID") @PathVariable Long id) {
        int count = skuService.delete(id);
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "批量更新启用状态")
    @PutMapping("/enable-status")
    public R<Integer> updateEnableStatus(
            @Parameter(description = "SKU ID 列表") @RequestParam List<Long> ids,
            @Parameter(description = "启用状态值: 0-禁用, 1-启用") @RequestParam Integer enableStatus) {
        return R.success(skuService.updateEnableStatus(ids, enableStatus));
    }

    @Operation(summary = "根据 SPU 获取 SKU 列表", description = "查询某个 SPU 下的所有 SKU")
    @GetMapping("/spus/{spuId}")
    public R<List<SkuRespVO>> listBySpuId(@Parameter(description = "SPU ID") @PathVariable Long spuId) {
        List<Sku> skuList = skuService.listBySpuId(spuId);
        return R.success(skuConvert.toSkuRespList(skuList));
    }
}
