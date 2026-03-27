package com.mallease.trade.controller.portal.cart;

import com.mallease.common.api.R;
import com.mallease.common.util.LoginContextUtil;
import com.mallease.trade.controller.portal.cart.vo.CartItemAddReqVO;
import com.mallease.trade.controller.portal.cart.vo.CartCheckedUpdateReqVO;
import com.mallease.trade.controller.portal.cart.vo.CartItemQuantityUpdateReqVO;
import com.mallease.trade.controller.portal.cart.vo.CartItemBatchDeleteReqVO;
import com.mallease.trade.controller.portal.cart.vo.CartPageRespVO;
import com.mallease.trade.controller.portal.cart.vo.CartSummaryRespVO;
import com.mallease.trade.service.cart.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 购物车控制器。
 *
 * @author: Aulen
 * @create: 2026-01-27
 */
@Tag(name = "购物车管理", description = "购物车增删改查与选中状态管理")
@RestController
@RequestMapping("/trade/cart")
@RequiredArgsConstructor
public class CartPortalController {

    private final CartService cartItemService;

    @Operation(summary = "前台获取购物车", description = "分页获取购物车列表，并返回整车汇总信息")
    @GetMapping("/portal/list")
    public R<CartPageRespVO> portalList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "20") Integer pageSize) {
        return R.success(cartItemService.getCartPage(pageNum, pageSize));
    }

    @Operation(summary = "前台添加购物车", description = "前台 App/H5 添加商品到购物车")
    @PostMapping("/portal/add")
    public R<Long> portalAdd(@Validated @RequestBody CartItemAddReqVO reqVO) {
        Long userId = LoginContextUtil.getUserId();
        return R.success(cartItemService.addToCart(userId, reqVO.getSkuId(), reqVO.getQuantity()));
    }

    @Operation(summary = "前台获取购物车数量", description = "前台 App/H5 获取购物车商品数量")
    @GetMapping("/portal/count")
    public R<Integer> portalCount() {
        Long userId = LoginContextUtil.getUserId();
        return R.success(cartItemService.countByUserId(userId));
    }

    @Operation(summary = "更新购物车数量")
    @PutMapping("/portal/quantity")
    public R<CartSummaryRespVO> updateQuantity(@Validated(CartItemQuantityUpdateReqVO.Update.class) @RequestBody CartItemQuantityUpdateReqVO reqVO) {
        return R.success(cartItemService.updateQuantity(reqVO.getId(), reqVO.getQuantity()));
    }

    @Operation(summary = "批量更新选中状态")
    @PutMapping("/portal/checked")
    public R<CartSummaryRespVO> updateChecked(@Validated @RequestBody CartCheckedUpdateReqVO reqVO) {
        Long userId = LoginContextUtil.getUserId();
        return R.success(cartItemService.updateChecked(userId, reqVO.getIds(), reqVO.getChecked()));
    }

    @Operation(summary = "全选/取消全选")
    @PutMapping("/portal/checked/all")
    public R<CartSummaryRespVO> updateCheckedAll(
            @Parameter(description = "选中状态 0-取消全选 1-全选", required = true) @RequestParam Integer checked) {
        Long userId = LoginContextUtil.getUserId();
        return R.success(cartItemService.updateCheckedAll(userId, checked));
    }

    @Operation(summary = "删除购物车项")
    @DeleteMapping("/portal/{id}")
    public R<Integer> delete(@Parameter(description = "购物车项ID") @PathVariable Long id) {
        return R.success(cartItemService.delete(id));
    }

    @Operation(summary = "批量删除购物车项")
    @DeleteMapping("/portal/batch")
    public R<Integer> deleteBatch(@Validated @RequestBody CartItemBatchDeleteReqVO reqVO) {
        return R.success(cartItemService.deleteBatch(reqVO.getIds()));
    }

    @Operation(summary = "清空购物车")
    @DeleteMapping("/portal/clear")
    public R<Integer> clear() {
        Long userId = LoginContextUtil.getUserId();
        return R.success(cartItemService.clearByUserId(userId));
    }
}
