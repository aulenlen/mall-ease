package com.mallease.marketing.controller.admin.flash;

import com.github.pagehelper.PageHelper;
import com.mallease.common.api.Page;
import com.mallease.common.api.PageUtils;
import com.mallease.common.api.R;
import com.mallease.marketing.convert.FlashConvert;
import com.mallease.marketing.controller.admin.flash.vo.FlashProductReqVO;
import com.mallease.marketing.controller.admin.flash.vo.FlashSessionReqVO;
import com.mallease.marketing.controller.admin.flash.vo.FlashProductPageReqVO;
import com.mallease.marketing.controller.admin.flash.vo.FlashSessionPageReqVO;
import com.mallease.marketing.controller.admin.flash.vo.FlashProductRespVO;
import com.mallease.marketing.controller.admin.flash.vo.FlashSessionRespVO;
import com.mallease.marketing.dal.entity.FlashProduct;
import com.mallease.marketing.dal.entity.FlashSession;
import com.mallease.marketing.service.flash.FlashService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "秒杀后台管理")
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/flash")
public class FlashAdminController {

    private final FlashService flashService;
    private final FlashConvert flashConvert;

    @Operation(summary = "创建场次")
    @PostMapping("/sessions")
    public R<Long> createSession(@Validated(FlashSessionReqVO.Create.class) @RequestBody FlashSessionReqVO reqVO) {
        FlashSession session = flashConvert.toFlashSession(reqVO);
        flashService.createSession(session);
        return R.success(session.getId());
    }

    @Operation(summary = "更新场次")
    @PutMapping("/sessions")
    public R<Integer> updateSession(@Validated(FlashSessionReqVO.Update.class) @RequestBody FlashSessionReqVO reqVO) {
        FlashSession session = flashConvert.toFlashSession(reqVO);
        return R.success(flashService.updateSession(session));
    }

    @Operation(summary = "获取场次详情")
    @GetMapping("/sessions/{id:\\d+}")
    public R<FlashSessionRespVO> getSession(@Parameter(description = "秒杀场次ID", required = true) @PathVariable Long id) {
        return R.success(flashConvert.toFlashSessionResp(flashService.getSessionById(id)));
    }

    @Operation(summary = "删除场次")
    @DeleteMapping("/sessions/{id:\\d+}")
    public R<Integer> deleteSession(@Parameter(description = "秒杀场次ID", required = true) @PathVariable Long id) {
        return R.success(flashService.deleteSession(id));
    }

    @Operation(summary = "分页查询场次列表")
    @GetMapping("/sessions")
    public R<Page<FlashSessionRespVO>> listSessions(@ParameterObject FlashSessionPageReqVO reqVO) {
        PageHelper.startPage(reqVO.getPageNum(), reqVO.getPageSize());
        List<FlashSession> sessionList = flashService.pageSessions(reqVO);
        return R.success(PageUtils.convertPage(sessionList, flashConvert::toFlashSessionRespList));
    }

    @Operation(summary = "批量修改场次状态")
    @PutMapping("/sessions/status")
    public R<Integer> updateSessionStatusBatch(
            @RequestBody List<Long> ids,
            @Parameter(description = "目标场次状态：0-禁用，1-启用", required = true)
            @RequestParam Integer sessionStatus) {
        return R.success(flashService.updateSessionStatusBatch(ids, sessionStatus));
    }

    @Operation(summary = "添加秒杀商品")
    @PostMapping("/products")
    public R<Long> createProduct(
            @Validated(FlashProductReqVO.Create.class) @RequestBody FlashProductReqVO reqVO) {
        FlashProduct product = flashConvert.toFlashProduct(reqVO);
        flashService.createProduct(product);
        return R.success(product.getId());
    }

    @Operation(summary = "批量添加秒杀商品")
    @PostMapping("/products/batch")
    public R<Integer> createProductBatch(
            @RequestBody List<FlashProductReqVO> reqVOList) {
        List<FlashProduct> productList = reqVOList.stream()
                .map(flashConvert::toFlashProduct)
                .toList();
        return R.success(flashService.createProductBatch(productList));
    }

    @Operation(summary = "更新秒杀商品")
    @PutMapping("/products")
    public R<Integer> updateProduct(
            @Validated(FlashProductReqVO.Update.class) @RequestBody FlashProductReqVO reqVO) {
        FlashProduct product = flashConvert.toFlashProduct(reqVO);
        return R.success(flashService.updateProduct(product));
    }

    @Operation(summary = "获取秒杀商品详情")
    @GetMapping("/products/{id:\\d+}")
    public R<FlashProductRespVO> getProduct(@Parameter(description = "秒杀商品ID", required = true) @PathVariable Long id) {
        return R.success(getEnrichedProduct(id));
    }

    @Operation(summary = "删除秒杀商品")
    @DeleteMapping("/products/{id:\\d+}")
    public R<Integer> deleteProduct(@Parameter(description = "秒杀商品ID", required = true) @PathVariable Long id) {
        return R.success(flashService.deleteProduct(id));
    }

    @Operation(summary = "批量删除秒杀商品")
    @DeleteMapping("/products/batch")
    public R<Integer> deleteProductBatch(
            @RequestBody List<Long> ids) {
        return R.success(flashService.deleteProductBatch(ids));
    }

    @Operation(summary = "分页查询秒杀商品列表")
    @GetMapping("/products")
    public R<Page<FlashProductRespVO>> listProducts(@ParameterObject FlashProductPageReqVO reqVO) {
        PageHelper.startPage(reqVO.getPageNum(), reqVO.getPageSize());
        List<FlashProduct> productList = flashService.pageProducts(reqVO);
        return R.success(PageUtils.convertPage(productList, flashService::enrichWithSkuInfo));
    }

    private FlashProductRespVO getEnrichedProduct(Long id) {
        FlashProduct product = flashService.getProductById(id);
        List<FlashProductRespVO> productRespVOList = flashService.enrichWithSkuInfo(List.of(product));
        return productRespVOList.isEmpty() ? flashConvert.toFlashProductResp(product) : productRespVOList.get(0);
    }
}
