package com.mallease.product.controller;

import com.mallease.common.api.R;
import com.mallease.common.api.ResultCode;
import com.mallease.product.converter.SkuStockConverter;
import com.mallease.product.model.client.cmd.SkuStockCmd;
import com.mallease.product.model.client.vo.SkuStockVO;
import com.mallease.product.model.data.entity.Sku;
import com.mallease.product.model.data.entity.SkuStock;
import com.mallease.product.model.data.entity.Spu;
import com.mallease.product.service.SkuService;
import com.mallease.product.service.SkuStockService;
import com.mallease.product.service.SpuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * SKU库存管理Controller
 *
 * @author: Aulen
 * @create: 2025-11-14
 */
@Tag(name = "SKU库存管理", description = "库存查询、调整、预警")
@Slf4j
@RestController
@RequestMapping("/product/stock")
public class SkuStockController {

    @Autowired
    private SkuStockService skuStockService;
    @Autowired
    private SkuStockConverter skuStockConverter;
    @Autowired
    private SpuService spuService;
    @Autowired
    private SkuService skuService;

    @Operation(summary = "创建库存记录", description = "为指定SKU创建库存记录")
    @PostMapping("/create")
    public R<Long> create(@Validated(SkuStockCmd.Create.class) @RequestBody SkuStockCmd cmd) {
        SkuStock stock = skuStockConverter.saveCmdToEntity(cmd);
        Long id = skuStockService.create(stock);
        return R.success(id);
    }

    @Operation(summary = "更新库存信息", description = "更新库存基础信息（预警值、状态等）")
    @PutMapping("/update")
    public R<Integer> update(@Validated(SkuStockCmd.Update.class) @RequestBody SkuStockCmd cmd) {
        SkuStock stock = skuStockConverter.saveCmdToEntity(cmd);
        int count = skuStockService.update(stock);
        return R.success(count);
    }

    @Operation(summary = "根据SKU获取库存")
    @GetMapping("/sku/{skuId}")
    public R<SkuStockVO> getBySkuId(@Parameter(description = "SKU ID") @PathVariable Long skuId) {
        SkuStock stock = skuStockService.getBySkuId(skuId);
        if (stock == null) {
            return R.failed("库存记录不存在");
        }
        SkuStockVO vo = skuStockConverter.entityToVo(stock);
        return R.success(vo);
    }

    @Operation(summary = "根据SPU获取库存列表", description = "查询某个SPU下所有SKU的库存，包含商品名称和规格信息")
    @GetMapping("/spu/{spuId}")
    public R<List<SkuStockVO>> listBySpuId(@Parameter(description = "SPU ID") @PathVariable Long spuId) {

        List<SkuStock> stockList = skuStockService.listStockBySpuIds(List.of(spuId));
        if (stockList.isEmpty()) {
            return R.success(List.of());
        }

        List<Spu> spuList = spuService.listByIds(List.of(spuId));
        String spuName = spuList.isEmpty() ? null : spuList.get(0).getName();

        List<Sku> skuList = skuService.listBySpuId(spuId);
        Map<Long, String> skuAttrMap = skuList.stream()
                .collect(Collectors.toMap(Sku::getId, Sku::getAttrValues, (a, b) -> a));

        List<SkuStockVO> voList = skuStockConverter.entityListToVoList(stockList);
        for (SkuStockVO vo : voList) {
            vo.setSpuName(spuName);
            String attrValuesJson = skuAttrMap.get(vo.getSkuId());
            vo.setAttrValues(attrValuesJson);
            vo.setAttrValuesObj(skuStockConverter.parseAttrValues(attrValuesJson));
        }

        return R.success(voList);
    }

    @Operation(summary = "调整库存", description = "手动入库/出库操作")
    @PutMapping("/adjust")
    public R<Integer> adjustStock(
            @Parameter(description = "SKU ID") @RequestParam Long skuId,
            @Parameter(description = "调整数量（正数入库，负数出库）") @RequestParam Integer quantity) {
        int count = skuStockService.adjustStock(skuId, quantity);
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "库存预警列表", description = "查询库存低于预警值的SKU")
    @GetMapping("/warning")
    public R<List<SkuStockVO>> listLowStockWarning() {
        List<SkuStock> stockList = skuStockService.listLowStockWarning();
        List<SkuStockVO> voList = skuStockConverter.entityListToVoList(stockList);
        return R.success(voList);
    }

    @Operation(summary = "批量更新库存状态")
    @PutMapping("/status")
    public R<Integer> updateStockStatusBatch(
            @Parameter(description = "SKU ID列表") @RequestParam List<Long> skuIds,
            @Parameter(description = "库存状态: 0-无货, 1-有货, 2-预售") @RequestParam Integer stockStatus) {
        int count = skuStockService.updateStockStatusBatch(skuIds, stockStatus);
        return R.success(count);
    }
}