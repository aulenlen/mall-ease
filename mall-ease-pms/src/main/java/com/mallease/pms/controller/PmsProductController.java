package com.mallease.pms.controller;

import com.github.pagehelper.PageHelper;
import com.mallease.common.api.Page;
import com.mallease.common.api.PageUtils;
import com.mallease.common.api.R;
import com.mallease.common.api.ResultCode;
import com.mallease.pms.converter.PmsProductConverter;
import com.mallease.pms.dto.cmd.CreateProductCmd;
import com.mallease.pms.dto.cmd.UpdateProductCmd;
import com.mallease.pms.dto.query.ProductQuery;
import com.mallease.pms.dto.response.PmsProductPublishResult;
import com.mallease.pms.dto.vo.PmsProductDetailVO;
import com.mallease.pms.dto.vo.PmsProductListVO;
import com.mallease.pms.pojo.PmsProduct;
import com.mallease.pms.service.PmsProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品管理Controller
 *
 * @author: Aulen
 * @create: 2025-11-15
 */
@Tag(name = "商品管理", description = "商品增删改查、状态管理")
@RestController
@RequestMapping("/pms/product")
@Slf4j
public class PmsProductController {

    @Autowired
    private PmsProductService productService;

    @Autowired
    private PmsProductConverter productConverter;

    @Operation(summary = "查询商品列表", description = "支持多条件查询和分页")
    @GetMapping("/list")
    public R<Page<PmsProductListVO>> list(@Validated @ModelAttribute ProductQuery query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<PmsProduct> productList = productService.list(query);

        // 使用Converter转换为VO列表
        List<PmsProductListVO> voList = productConverter.entityListToListVoList(productList);

        // 使用PageUtils保留分页信息
        Page<PmsProductListVO> result = PageUtils.buildPage(productList, voList);
        return R.success(result);
    }

    @Operation(summary = "创建商品", description = "创建商品及其关联信息")
    @PostMapping("/create")
    public R<Integer> create(@Validated @RequestBody CreateProductCmd cmd) {
        int count = productService.createProduct(cmd);
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "批量更新上架状态")
    @PostMapping("/update/publishStatus")
    public R<PmsProductPublishResult> updatePublishStatus(
            @Parameter(description = "商品ID列表") @RequestParam List<Long> ids,
            @Parameter(description = "上架状态(0:下架 1:上架)") @RequestParam Integer publishStatus) {
        PmsProductPublishResult result = productService.updatePublishStatusBatch(ids, publishStatus);
        // 判断是否有失败的商品
        if (result.getFailCount() > 0) {
            log.warn("批量上架存在失败，成功: {}, 失败: {}",
                    result.getSuccessCount(), result.getFailCount());
        }
        return R.success(result);
    }

    @Operation(summary = "批量更新新品状态")
    @PostMapping("/update/newStatus")
    public R<Integer> updateNewStatus(
            @Parameter(description = "商品ID列表") @RequestParam List<Long> ids,
            @Parameter(description = "新品状态(0:不是新品 1:新品)") @RequestParam Integer newStatus) {
        int count = productService.updateNewStatusBatch(ids, newStatus);
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "批量更新推荐状态")
    @PostMapping("/update/recommendStatus")
    public R<Integer> updateRecommendStatus(
            @Parameter(description = "商品ID列表") @RequestParam List<Long> ids,
            @Parameter(description = "推荐状态(0:不推荐 1:推荐)") @RequestParam Integer recommendStatus) {
        int count = productService.updateRecommendStatusBatch(ids, recommendStatus);
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "批量修改审核状态")
    @PostMapping("/update/verifyStatus")
    public R<Integer> updateVerifyStatus(
            @Parameter(description = "商品ID列表") @RequestParam List<Long> ids,
            @Parameter(description = "审核状态(0:未审核 1:审核通过)") @RequestParam Integer verifyStatus,
            @Parameter(description = "审核详情") @RequestParam String detail) {
        int count = productService.updateVerifyStatusBatch(ids, verifyStatus, detail);
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "批量更新删除状态")
    @PostMapping("/update/deleteStatus")
    public R<Integer> updateDeleteStatus(
            @Parameter(description = "商品ID列表") @RequestParam List<Long> ids,
            @Parameter(description = "删除状态(0:未删除 1:已删除)") @RequestParam Integer deleteStatus) {
        int count = productService.updateDeleteStatusBatch(ids, deleteStatus);
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "获取商品详情", description = "获取商品完整信息用于编辑")
    @GetMapping("/updateInfo/{id}")
    public R<PmsProductDetailVO> getUpdateInfo(@Parameter(description = "商品ID") @PathVariable Long id) {
        PmsProductDetailVO result = productService.getUpdateInfo(id);
        return R.success(result);
    }

    @Operation(summary = "更新商品", description = "更新商品及其关联信息")
    @PostMapping("/update/{id}")
    public R<Integer> update(
            @Parameter(description = "商品ID") @PathVariable Long id,
            @Validated @RequestBody UpdateProductCmd cmd) {
        int count = productService.updateProduct(id, cmd);
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }
}

