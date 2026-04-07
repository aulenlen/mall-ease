package com.mallease.marketing.service.flash;

import com.mallease.common.dto.remote.FlashDeductReqDTO;
import com.mallease.common.dto.remote.FlashDeductResultDTO;
import com.mallease.common.dto.remote.FlashRestoreReqDTO;
import com.mallease.marketing.controller.portal.flash.vo.FlashOrderConfirmRespVO;
import com.mallease.marketing.controller.portal.flash.vo.FlashOrderSubmitReqVO;
import com.mallease.marketing.controller.portal.flash.vo.OrderSubmitRespVO;

public interface FlashOrderService {
    FlashDeductResultDTO deductStock(FlashDeductReqDTO req);

    Long restoreStock(FlashRestoreReqDTO req);

    OrderSubmitRespVO submitFlashOrder(Long userId, FlashOrderSubmitReqVO reqVO);

    FlashOrderConfirmRespVO confirmOrder(Long userId, Long sessionId, Long spuId, Long skuId, Integer quantity);
}
