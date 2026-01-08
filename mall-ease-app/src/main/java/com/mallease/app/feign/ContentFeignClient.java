package com.mallease.app.feign;

import com.mallease.common.api.R;
import com.mallease.common.dto.remote.BannerDTO;
import com.mallease.common.dto.remote.EditorialDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 内容服务 Feign 客户端
 *
 * @author: Aulen
 * @create: 2026-01-08
 */
@FeignClient(name = "mall-ease-content")
public interface ContentFeignClient {

    /**
     * 获取首页轮播图
     *
     * @param position 投放位置
     * @return 轮播图列表
     */
    @GetMapping("/content/banner/internal/published")
    R<List<BannerDTO>> listPublishedBanners(@RequestParam(defaultValue = "home") String position);

    /**
     * 获取已发布编辑精选
     *
     * @param limit 返回数量
     * @return 编辑精选列表
     */
    @GetMapping("/content/editorial/internal/published")
    R<List<EditorialDTO>> listPublishedEditorials(@RequestParam(defaultValue = "10") Integer limit);
}