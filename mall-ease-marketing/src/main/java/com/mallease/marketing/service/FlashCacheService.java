package com.mallease.marketing.service;

import com.mallease.common.dto.remote.ProductDTO;
import com.mallease.common.dto.remote.SpuFlashOverlayDTO;
import com.mallease.marketing.model.data.entity.FlashSession;

import java.util.Map;

public interface FlashCacheService {

    void warmUpCurrentSession();

    void warmUpUpcomingSessions();

    void warmUpSessions(Map<Long, FlashSession> sessionMap);

    ProductDTO getHotDetail(Long sessionId, Long spuId);

    SpuFlashOverlayDTO getOverlay(Long sessionId, Long spuId);
}
