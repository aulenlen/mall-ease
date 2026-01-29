package com.mallease.trade.controller;

import com.mallease.common.api.R;
import com.mallease.common.dto.remote.CartItemDTO;
import com.mallease.common.dto.remote.SkuSimpleDTO;
import com.mallease.common.exception.ApiException;
import com.mallease.trade.converter.CartItemConverter;
import com.mallease.trade.feign.ProductFeignClient;
import com.mallease.trade.model.client.cmd.AddCartItemCmd;
import com.mallease.trade.model.client.cmd.CartCheckedCmd;
import com.mallease.trade.model.client.cmd.CartItemCmd;
import com.mallease.trade.model.client.cmd.DeleteCartItemCmd;
import com.mallease.trade.model.client.vo.CartItemVO;
import com.mallease.trade.model.client.vo.CartVO;
import com.mallease.trade.model.data.entity.CartItem;
import com.mallease.trade.service.CartItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 购物车管理
 *
 * @author: Aulen
 * @create: 2026-01-27
 */
@Tag(name = "购物车管理", description = "购物车增删改查、选中状态管理")
@Slf4j
@RestController
@RequestMapping("/trade/cart")
@RequiredArgsConstructor
public class CartItemController {

    private final CartItemService cartItemService;
    private final CartItemConverter cartItemConverter;
    private final ProductFeignClient productFeignClient;

    // 前台接口

    @Operation(summary = "前台获取购物车", description = "获取购物车列表及汇总信息（聚合商品数据）")
    @GetMapping("/portal/list")
    public R<CartVO> portalList(
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId) {

        List<CartItem> cartItems = cartItemService.listByUserId(userId);
        if (cartItems.isEmpty()) {
            return R.success(buildEmptyCart());
        }

        List<Long> skuIds = cartItems.stream().map(CartItem::getSkuId).distinct().toList();
        Map<Long, SkuSimpleDTO> skuMap = fetchSkuMap(skuIds);

        List<CartItemVO> items = cartItems.stream().map(item -> {
            CartItemVO vo = cartItemConverter.entityToVo(item);
            SkuSimpleDTO skuInfo = skuMap.get(item.getSkuId());

            if (skuInfo != null) {
                vo.setSpuName(skuInfo.getSpuName());
                vo.setSkuPic(skuInfo.getSkuPic() != null ? skuInfo.getSkuPic() : skuInfo.getSpuPic());
                vo.setCurrentPrice(skuInfo.getOriginalPrice());
                vo.setPriceChanged(item.getPrice() != null &&
                        item.getPrice().compareTo(skuInfo.getOriginalPrice()) != 0);
                vo.setValid(true);
                vo.setInStock(true);  // TODO: 从库存服务获取实时库存
                vo.setStock(999);     // TODO: 从库存服务获取实时库存
            } else {
                vo.setValid(false);
                vo.setInStock(false);
                vo.setInvalidReason("商品已下架");
            }

            BigDecimal price = vo.getCurrentPrice() != null ? vo.getCurrentPrice() : vo.getPrice();
            vo.setSubtotal(price.multiply(BigDecimal.valueOf(vo.getQuantity())));
            return vo;
        }).toList();

        return R.success(buildCartVO(items));
    }

    @Operation(summary = "前台添加购物车", description = "前台 App/H5 添加商品到购物车")
    @PostMapping("/portal/add")
    public R<Long> portalAdd(@Validated @RequestBody AddCartItemCmd cmd) {
        R<List<SkuSimpleDTO>> skuResult = productFeignClient.listSkuSimpleByIds(List.of(cmd.getSkuId()));
        if (skuResult == null || skuResult.getData() == null || skuResult.getData().isEmpty()) {
            log.error("获取SKU信息失败, skuId: {}", cmd.getSkuId());
            throw new ApiException("商品不存在或已下架");
        }
        SkuSimpleDTO skuInfo = skuResult.getData().get(0);

        CartItem cartItem = new CartItem();
        cartItem.setUserId(cmd.getUserId());
        cartItem.setSkuId(cmd.getSkuId());
        cartItem.setSpuId(skuInfo.getSpuId());
        cartItem.setQuantity(cmd.getQuantity() != null ? cmd.getQuantity() : 1);
        cartItem.setSpuName(skuInfo.getSpuName());
        cartItem.setSkuPic(skuInfo.getSkuPic() != null ? skuInfo.getSkuPic() : skuInfo.getSpuPic());
        cartItem.setSkuAttrs(skuInfo.getAttrValues());
        cartItem.setPrice(skuInfo.getOriginalPrice());
        cartItem.setChecked(1);

        return R.success(cartItemService.add(cartItem));
    }

    @Operation(summary = "前台获取购物车数量", description = "前台 App/H5 获取购物车商品数量")
    @GetMapping("/portal/count")
    public R<Integer> portalCount(@RequestParam Long userId) {
        return R.success(cartItemService.countByUserId(userId));
    }

