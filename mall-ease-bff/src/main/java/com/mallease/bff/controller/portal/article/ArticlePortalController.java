package com.mallease.bff.controller.portal.article;

import com.mallease.bff.controller.portal.article.vo.ArticleDetailRespVO;
import com.mallease.bff.service.article.ArticleService;
import com.mallease.common.api.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** BFF 文章接口。 */
@Tag(name = "BFF文章", description = "文章详情聚合接口")
@RestController
@RequestMapping("/portal/articles")
@RequiredArgsConstructor
public class ArticlePortalController {

    private final ArticleService articleService;

    @Operation(summary = "获取文章详情", description = "聚合返回文章详情和文中商品池")
    @GetMapping("/{id}")
    public R<ArticleDetailRespVO> getDetail(@PathVariable Long id) {
        ArticleDetailRespVO detailRespVO = articleService.getDetail(id);
        if (detailRespVO == null) {
            return R.failed("文章不存在或暂不可用");
        }
        return R.success(detailRespVO);
    }
}
