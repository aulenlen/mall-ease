package com.mallease.content.controller.internal.article;

import com.mallease.common.api.R;
import com.mallease.common.dto.remote.ArticleDetailDTO;
import com.mallease.content.convert.article.ArticleConvert;
import com.mallease.content.dal.entity.Article;
import com.mallease.content.service.article.ArticleExtrasCodec;
import com.mallease.content.service.article.ArticleService;
import com.mallease.content.service.article.ContentCodec;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "文章内部接口")
@RestController
@RequiredArgsConstructor
@RequestMapping("/content/article/internal")
public class ArticleInternalController {

    private final ArticleConvert articleConvert;
    private final ArticleExtrasCodec articleExtrasCodec;
    private final ContentCodec contentCodec;
    private final ArticleService articleService;

    @Operation(summary = "获取已发布文章详情")
    @GetMapping("/published/{id}")
    public R<ArticleDetailDTO> getPublishedDetail(@PathVariable Long id) {
        Article article = articleService.getPublished(id);
        if (article == null) {
            return R.success(null);
        }
        return R.success(articleConvert.toArticleDetailRemote(
                article,
                articleService.listSpuIds(id),
                contentCodec,
                articleExtrasCodec
        ));
    }
}