    @Operation(summary = "更新购物车数量")
    @PutMapping("/portal/quantity")
    public R<Integer> updateQuantity(@Validated(CartItemCmd.Update.class) @RequestBody CartItemCmd cmd) {
        return R.success(cartItemService.updateQuantity(cmd.getId(), cmd.getQuantity()));
    }

    @Operation(summary = "批量更新选中状态")
    @PutMapping("/portal/checked")
    public R<Integer> updateChecked(@Validated @RequestBody CartCheckedCmd cmd) {
        return R.success(cartItemService.updateChecked(cmd.getIds(), cmd.getChecked()));
    }

    @Operation(summary = "全选/取消全选")
    @PutMapping("/portal/checked/all")
    public R<Integer> updateCheckedAll(
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId,
            @Parameter(description = "选中状态: 0-取消全选, 1-全选", required = true) @RequestParam Integer checked) {
        return R.success(cartItemService.updateCheckedAll(userId, checked));
    }

    @Operation(summary = "删除购物车项")
    @DeleteMapping("/portal/{id}")
    public R<Integer> delete(
            @Parameter(description = "购物车项ID") @PathVariable Long id) {
        return R.success(cartItemService.delete(id));
    }

    @Operation(summary = "批量删除购物车项")
    @DeleteMapping("/portal/batch")
    public R<Integer> deleteBatch(@Validated @RequestBody DeleteCartItemCmd cmd) {
        return R.success(cartItemService.deleteBatch(cmd.getIds()));
    }

    @Operation(summary = "清空购物车")
    @DeleteMapping("/portal/clear")
    public R<Integer> clear(
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId) {
        return R.success(cartItemService.clearByUserId(userId));
    }

    // 内部接口（供 Order 服务调用）

    @Operation(summary = "获取用户已选中的购物车项", description = "内部调用，下单时获取已选中商品")
    @GetMapping("/internal/checked")
    public R<List<CartItemDTO>> internalListChecked(@RequestParam Long userId) {
        List<CartItem> items = cartItemService.listCheckedByUserId(userId);
        return R.success(cartItemConverter.entityListToDtoList(items));
    }

    @Operation(summary = "清除已选中的购物车项", description = "内部调用，下单成功后调用")
    @DeleteMapping("/internal/clearChecked")
    public R<Integer> internalClearChecked(@RequestParam Long userId) {
        return R.success(cartItemService.deleteCheckedByUserId(userId));
    }

    // 私有方法

    /**
     * 批量获取 SKU 信息
     */
    private Map<Long, SkuSimpleDTO> fetchSkuMap(List<Long> skuIds) {
        try {
            R<List<SkuSimpleDTO>> result = productFeignClient.listSkuSimpleByIds(skuIds);
            if (result != null && result.getData() != null) {
                return result.getData().stream()
                        .collect(Collectors.toMap(SkuSimpleDTO::getId, sku -> sku, (a, b) -> a));
            }
        } catch (Exception e) {
            log.error("获取SKU信息失败", e);
        }
        return Collections.emptyMap();
    }

    /**
     * 构建空购物车
     */
    private CartVO buildEmptyCart() {
        return CartVO.builder()
                .items(Collections.emptyList())
                .validCount(0)
                .invalidCount(0)
                .checkedCount(0)
                .checkedAmount(BigDecimal.ZERO)
                .allChecked(true)
                .hasPriceChanged(false)
                .hasOutOfStock(false)
                .build();
    }

    /**
     * 构建购物车汇总
     */
    private CartVO buildCartVO(List<CartItemVO> items) {
        int validCount = 0;
        int invalidCount = 0;
        int checkedCount = 0;
        BigDecimal checkedAmount = BigDecimal.ZERO;
        boolean hasPriceChanged = false;
        boolean hasOutOfStock = false;

        for (CartItemVO item : items) {
            if (Boolean.TRUE.equals(item.getValid())) {
                validCount++;
                if (Boolean.TRUE.equals(item.getPriceChanged())) {
                    hasPriceChanged = true;
                }
                if (!Boolean.TRUE.equals(item.getInStock())) {
                    hasOutOfStock = true;
                }
                if (item.getChecked() != null && item.getChecked() == 1) {
                    checkedCount += item.getQuantity();
                    checkedAmount = checkedAmount.add(item.getSubtotal());
                }
            } else {
                invalidCount++;
            }
        }

        boolean allChecked = validCount > 0 && checkedCount > 0 &&
                items.stream()
                        .filter(i -> Boolean.TRUE.equals(i.getValid()))
                        .allMatch(i -> i.getChecked() != null && i.getChecked() == 1);

        return CartVO.builder()
                .items(items)
                .validCount(validCount)
                .invalidCount(invalidCount)
                .checkedCount(checkedCount)
                .checkedAmount(checkedAmount)
                .allChecked(allChecked)
                .hasPriceChanged(hasPriceChanged)
                .hasOutOfStock(hasOutOfStock)
                .build();
    }
}
