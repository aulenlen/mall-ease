package com.mallease.bff.feign;

import com.mallease.common.api.R;
import com.mallease.common.dto.remote.FlashCurrentDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 营销服务 Feign 客户端
 *
 * @author: Aulen
 * @create: 2026-01-08
 */
@FeignClient(name = "mall-ease-marketing")
public interface MarketingFeignClient {

    /**
     * 获取当前秒杀数据
     *
     * @return 当前秒杀场次及商品列表
     */
    @GetMapping("/internal/marketing/flash/current")
    R<FlashCurrentDTO> getCurrentFlashData();
}
