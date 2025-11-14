package com.mallease.pms.controller;

import com.mallease.common.api.R;
import com.mallease.pms.pojo.PmsSkuStock;
import com.mallease.pms.service.PmsSkuStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * SKU库存管理Controller
 *
 * @author: Aulen
 * @description: SKU库存相关接口
 * @create: 2025-11-14 02:15
 */
@RestController
@RequestMapping("/pms/sku")
public class PmsSkuStockController {
    @Autowired
    private PmsSkuStockService skuStockService;

    /**
     * 根据商品编号及关键字模糊搜索SKU库存
     *
     * @param pid 商品ID（路径参数）
     * @param keyword 关键字（可选，用于模糊匹配sku_code）
     * @return SKU库存列表
     */
    @GetMapping("/{pid}")
    public R<List<PmsSkuStock>> getSkuByProductId(@PathVariable("pid") Long pid,
                                                    @RequestParam(value = "keyword", required = false) String keyword) {
        List<PmsSkuStock> skuStockList = skuStockService.getByProductIdAndKeyword(pid, keyword);
        return R.success(skuStockList);
    }

    /**
     * 批量更新库存信息
     *
     * @param pid 商品ID（路径参数）
     * @param skuStockList SKU库存列表
     * @return 更新的记录数
     */
    @PostMapping("/update/{pid}")
    public R<Integer> updateBatch(@PathVariable("pid") Long pid,
                                   @RequestBody List<PmsSkuStock> skuStockList) {
        int count = skuStockService.updateBatch(pid, skuStockList);
        return R.success(count);
    }
}

