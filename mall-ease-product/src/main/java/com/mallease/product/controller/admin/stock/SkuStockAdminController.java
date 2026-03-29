package com.mallease.product.controller.admin.stock;

import com.github.pagehelper.PageHelper;
import com.mallease.common.api.Page;
import com.mallease.common.api.PageUtils;
import com.mallease.common.api.R;
import com.mallease.common.api.ResultCode;
import com.mallease.common.dto.remote.BrandDTO;
import com.mallease.product.controller.admin.stock.vo.InventoryFilterOptionsRespVO;
import com.mallease.product.controller.admin.stock.vo.InventorySpuRecordRespVO;
import com.mallease.product.controller.admin.stock.vo.InventoryStatsRespVO;
import com.mallease.product.controller.admin.stock.vo.SkuStockLogPageReqVO;
import com.mallease.product.controller.admin.stock.vo.SkuStockLogRespVO;
import com.mallease.product.controller.admin.stock.vo.SkuStockPageReqVO;
import com.mallease.product.controller.admin.stock.vo.SkuStockRespVO;
import com.mallease.product.controller.admin.stock.vo.SkuStockSaveReqVO;
import com.mallease.product.convert.stock.SkuStockConvert;
import com.mallease.product.dal.entity.Category;
import com.mallease.product.dal.entity.Sku;
import com.mallease.product.dal.entity.SkuStock;
import com.mallease.product.dal.entity.Spu;
import com.mallease.product.service.brand.BrandService;
import com.mallease.product.service.category.CategoryService;
import com.mallease.product.service.sku.SkuService;
import com.mallease.product.service.sku.support.SkuSpecResolver;
import com.mallease.product.service.stock.SkuStockService;
import com.mallease.product.service.spu.SpuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private final SpuService spuService;
    private final SkuService skuService;
    private final SkuSpecResolver skuSpecResolver;
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

    @Operation(summary = "分页查询库存列表", description = "独立库存页使用，只返回 SPU 聚合行")
    @GetMapping
    public R<Page<InventorySpuRecordRespVO>> page(@Validated @ModelAttribute SkuStockPageReqVO reqVO) {
        PageHelper.startPage(reqVO.getPageNum(), reqVO.getPageSize());
        List<InventorySpuRecordRespVO> respVOList = skuStockService.page(reqVO);
        return R.success(PageUtils.buildPage(respVOList, respVOList));
    }

    @Operation(summary = "库存页统计", description = "返回 summary 和 tabTotals，忽略页签和库存状态条件")
    @GetMapping("/stats")
    public R<InventoryStatsRespVO> stats(@Validated @ModelAttribute SkuStockPageReqVO reqVO) {
        return R.success(skuStockService.stats(reqVO));
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

    @Operation(summary = "根据 SPU 获取库存列表", description = "查询某个 SPU 下所有 SKU 的库存，包含商品名称、规格信息，以及启用和停用两类 SKU")
    @GetMapping("/by-spu/{spuId}")
    public R<List<SkuStockRespVO>> listBySpuId(@Parameter(description = "SPU ID") @PathVariable Long spuId) {
        List<Spu> spuList = spuService.listByIds(List.of(spuId));
        String spuName = spuList.isEmpty() ? null : spuList.get(0).getName();

        List<Sku> skuList = skuService.listBySpuId(spuId);
        if (skuList.isEmpty()) {
            return R.success(List.of());
        }

        Map<Long, SkuStock> stockMap = skuStockService.listStockBySpuIds(List.of(spuId)).stream()
                .collect(Collectors.toMap(SkuStock::getSkuId, item -> item, (left, right) -> left));
        Map<Long, String> attrValuesMap = skuSpecResolver.buildAttrValueJsonMap(skuList.stream().map(Sku::getId).toList());

        List<SkuStockRespVO> respVOList = skuList.stream()
                .map(sku -> toSkuStockRespVO(spuName, sku, stockMap.get(sku.getId()), attrValuesMap.get(sku.getId())))
                .toList();
        return R.success(respVOList);
    }

    private SkuStockRespVO toSkuStockRespVO(String spuName, Sku sku, SkuStock stock, String attrValuesJson) {
        SkuStockRespVO respVO = stock != null
                ? skuStockConvert.toSkuStockResp(stock)
                : SkuStockRespVO.builder()
                .skuId(sku.getId())
                .spuId(sku.getSpuId())
                .stock(0)
                .lockStock(0)
                .sale(0)
                .lowStock(0)
                .stockStatus(0)
                .lowStockWarning(false)
                .build();
        respVO.setSpuName(spuName);
        respVO.setSkuCode(sku.getSkuCode());
        respVO.setPic(sku.getPic());
        respVO.setAttrValues(attrValuesJson);
        respVO.setAttrValuesObj(skuStockConvert.parseAttrValues(attrValuesJson));
        return respVO;
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
