package com.mallease.product.controller.admin.stock;

import com.github.pagehelper.PageHelper;
import com.mallease.common.api.Page;
import com.mallease.common.api.PageUtils;
import com.mallease.common.api.R;
import com.mallease.common.api.ResultCode;
import com.mallease.common.dto.remote.BrandDTO;
import com.mallease.product.controller.admin.stock.vo.*;
import com.mallease.product.convert.stock.SkuStockConvert;
import com.mallease.product.dal.entity.Category;
import com.mallease.product.dal.entity.SkuStock;
import com.mallease.product.service.brand.BrandService;
import com.mallease.product.service.category.CategoryService;
import com.mallease.product.service.stock.SkuStockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台 SKU 库存管理
 */
@Tag(name = "后台 SKU 库存管理", description = "库存查询、调整、预警、锁库存")
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/stocks")
public class SkuStockAdminController {

    private final SkuStockService skuStockService;
    private final SkuStockConvert skuStockConvert;
    private final BrandService brandService;
    private final CategoryService categoryService;

    @Operation(summary = "创建库存记录", description = "为指定 SKU 创建库存记录")
    @PostMapping
    public R<Long> create(@Validated(SkuStockSaveReqVO.Create.class) @RequestBody SkuStockSaveReqVO reqVO) {
        return R.success(skuStockService.create(reqVO));
    }

    @Operation(summary = "更新库存信息", description = "更新库存基础信息（预警值、状态等）")
    @PutMapping
    public R<Integer> update(@Validated(SkuStockSaveReqVO.Update.class) @RequestBody SkuStockSaveReqVO reqVO) {
        return R.success(skuStockService.update(reqVO));
    }

    @Operation(summary = "批量更新库存信息", description = "批量更新库存基础信息，供独立库存页保存使用")
    @PutMapping("/batch")
    public R<Integer> updateBatch(@RequestBody List<SkuStockSaveReqVO> reqList) {
        return R.success(skuStockService.updateBatch(reqList));
    }

    @Operation(summary = "分页查询库存列表", description = "SKU 维度平铺展示，每行一条 SKU 库存记录")
    @GetMapping
    public R<Page<SkuStockRespVO>> page(@Validated @ModelAttribute SkuStockPageReqVO reqVO) {
        PageHelper.startPage(reqVO.getPageNum(), reqVO.getPageSize());
        List<SkuStockRespVO> respVOList = skuStockService.page(reqVO);
        return R.success(PageUtils.buildPage(respVOList, respVOList));
    }

    @Operation(summary = "库存页筛选项", description = "只返回当前真实数据模型支持的品牌、分类、库存状态和页签")
    @GetMapping("/filter-options")
    public R<InventoryFilterOptionsRespVO> filterOptions() {
        List<InventoryFilterOptionsRespVO.OptionItemVO> brandOptions = brandService.listEnabledBrands().stream()
                .map(this::toBrandOption)
                .toList();
        List<InventoryFilterOptionsRespVO.OptionItemVO> categoryOptions = categoryService.listAll().stream()
                .filter(category -> category.getDeleted() == null || category.getDeleted() == 0)
                .filter(category -> category.getEnableStatus() == null || category.getEnableStatus() == 1)
                .map(this::toCategoryOption)
                .toList();

        InventoryFilterOptionsRespVO respVO = InventoryFilterOptionsRespVO.builder()
                .brands(brandOptions)
                .categories(categoryOptions)
                .stockStatuses(List.of(
                        option("0", "无货"),
                        option("1", "有货"),
                        option("2", "预售")
                ))
                .tabs(List.of(
                        option("all", "全部"),
                        option("warning", "低库存"),
                        option("empty", "缺货"),
                        option("presale", "预售")
                ))
                .build();
        return R.success(respVO);
    }

    @Operation(summary = "分页查询库存日志", description = "独立库存页日志抽屉使用")
    @GetMapping("/logs")
    public R<Page<SkuStockLogRespVO>> logPage(@Validated @ModelAttribute SkuStockLogPageReqVO reqVO) {
        PageHelper.startPage(reqVO.getPageNum(), reqVO.getPageSize());
        List<SkuStockLogRespVO> respVOList = skuStockService.logPage(reqVO);
        return R.success(PageUtils.buildPage(respVOList, respVOList));
    }

    @Operation(summary = "根据 SKU 获取库存")
    @GetMapping("/by-sku/{skuId}")
    public R<SkuStockRespVO> getBySkuId(@Parameter(description = "SKU ID") @PathVariable Long skuId) {
        SkuStock stock = skuStockService.getBySkuId(skuId);
        if (stock == null) {
            return R.failed("库存记录不存在");
        }
        return R.success(skuStockConvert.toSkuStockResp(stock));
    }

    @Operation(summary = "调整库存", description = "手动入库/出库操作")
    @PutMapping("/adjustments")
    public R<Integer> adjustStock(
            @Parameter(description = "SKU ID") @RequestParam Long skuId,
            @Parameter(description = "调整数量（正数入库，负数出库）") @RequestParam Integer quantity) {
        int count = skuStockService.adjustStock(skuId, quantity);
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "库存预警列表", description = "查询库存低于预警值的 SKU")
    @GetMapping("/warnings")
    public R<List<SkuStockRespVO>> listLowStockWarning() {
        List<SkuStock> stockList = skuStockService.listLowStockWarning();
        return R.success(skuStockConvert.toSkuStockRespList(stockList));
    }

    @Operation(summary = "批量更新库存状态")
    @PutMapping("/status")
    public R<Integer> updateStockStatusBatch(
            @Parameter(description = "SKU ID 列表") @RequestParam List<Long> skuIds,
            @Parameter(description = "库存状态: 0-无货, 1-有货, 2-预售") @RequestParam Integer stockStatus) {
        return R.success(skuStockService.updateStockStatusBatch(skuIds, stockStatus));
    }

    private InventoryFilterOptionsRespVO.OptionItemVO toBrandOption(BrandDTO brand) {
        return option(String.valueOf(brand.getId()), brand.getName());
    }

    private InventoryFilterOptionsRespVO.OptionItemVO toCategoryOption(Category category) {
        return option(String.valueOf(category.getId()), category.getName());
    }

    private InventoryFilterOptionsRespVO.OptionItemVO option(String value, String label) {
        return InventoryFilterOptionsRespVO.OptionItemVO.builder()
                .value(value)
                .label(label)
                .build();
    }

}
