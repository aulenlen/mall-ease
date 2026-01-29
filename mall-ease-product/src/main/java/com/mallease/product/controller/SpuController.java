package com.mallease.product.controller;

import com.github.pagehelper.PageHelper;
import com.mallease.common.api.Page;
import com.mallease.common.api.PageUtils;
import com.mallease.common.api.R;
import com.mallease.common.api.ResultCode;
import com.mallease.common.dto.remote.SpuSearchQuery;
import com.mallease.common.dto.remote.SpuSearchResultDTO;
import com.mallease.product.assembler.SpuDetailAssembler;
import com.mallease.product.assembler.SpuSaveAssembler;
import com.mallease.product.converter.SkuConverter;
import com.mallease.product.converter.SpuCacheConverter;
import com.mallease.product.converter.SpuConverter;
import com.mallease.product.model.aggregate.SpuAggregate;
import com.mallease.product.model.client.cmd.PublishSpuCmd;
import com.mallease.product.model.client.cmd.SpuCmd;
import com.mallease.product.model.client.query.SpuQuery;
import com.mallease.product.model.client.vo.*;
import com.mallease.product.model.data.cache.SpuCache;
import com.mallease.product.model.data.entity.Sku;
import com.mallease.product.model.data.entity.SkuStock;
import com.mallease.product.model.data.entity.Spu;
import com.mallease.product.service.SkuService;
import com.mallease.product.service.SkuStockService;
import com.mallease.product.service.SpuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "商品SPU管理", description = "SPU增删改查")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/product/spu")
public class SpuController {

    private final SpuService spuService;
    private final SpuConverter spuConverter;
    private final SpuSaveAssembler spuSaveAssembler;
    private final SpuDetailAssembler spuDetailAssembler;
    private final SkuService skuService;
    private final SkuConverter skuConverter;
    private final SkuStockService skuStockService;
    private final SpuCacheConverter spuCacheConverter;

    @Operation(summary = "创建商品")
    @PostMapping("/create")
    public R<Long> create(@Validated(SpuCmd.Create.class) @RequestBody SpuCmd cmd) {
        SpuAggregate aggregate = spuSaveAssembler.assembleForCreate(cmd);
        Long spuId = spuService.create(aggregate);
        return R.success(spuId);
    }

    @Operation(summary = "更新商品")
    @PutMapping("/update")
    public R<Integer> update(@Validated(SpuCmd.Update.class) @RequestBody SpuCmd cmd) {
        SpuAggregate aggregate = spuSaveAssembler.assembleForUpdate(cmd);
        int count = spuService.update(aggregate);
        return R.success(count);
    }

    @Operation(summary = "搜索商品", description = "支持分页、模糊搜索")
    @GetMapping("/list")
    public R<Page<SpuVO>> list(@Validated @ModelAttribute SpuQuery query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<Spu> spuList = spuService.list(query);
        List<SpuVO> spuVOList = spuConverter.entityListToVoList(spuList);
        Page<SpuVO> result = PageUtils.buildPage(spuList, spuVOList);
        return R.success(result);
    }

    @Operation(summary = "获取商品更新信息", description = "用于编辑页面数据回显，返回SPU完整信息")
    @GetMapping("/{id}")
    public R<SpuDetailVO> getUpdateInfo(@Parameter(description = "SPU ID") @PathVariable Long id) {
        SpuAggregate aggregate = spuService.getUpdateInfo(id);
        SpuDetailVO vo = spuDetailAssembler.assemble(aggregate);
        return R.success(vo);
    }

    @Operation(summary = "删除商品", description = "级联删除SKU、详情、属性值、满减规则及CMS关联")
    @DeleteMapping("/{id}")
    public R<Integer> delete(@Parameter(description = "SPU ID") @PathVariable Long id) {
        int count = spuService.delete(id);
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "SPU上下架，支持批量操作")
    @PutMapping("/publish")
    public R<SpuPublishVO> publish(@Validated @RequestBody PublishSpuCmd cmd) {
        SpuPublishVO result = spuService.publish(cmd.getSpuIds(), cmd.getPublishStatus());
        return R.success(result);
    }

    @Operation(summary = "搜索SPU列表（含SKU）", description = "用于秒杀/优惠券商品选择")
    @GetMapping("/listWithSku")
    public R<Page<SpuVO>> listWithSku(@Validated @ModelAttribute SpuQuery query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<Spu> spuList = spuService.list(query);
        List<SpuVO> spuVOS = spuConverter.entityListToVoList(spuList);

        List<Long> spuIds = spuVOS.stream().map(SpuVO::getId).toList();

        List<Sku> skuList = skuService.selectBySpuIds(spuIds);
        List<SkuVO> skuVOList = skuConverter.entityListToVoList(skuList);
        Map<Long, SkuVO> skuVOMap = skuVOList.stream()
                .collect(Collectors.toMap(SkuVO::getId, v -> v, (a, b) -> a));

        List<SkuStock> stockList = skuStockService.listStockBySpuIds(spuIds);
        for (SkuStock stock : stockList) {
            SkuVO skuVO = skuVOMap.get(stock.getSkuId());
            if (skuVO != null) {
                skuConverter.mergeSkuStockToVo(skuVO, stock);
            }
        }

        Map<Long, List<SkuVO>> listMap = skuVOList.stream().collect(Collectors.groupingBy(SkuVO::getSpuId));
        spuVOS.forEach(spuVo -> spuVo.setSkuVOList(listMap.get(spuVo.getId())));

        Page<SpuVO> result = PageUtils.buildPage(spuList, spuVOS);
        return R.success(result);
    }

    @Operation(summary = "前台商品详情", description = "获取完整商品信息（前台展示）")
    @GetMapping("/portal/{spuId}")
    public R<ProductVO> portalDetail(@PathVariable Long spuId) {
        SpuCache cache = spuService.getProduct(spuId);
        if (cache == null) {
            return R.success(null);
        }
        return R.success(spuCacheConverter.cacheToVO(cache));
    }

    // 内部调用
    @Operation(summary = "MySQL搜索商品", description = "内部调用")
    @PostMapping("/internal/advancedSearch")
    public R<SpuSearchResultDTO> advancedSearch(@RequestBody SpuSearchQuery query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        SpuSearchResultDTO result = spuService.advancedSearch(query);
        return R.success(result);
    }
}
