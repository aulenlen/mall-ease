package com.mallease.pms.controller;

import com.github.pagehelper.PageHelper;
import com.mallease.common.api.Page;
import com.mallease.common.api.R;
import com.mallease.common.api.ResultCode;
import com.mallease.pms.dto.request.PmsProductRequest;
import com.mallease.pms.pojo.PmsProduct;
import com.mallease.pms.service.PmsProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品管理Controller
 *
 * @author: Aulen
 * @description: 商品相关接口
 * @create: 2025-11-13 18:01
 */
@RestController
@RequestMapping("/pms/product")
public class PmsProductController {
    @Autowired
    private PmsProductService productService;

    /**
     * 查询商品列表（支持多条件查询和分页）
     *
     * @param request 查询请求参数
     * @return 分页结果
     */
    @GetMapping("/list")
    public R<Page<PmsProduct>> list(@Validated @ModelAttribute PmsProductRequest request) {
        PageHelper.startPage(request.getPageNum(), request.getPageSize());
        List<PmsProduct> productList = productService.list(request);
        return R.success(Page.restPage(productList));
    }

    /**
     * 批量更新商品上架状态
     *
     * @param ids 商品ID列表（数组格式）
     * @param publishStatus 上架状态：0->下架；1->上架
     * @return 更新结果
     */
    @PostMapping("/update/publishStatus")
    public R<Integer> updatePublishStatus(@RequestParam(value = "ids") List<Long> ids,
                                          @RequestParam(value = "publishStatus") Integer publishStatus) {
        int count = productService.updatePublishStatusBatch(ids, publishStatus);
        if (count > 0) {
            return R.success(count);
        }
        return R.failed(ResultCode.FAILED);
    }
}

