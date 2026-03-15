package com.mallease.marketing.service.impl;

import com.mallease.common.service.RedisService;
import com.mallease.marketing.service.FlashActivityService;
import com.mallease.marketing.service.FlashCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class FlashCacheServiceImpl implements FlashCacheService {

    private final RedisService redisService;
    private final FlashActivityService flashActivityService;

    @Override
    public void warmUpCurrentSession() {
        flashActivityService.getCurrentFlashProducts();
    }
}
