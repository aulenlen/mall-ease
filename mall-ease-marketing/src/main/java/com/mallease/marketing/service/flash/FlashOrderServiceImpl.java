package com.mallease.marketing.service.flash;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mallease.common.api.R;
import com.mallease.common.dto.remote.*;
import com.mallease.common.exception.ApiException;
import com.mallease.common.service.TypedRedisService;
import com.mallease.marketing.constant.FlashRedisKeys;
import com.mallease.marketing.controller.portal.flash.vo.FlashOrderConfirmRespVO;
import com.mallease.marketing.controller.portal.flash.vo.FlashOrderSubmitReqVO;
import com.mallease.marketing.controller.portal.flash.vo.OrderSubmitRespVO;
import com.mallease.marketing.feign.FlashTradeOrderFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class FlashOrderServiceImpl implements FlashOrderService {
    private static final long SNAPSHOT_TTL_SECONDS = 300L;

    private final ObjectMapper objectMapper;
    private final TypedRedisService typedRedisService;
    private final RedisScript<Long> flashDeductStockScript;
    private final RedisScript<Long> flashRestoreStockScript;
    private final FlashTradeOrderFeignClient flashTradeOrderFeignClient;

    @Override
    public FlashDeductResultDTO deductStock(FlashDeductReqDTO req) {
        Long sessionId = req.getSessionId();
        Long skuId = req.getSkuId();
        Long userId = req.getUserId();
        Integer quantity = req.getQuantity();
        List<String> keys = List.of(
                FlashRedisKeys.stockKey(sessionId, skuId),
                FlashRedisKeys.userBoughtKey(sessionId, skuId, userId),
                FlashRedisKeys.sessionMetaKey(sessionId),
                FlashRedisKeys.limitKey(sessionId, skuId)
        );

        Long result = typedRedisService.executeScript(flashDeductStockScript, keys, quantity, System.currentTimeMillis());
        if (result == null) {
            throw new ApiException("秒杀扣减失败");
        }

        return switch (result.intValue()) {
            case 1 -> FlashDeductResultDTO.builder().success(true).build();
            case -1 -> FlashDeductResultDTO.builder().success(false).errorCode(-1).errorMessage("库存不足").build();
            case -2 -> FlashDeductResultDTO.builder().success(false).errorCode(-2).errorMessage("超出限购").build();
            case -3 ->
                    FlashDeductResultDTO.builder().success(false).errorCode(-3).errorMessage("活动未开始或已结束").build();
            case -4 ->
                    FlashDeductResultDTO.builder().success(false).errorCode(-4).errorMessage("场次信息不存在").build();
            default -> throw new ApiException("秒杀扣减返回值异常");
        };
    }

    @Override
    public Long restoreStock(FlashRestoreReqDTO req) {
        Long userId = req.getUserId();
        Long skuId = req.getSkuId();
        Long sessionId = req.getSessionId();
        Integer quantity = req.getQuantity();

        List<String> keys = List.of(
                FlashRedisKeys.stockKey(sessionId, skuId),
                FlashRedisKeys.userBoughtKey(sessionId, skuId, userId),
                FlashRedisKeys.limitKey(sessionId, skuId)
        );
        Long count;
        try {
            count = typedRedisService.executeScript(flashRestoreStockScript, keys, quantity);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ApiException("取消订单失败");
        }
        return count;
    }

    @Override
    public OrderSubmitRespVO submitFlashOrder(Long userId, FlashOrderSubmitReqVO reqVO) {
        FlashOrderConfirmRespVO snapshot = typedRedisService.getJson(
                FlashRedisKeys.orderSnapshotKey(userId, reqVO.getRequestId()),
                FlashOrderConfirmRespVO.class
        );

        if (snapshot == null) {
            throw new ApiException("确认页已失效，请重新抢购");
        }
        if (!Objects.equals(snapshot.getUserId(), userId)) {
            throw new ApiException("订单请求非法");
        }
        if (snapshot.getItem() == null) {
            throw new ApiException("确认页数据不完整，请重新抢购");
        }
        if (!Objects.equals(snapshot.getSessionId(), reqVO.getSessionId())
                || !Objects.equals(snapshot.getItem().getSkuId(), reqVO.getSkuId())
                || !Objects.equals(snapshot.getItem().getQuantity(), reqVO.getQuantity())) {
            throw new ApiException("订单请求非法");
        }
        if (snapshot.getFlashProductId() == null) {
            throw new ApiException("确认页数据不完整，请重新抢购");
        }

        FlashDeductResultDTO deductResult = deductStock(
                FlashDeductReqDTO.builder()
                        .flashLimit(snapshot.getFlashLimit() != null ? snapshot.getFlashLimit() : 0)
                        .skuId(snapshot.getItem().getSkuId())
                        .quantity(snapshot.getItem().getQuantity())
                        .sessionId(snapshot.getSessionId())
                        .userId(userId)
                        .build()
        );

        if (!Boolean.TRUE.equals(deductResult.getSuccess())) {
            throw new ApiException(deductResult.getErrorMessage());
        }

        R<FlashCreateOrderRespDTO> tradeResp = flashTradeOrderFeignClient.createFlashOrder(
                FlashCreateOrderReqDTO.builder()
                        .userId(userId)
                        .requestId(snapshot.getRequestId())
                        .sessionId(snapshot.getSessionId())
                        .flashProductId(snapshot.getFlashProductId())
                        .spuId(snapshot.getItem().getSpuId())
                        .skuId(snapshot.getItem().getSkuId())
                        .quantity(snapshot.getItem().getQuantity())
                        .flashPrice(snapshot.getItem().getPrice())
                        .spuName(snapshot.getItem().getSpuName())
                        .skuPic(snapshot.getItem().getSkuPic())
                        .skuAttrs(snapshot.getItem().getSkuAttrs())
                        .receiverName(reqVO.getReceiverName())
                        .receiverPhone(reqVO.getReceiverPhone())
                        .receiverProvince(reqVO.getReceiverProvince())
                        .receiverCity(reqVO.getReceiverCity())
                        .receiverDistrict(reqVO.getReceiverDistrict())
                        .receiverAddress(reqVO.getReceiverAddress())
                        .remark(reqVO.getRemark())
                        .build()
        );

        if (tradeResp == null || !tradeResp.isSuccess() || tradeResp.getData() == null) {
            throw new ApiException(tradeResp != null ? tradeResp.getMessage() : "秒杀订单创建失败");
        }

        FlashCreateOrderRespDTO tradeOrder = tradeResp.getData();
        typedRedisService.delete(FlashRedisKeys.orderSnapshotKey(userId, reqVO.getRequestId()));
        return OrderSubmitRespVO.builder()
                .orderNo(tradeOrder.getOrderNo())
                .payAmount(tradeOrder.getPayAmount())
                .payExpireTime(tradeOrder.getPayExpireTime())
                .orderStatus(tradeOrder.getOrderStatus())
                .serverTime(tradeOrder.getServerTime())
                .build();
    }

    @Override
    public FlashOrderConfirmRespVO confirmOrder(Long userId, Long sessionId, Long spuId, Long skuId, Integer quantity) {
        if (userId == null) {
            throw new ApiException("用户未登录");
        }

        if (sessionId == null) {
            throw new ApiException("秒杀场次不正确");
        }

        if (spuId == null) {
            throw new ApiException("秒杀商品不正确");
        }

        if (skuId == null) {
            throw new ApiException("请选择要结算的商品");
        }

        if (quantity == null || quantity <= 0) {
            throw new ApiException("商品数量不正确");
        }

        String requestId = UUID.randomUUID().toString().replace("-", "");

        ProductDTO productDTO = typedRedisService.getJson(FlashRedisKeys.detailKey(sessionId, spuId), ProductDTO.class);
        if (productDTO == null || productDTO.getSkuList() == null || productDTO.getSkuList().isEmpty()) {
            throw new ApiException("秒杀商品信息已失效，请刷新后重试");
        }

        ProductDTO.SkuViewInfo targetSkuView = productDTO.getSkuList().stream().filter(Objects::nonNull).filter(item -> item.getSku() != null && Objects.equals(item.getSku().getId(), skuId)).findFirst().orElseThrow(() -> new ApiException("秒杀商品规格不存在或已失效"));

        ProductDTO.SkuInfo sku = targetSkuView.getSku();
        ProductDTO.SkuStockInfo stockInfo = targetSkuView.getStock();
        if (stockInfo != null && Boolean.FALSE.equals(stockInfo.getInStock())) {
            throw new ApiException("商品已抢光");
        }

        BigDecimal unitPrice = resolveUnitPrice(sku);
        BigDecimal originalUnitPrice = sku.getBasePrice() != null ? sku.getBasePrice() : unitPrice;
        BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
        BigDecimal originalTotalPrice = originalUnitPrice.multiply(BigDecimal.valueOf(quantity));
        BigDecimal discountAmount = originalTotalPrice.subtract(totalPrice);
        if (discountAmount.compareTo(BigDecimal.ZERO) < 0) {
            discountAmount = BigDecimal.ZERO;
        }

        FlashOrderConfirmRespVO confirmRespVO = FlashOrderConfirmRespVO.builder()
                .requestId(requestId)
                .sessionId(sessionId)
                .flashProductId(spuId)
                .userId(userId)
                .createTime(LocalDateTime.now())
                .item(FlashOrderConfirmRespVO.FlashOrderItemRespVO.builder()
                        .spuId(spuId)
                        .skuId(skuId)
                        .spuName(productDTO.getName())
                        .skuPic(sku.getPic())
                        .skuAttrs(toSkuAttrsJson(targetSkuView))
                        .price(unitPrice)
                        .quantity(quantity)
                        .subtotal(totalPrice)
                        .build())
                .totalAmount(totalPrice)
                .freightAmount(BigDecimal.ZERO)
                .discountAmount(discountAmount).payAmount(totalPrice)
                .build();

        typedRedisService.setJson(FlashRedisKeys.orderSnapshotKey(userId, requestId), confirmRespVO, SNAPSHOT_TTL_SECONDS);
        return confirmRespVO;
    }

    private BigDecimal resolveUnitPrice(ProductDTO.SkuInfo sku) {
        if (sku == null) {
            throw new ApiException("秒杀商品规格不存在或已失效");
        }
        if (sku.getDisplayPrice() != null) {
            return sku.getDisplayPrice();
        }
        if (sku.getPromotionPrice() != null) {
            return sku.getPromotionPrice();
        }
        if (sku.getBasePrice() != null) {
            return sku.getBasePrice();
        }
        throw new ApiException("秒杀商品价格异常");
    }

    private String toSkuAttrsJson(ProductDTO.SkuViewInfo skuViewInfo) {
        if (skuViewInfo == null || skuViewInfo.getSpecValues() == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(skuViewInfo.getSpecValues());
        } catch (JsonProcessingException ex) {
            throw new ApiException("商品规格数据异常");
        }
    }
}
