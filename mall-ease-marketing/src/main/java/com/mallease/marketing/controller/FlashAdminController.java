package com.mallease.marketing.controller;

import com.github.pagehelper.PageHelper;
import com.mallease.common.api.Page;
import com.mallease.common.api.PageUtils;
import com.mallease.common.api.R;
import com.mallease.marketing.converter.FlashConverter;
import com.mallease.marketing.model.client.cmd.FlashProductCmd;
import com.mallease.marketing.model.client.cmd.FlashSessionCmd;
import com.mallease.marketing.model.client.query.FlashProductQuery;
import com.mallease.marketing.model.client.query.FlashSessionQuery;
import com.mallease.marketing.model.client.vo.FlashProductVO;
import com.mallease.marketing.model.client.vo.FlashSessionVO;
import com.mallease.marketing.model.data.entity.FlashProduct;
import com.mallease.marketing.model.data.entity.FlashSession;
import com.mallease.marketing.service.FlashService;
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
@RequestMapping("/admin/marketing/flash")
public class FlashAdminController {

    private final FlashService flashService;
    private final FlashConverter flashConverter;

    @Operation(summary = "创建场次")
    @PostMapping("/sessions")
    public R<Long> createSession(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "秒杀场次创建参数", required = true)
            @Validated(FlashSessionCmd.Create.class) @RequestBody FlashSessionCmd cmd) {
        FlashSession session = flashConverter.cmdToSession(cmd);
        flashService.createFlashSession(session);
        return R.success(session.getId());
    }

    @Operation(summary = "更新场次")
    @PutMapping("/sessions")
    public R<Integer> updateSession(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "秒杀场次更新参数", required = true)
            @Validated(FlashSessionCmd.Update.class) @RequestBody FlashSessionCmd cmd) {
        FlashSession session = flashConverter.cmdToSession(cmd);
        return R.success(flashService.updateFlashSession(session));
    }

    @Operation(summary = "获取场次详情")
    @GetMapping("/sessions/{id:\\d+}")
    public R<FlashSessionVO> getSession(@Parameter(description = "秒杀场次ID", required = true) @PathVariable Long id) {
        return R.success(flashConverter.sessionToVo(flashService.getFlashSessionById(id)));
    }

    @Operation(summary = "删除场次")
    @DeleteMapping("/sessions/{id:\\d+}")
    public R<Integer> deleteSession(@Parameter(description = "秒杀场次ID", required = true) @PathVariable Long id) {
        return R.success(flashService.deleteFlashSession(id));
    }

    @Operation(summary = "分页查询场次列表")
    @GetMapping("/sessions")
    public R<Page<FlashSessionVO>> listSessions(@ParameterObject FlashSessionQuery query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<FlashSession> list = flashService.listFlashSessions(query);
        return R.success(PageUtils.convertPage(list, flashConverter::sessionListToVoList));
    }

    @Operation(summary = "批量修改场次状态")
    @PutMapping("/sessions/status")
    public R<Integer> updateSessionStatusBatch(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "需要批量更新状态的场次ID列表", required = true)
            @RequestBody List<Long> ids,
            @Parameter(description = "目标场次状态：0-禁用，1-启用", required = true)
            @RequestParam Integer sessionStatus) {
        return R.success(flashService.updateSessionStatusBatch(ids, sessionStatus));
    }

    @Operation(summary = "添加秒杀商品")
    @PostMapping("/products")
    public R<Long> createProduct(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "秒杀商品创建参数", required = true)
            @Validated(FlashProductCmd.Create.class) @RequestBody FlashProductCmd cmd) {
        FlashProduct product = flashConverter.cmdToProduct(cmd);
        flashService.addFlashProduct(product);
        return R.success(product.getId());
    }

    @Operation(summary = "批量添加秒杀商品")
    @PostMapping("/products/batch")
    public R<Integer> createProductBatch(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "批量秒杀商品创建参数列表", required = true)
            @RequestBody List<FlashProductCmd> cmdList) {
        List<FlashProduct> products = cmdList.stream()
                .map(flashConverter::cmdToProduct)
                .toList();
        return R.success(flashService.addFlashProductBatch(products));
    }

    @Operation(summary = "更新秒杀商品")
    @PutMapping("/products")
    public R<Integer> updateProduct(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "秒杀商品更新参数", required = true)
            @Validated(FlashProductCmd.Update.class) @RequestBody FlashProductCmd cmd) {
        FlashProduct product = flashConverter.cmdToProduct(cmd);
        return R.success(flashService.updateFlashProduct(product));
    }

    @Operation(summary = "获取秒杀商品详情")
    @GetMapping("/products/{id:\\d+}")
    public R<FlashProductVO> getProduct(@Parameter(description = "秒杀商品ID", required = true) @PathVariable Long id) {
        return R.success(getEnrichedProduct(id));
    }

    @Operation(summary = "删除秒杀商品")
    @DeleteMapping("/products/{id:\\d+}")
    public R<Integer> deleteProduct(@Parameter(description = "秒杀商品ID", required = true) @PathVariable Long id) {
        return R.success(flashService.deleteFlashProduct(id));
    }

    @Operation(summary = "批量删除秒杀商品")
    @DeleteMapping("/products/batch")
    public R<Integer> deleteProductBatch(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "需要批量删除的秒杀商品ID列表", required = true)
            @RequestBody List<Long> ids) {
        return R.success(flashService.deleteFlashProductBatch(ids));
    }

    @Operation(summary = "分页查询秒杀商品列表")
    @GetMapping("/products")
    public R<Page<FlashProductVO>> listProducts(@ParameterObject FlashProductQuery query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<FlashProduct> list = flashService.listFlashProducts(query);
        return R.success(PageUtils.convertPage(list, flashService::enrichWithSkuInfo));
    }

    private FlashProductVO getEnrichedProduct(Long id) {
        FlashProduct product = flashService.getFlashProductById(id);
        List<FlashProductVO> products = flashService.enrichWithSkuInfo(List.of(product));
        return products.isEmpty() ? flashConverter.productToVo(product) : products.get(0);
    }
}
