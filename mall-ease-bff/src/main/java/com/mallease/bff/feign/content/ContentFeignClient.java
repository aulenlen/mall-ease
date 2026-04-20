package com.mallease.bff.feign.content;

import com.mallease.common.api.R;
import com.mallease.common.dto.remote.ArticleDetailDTO;
import com.mallease.common.dto.remote.SlotRenderDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/content/slot/internal/render")
    R<SlotRenderDTO> getSlotRender(@RequestParam String slotCode,
                                   @RequestParam(defaultValue = "10") Integer limit);

    /**
     * 获取已发布文章详情
     *
     * @param id 文章ID
     * @return 文章详情
     */
    @GetMapping("/content/article/internal/published/{id}")
    R<ArticleDetailDTO> getPublishedArticleDetail(@PathVariable Long id);
}
