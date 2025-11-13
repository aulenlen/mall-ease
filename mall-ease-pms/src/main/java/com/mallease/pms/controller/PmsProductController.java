package com.mallease.pms.controller;

import com.github.pagehelper.PageHelper;
import com.mallease.common.api.Page;
import com.mallease.common.api.R;
import com.mallease.common.api.ResultCode;
import com.mallease.pms.dto.request.PmsProductAggregationRequest;
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
     * 创建商品（聚合接口）
     *
     * @param request 商品聚合请求
     * @return 创建结果
     */
    @PostMapping("/create")
    public R<Integer> create(@Validated @RequestBody PmsProductAggregationRequest request) {
        int count = productService.createProduct(request);
        if (count > 0) {
            return R.success(count);
        }
        return R.failed(ResultCode.FAILED);
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

    /**
     * 批量更新商品新品状态
     *
     * @param ids 商品ID列表（数组格式）
     * @param newStatus 新品状态：0->不是新品；1->新品
     * @return 更新结果
     */
    @PostMapping("/update/newStatus")
    public R<Integer> updateNewStatus(@RequestParam(value = "ids") List<Long> ids,
                                      @RequestParam(value = "newStatus") Integer newStatus) {
        int count = productService.updateNewStatusBatch(ids, newStatus);
        if (count > 0) {
            return R.success(count);
        }
        return R.failed(ResultCode.FAILED);
    }

    /**
     * 批量更新商品推荐状态
     *
     * @param ids 商品ID列表（数组格式）
     * @param recommendStatus 推荐状态：0->不推荐；1->推荐
     * @return 更新结果
     */
    @PostMapping("/update/recommendStatus")
    public R<Integer> updateRecommendStatus(@RequestParam(value = "ids") List<Long> ids,
                                            @RequestParam(value = "recommendStatus") Integer recommendStatus) {
        int count = productService.updateRecommendStatusBatch(ids, recommendStatus);
        if (count > 0) {
            return R.success(count);
        }
        return R.failed(ResultCode.FAILED);
    }

    /**
     * 批量修改商品审核状态
     *
     * @param ids 商品ID列表（数组格式）
     * @param verifyStatus 审核状态：0->未审核；1->审核通过
     * @param detail 审核详情
     * @return 更新结果
     */
    @PostMapping("/update/verifyStatus")
    public R<Integer> updateVerifyStatus(@RequestParam(value = "ids") List<Long> ids,
                                         @RequestParam(value = "verifyStatus") Integer verifyStatus,
                                         @RequestParam(value = "detail") String detail) {
        int count = productService.updateVerifyStatusBatch(ids, verifyStatus, detail);
        if (count > 0) {
            return R.success(count);
        }
        return R.failed(ResultCode.FAILED);
    }
}

