package com.mallease.product.controller.admin.stock;

import com.mallease.common.api.R;
import com.mallease.common.api.ResultCode;
import com.mallease.common.dto.remote.SkuAvailabilityDTO;
import com.mallease.common.dto.remote.SkuStockQueryDTO;
import com.mallease.common.dto.remote.StockLockDTO;
import com.mallease.product.controller.admin.stock.vo.SkuStockRespVO;
import com.mallease.product.controller.admin.stock.vo.SkuStockSaveReqVO;
import com.mallease.product.convert.stock.SkuStockConvert;
import com.mallease.product.dal.entity.Sku;
import com.mallease.product.dal.entity.SkuStock;
import com.mallease.product.dal.entity.Spu;
import com.mallease.product.service.sku.SkuService;
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
@RequestMapping("/admin/stock")
public class SkuStockAdminController {

    private final SkuStockService skuStockService;
    private final SkuStockConvert skuStockConvert;
    private final SpuService spuService;
    private final SkuService skuService;

    @Operation(summary = "创建库存记录", description = "为指定 SKU 创建库存记录")
    @PostMapping("/create")
    public R<Long> create(@Validated(SkuStockSaveReqVO.Create.class) @RequestBody SkuStockSaveReqVO reqVO) {
        return R.success(skuStockService.create(reqVO));
    }

    @Operation(summary = "更新库存信息", description = "更新库存基础信息（预警值、状态等）")
    @PutMapping("/update")
    public R<Integer> update(@Validated(SkuStockSaveReqVO.Update.class) @RequestBody SkuStockSaveReqVO reqVO) {
        return R.success(skuStockService.update(reqVO));
    }

    @Operation(summary = "根据 SKU 获取库存")
    @GetMapping("/sku/{skuId}")
    public R<SkuStockRespVO> getBySkuId(@Parameter(description = "SKU ID") @PathVariable Long skuId) {
        SkuStock stock = skuStockService.getBySkuId(skuId);
        if (stock == null) {
            return R.failed("库存记录不存在");
        }
        return R.success(skuStockConvert.entityToRespVO(stock));
    }

    @Operation(summary = "根据 SPU 获取库存列表", description = "查询某个 SPU 下所有 SKU 的库存，包含商品名称和规格信息")
    @GetMapping("/spu/{spuId}")
    public R<List<SkuStockRespVO>> listBySpuId(@Parameter(description = "SPU ID") @PathVariable Long spuId) {
        List<SkuStock> stockList = skuStockService.listStockBySpuIds(List.of(spuId));
        if (stockList.isEmpty()) {
            return R.success(List.of());
        }

        List<Spu> spuList = spuService.listByIds(List.of(spuId));
        String spuName = spuList.isEmpty() ? null : spuList.get(0).getName();

        List<Sku> skuList = skuService.listBySpuId(spuId);
        Map<Long, String> skuAttrMap = skuList.stream()
                .collect(Collectors.toMap(Sku::getId, Sku::getAttrValues, (left, right) -> left));

        List<SkuStockRespVO> respVOList = skuStockConvert.entityListToRespVOList(stockList);
        for (SkuStockRespVO respVO : respVOList) {
            respVO.setSpuName(spuName);
            String attrValuesJson = skuAttrMap.get(respVO.getSkuId());
            respVO.setAttrValues(attrValuesJson);
            respVO.setAttrValuesObj(skuStockConvert.parseAttrValues(attrValuesJson));
        }
        return R.success(respVOList);
    }

    @Operation(summary = "调整库存", description = "手动入库/出库操作")
    @PutMapping("/adjust")
    public R<Integer> adjustStock(
            @Parameter(description = "SKU ID") @RequestParam Long skuId,
            @Parameter(description = "调整数量（正数入库，负数出库）") @RequestParam Integer quantity) {
        int count = skuStockService.adjustStock(skuId, quantity);
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "库存预警列表", description = "查询库存低于预警值的 SKU")
    @GetMapping("/warning")
    public R<List<SkuStockRespVO>> listLowStockWarning() {
        List<SkuStock> stockList = skuStockService.listLowStockWarning();
        return R.success(skuStockConvert.entityListToRespVOList(stockList));
    }

    @Operation(summary = "批量更新库存状态")
    @PutMapping("/status")
    public R<Integer> updateStockStatusBatch(
            @Parameter(description = "SKU ID 列表") @RequestParam List<Long> skuIds,
            @Parameter(description = "库存状态: 0-无货, 1-有货, 2-预售") @RequestParam Integer stockStatus) {
        return R.success(skuStockService.updateStockStatusBatch(skuIds, stockStatus));
    }

    @Operation(summary = "锁定库存", description = "内部调用，下单时锁定库存")
    @PutMapping("/internal/lock")
    public R<Boolean> lockStock(@RequestBody StockLockDTO stockLockDTO) {
        Map<Long, Integer> skuStocks = flattenSkuStocks(stockLockDTO.getSpuSkuQuantityMap());
        skuStockService.lockStock(
                stockLockDTO.getOrderNo(),
                skuStocks,
                stockLockDTO.getExpireTime()
        );
        return R.success(null);
    }

    @Operation(summary = "释放库存", description = "内部调用，取消订单释放库存")
    @PutMapping("/internal/unlock")
    public R<List<String>> releaseStock(@RequestBody List<String> orderNos) {
        return R.success(skuStockService.unlockStock(orderNos));
    }

    @Operation(summary = "确认扣减库存", description = "内部调用，支付成功后确认扣减库存")
    @PutMapping("/internal/confirm")
    public R<Void> confirmStock(@RequestParam String orderNo) {
        skuStockService.confirmStock(orderNo);
        return R.success();
    }

    @Operation(summary = "批量查询 SKU 是否有货", description = "内部调用，购物车/下单页获取实时库存状态")
    @PostMapping("/internal/availability")
    public R<List<SkuAvailabilityDTO>> listAvailability(@RequestBody List<SkuStockQueryDTO> queries) {
        return R.success(skuStockService.listAvailabilityBySkuIds(queries));
    }

    private Map<Long, Integer> flattenSkuStocks(Map<Long, Map<Long, Integer>> spuSkuQuantityMap) {
        if (spuSkuQuantityMap == null || spuSkuQuantityMap.isEmpty()) {
            return Map.of();
        }

        Map<Long, Integer> skuStocks = new java.util.LinkedHashMap<>();
        for (Map<Long, Integer> skuQuantityMap : spuSkuQuantityMap.values()) {
            if (skuQuantityMap == null || skuQuantityMap.isEmpty()) {
                continue;
            }
            for (Map.Entry<Long, Integer> entry : skuQuantityMap.entrySet()) {
                if (entry.getKey() == null || entry.getValue() == null) {
                    continue;
                }
                skuStocks.merge(entry.getKey(), entry.getValue(), Integer::sum);
            }
        }
        return skuStocks;
    }
}
