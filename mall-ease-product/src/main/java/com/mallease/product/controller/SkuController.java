package com.mallease.product.controller;

import com.github.pagehelper.PageHelper;
import com.mallease.common.api.Page;
import com.mallease.common.api.PageUtils;
import com.mallease.common.api.R;
import com.mallease.common.api.ResultCode;
import com.mallease.product.converter.SkuConverter;
import com.mallease.product.model.aggregate.SpuAggregate;
import com.mallease.product.model.client.cmd.SkuCmd;
import com.mallease.product.model.client.query.SkuQuery;
import com.mallease.product.model.client.vo.SkuVO;
import com.mallease.product.model.data.entity.Sku;
import com.mallease.product.service.SkuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * SKU管理
 *
 * @author: Aulen
 * @create: 2026-01-05
 */
@Tag(name = "商品SKU管理", description = "SKU增删改查")
@Slf4j
@RestController
@RequestMapping("/product/sku")
public class SkuController {

    @Autowired
    private SkuService skuService;
    @Autowired
    private SkuConverter skuConverter;

    @Operation(summary = "创建SKU", description = "在指定SPU下创建单个SKU（含库存）")
    @PostMapping("/spu/{spuId}")
    public R<Long> create(
            @Parameter(description = "SPU ID") @PathVariable Long spuId,
            @Validated(SkuCmd.Create.class) @RequestBody SkuCmd cmd) {
        SpuAggregate.SkuData skuData = skuConverter.saveCmdToSkuData(cmd);
        Long skuId = skuService.create(spuId, skuData);
        return R.success(skuId);
    }

    @Operation(summary = "更新SKU", description = "更新SKU基础信息（不含库存、促销）")
    @PutMapping("/update")
    public R<Integer> update(@Validated(SkuCmd.Update.class) @RequestBody SkuCmd cmd) {
        Sku sku = skuConverter.saveCmdToEntity(cmd);
        int count = skuService.update(sku);
        return R.success(count);
    }

    @Operation(summary = "SKU列表", description = "支持分页、多条件查询")
    @GetMapping("/list")
    public R<Page<SkuVO>> list(@Validated @ModelAttribute SkuQuery query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<Sku> skuList = skuService.list(query);
        List<SkuVO> voList = skuConverter.entityListToVoList(skuList);
        Page<SkuVO> result = PageUtils.buildPage(skuList, voList);
        return R.success(result);
    }

    @Operation(summary = "获取SKU详情")
    @GetMapping("/{id}")
    public R<SkuVO> getById(@Parameter(description = "SKU ID") @PathVariable Long id) {
        Sku sku = skuService.getById(id);
        if (sku == null) {
            return R.failed("SKU不存在");
        }
        SkuVO vo = skuConverter.entityToVo(sku);
        return R.success(vo);
    }

    @Operation(summary = "删除SKU", description = "级联删除库存、促销、价格策略")
    @DeleteMapping("/{id}")
    public R<Integer> delete(@Parameter(description = "SKU ID") @PathVariable Long id) {
        int count = skuService.delete(id);
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "批量更新启用状态")
    @PutMapping("/enable-status")
    public R<Integer> updateEnableStatus(
            @Parameter(description = "SKU ID列表") @RequestParam List<Long> ids,
            @Parameter(description = "状态值: 0-禁用, 1-启用") @RequestParam Integer status) {
        int count = skuService.updateEnableStatus(ids, status);
        return R.success(count);
    }

    @Operation(summary = "根据SPU获取SKU列表", description = "查询某个SPU下的所有SKU")
    @GetMapping("/spu/{spuId}")
    public R<List<SkuVO>> listBySpuId(@Parameter(description = "SPU ID") @PathVariable Long spuId) {
        List<Sku> skuList = skuService.listBySpuId(spuId);
        List<SkuVO> voList = skuConverter.entityListToVoList(skuList);
        return R.success(voList);
    }
}