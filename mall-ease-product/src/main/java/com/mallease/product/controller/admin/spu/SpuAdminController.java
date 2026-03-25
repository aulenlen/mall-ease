package com.mallease.product.controller.admin.spu;

import com.github.pagehelper.PageHelper;
import com.mallease.common.api.Page;
import com.mallease.common.api.PageUtils;
import com.mallease.common.api.R;
import com.mallease.product.controller.admin.sku.vo.SkuRespVO;
import com.mallease.product.controller.admin.spu.vo.SpuDetailRespVO;
import com.mallease.product.controller.admin.spu.vo.SpuPageReqVO;
import com.mallease.product.controller.admin.spu.vo.SpuPageRespVO;
import com.mallease.product.controller.admin.spu.vo.SpuSaveReqVO;
import com.mallease.product.controller.admin.spu.vo.SpuStatsRespVO;
import com.mallease.product.convert.sku.SkuConvert;
import com.mallease.product.convert.spu.SpuConvert;
import com.mallease.product.dal.entity.Sku;
import com.mallease.product.dal.entity.SkuStock;
import com.mallease.product.dal.entity.Spu;
import com.mallease.product.service.sku.SkuService;
import com.mallease.product.service.stock.SkuStockService;
import com.mallease.product.service.spu.SpuPublishService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "后台商品管理", description = "后台商品 CRUD")
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/spu")
public class SpuAdminController {

    private final com.mallease.product.service.spu.SpuService spuService;
    private final SpuPublishService spuPublishService;
    private final SpuConvert spuConvert;
    private final SkuService skuService;
    private final SkuConvert skuConvert;
    private final SkuStockService skuStockService;

    @Operation(summary = "创建商品")
    @PostMapping
    public R<Long> create(@Valid @RequestBody SpuSaveReqVO reqVO) {
        return R.success(spuService.create(reqVO));
    }

    @Operation(summary = "更新商品")
    @PutMapping("/{id}")
    public R<Integer> update(@PathVariable Long id, @Valid @RequestBody SpuSaveReqVO reqVO) {
        return R.success(spuService.update(id, reqVO));
    }

    @Operation(summary = "分页查询商品")
    @GetMapping
    public R<Page<SpuPageRespVO>> page(@Validated @ModelAttribute SpuPageReqVO reqVO) {
        PageHelper.startPage(reqVO.getPageNum(), reqVO.getPageSize());
        List<Spu> spuList = spuService.page(reqVO);
        return R.success(PageUtils.convertPage(spuList, spuConvert::toPageRespList));
    }

    @Operation(summary = "获取商品详情")
    @GetMapping("/{id}")
    public R<SpuDetailRespVO> getDetail(@PathVariable Long id) {
        return R.success(spuService.getDetail(id));
    }

    @Operation(summary = "获取商品统计", description = "统计当前筛选条件下全部、上架、下架、未审核数量，忽略 publishStatus 和 verifyStatus 条件")
    @GetMapping("/stats")
    public R<SpuStatsRespVO> stats(@Validated @ModelAttribute SpuPageReqVO reqVO) {
        return R.success(spuService.stats(reqVO));
    }

    @Operation(summary = "删除商品")
    @DeleteMapping("/{id}")
    public R<Integer> delete(@PathVariable Long id) {
        return R.success(spuService.delete(id));
    }

    @Operation(summary = "批量删除商品", description = "批量逻辑删除商品及其关联数据")
    @PostMapping("/delete/batch")
    public R<Integer> deleteBatch(@RequestBody List<Long> ids) {
        return R.success(spuService.deleteBatch(ids));
    }

    @Operation(summary = "批量上架商品")
    @PutMapping("/publish")
    public R<Integer> publish(@RequestBody List<Long> spuIds) {
        return R.success(spuPublishService.publish(spuIds));
    }

    @Operation(summary = "批量下架商品")
    @PutMapping("/unpublish")
    public R<Integer> unpublish(@RequestBody List<Long> spuIds) {
        return R.success(spuPublishService.unpublish(spuIds));
    }

    @Operation(summary = "搜索SPU列表（含SKU）", description = "用于秒杀/优惠券商品选择")
    @GetMapping("/listWithSku")
    public R<Page<SpuPageRespVO>> listWithSku(@Validated @ModelAttribute SpuPageReqVO reqVO) {
        PageHelper.startPage(reqVO.getPageNum(), reqVO.getPageSize());
        List<Spu> spuList = spuService.page(reqVO);
        List<SpuPageRespVO> respVOList = spuConvert.toPageRespList(spuList);
        if (respVOList.isEmpty()) {
            return R.success(PageUtils.buildPage(spuList, respVOList));
        }

        List<Long> spuIds = respVOList.stream()
                .map(SpuPageRespVO::getId)
                .toList();

        List<Sku> skuList = skuService.selectBySpuIds(spuIds);
        List<SkuRespVO> skuRespList = skuConvert.entityListToRespVOList(skuList);
        Map<Long, SkuRespVO> skuRespMap = skuRespList.stream()
                .collect(Collectors.toMap(SkuRespVO::getId, item -> item, (left, right) -> left));

        List<SkuStock> stockList = skuStockService.listStockBySpuIds(spuIds);
        for (SkuStock stock : stockList) {
            SkuRespVO skuRespVO = skuRespMap.get(stock.getSkuId());
            if (skuRespVO != null) {
                skuConvert.mergeSkuStockToRespVO(skuRespVO, stock);
            }
        }

        Map<Long, List<SkuRespVO>> spuSkuMap = skuRespList.stream()
                .collect(Collectors.groupingBy(SkuRespVO::getSpuId));
        respVOList.forEach(item -> item.setSkuVOList(spuSkuMap.get(item.getId())));

        return R.success(PageUtils.buildPage(spuList, respVOList));
    }
}
