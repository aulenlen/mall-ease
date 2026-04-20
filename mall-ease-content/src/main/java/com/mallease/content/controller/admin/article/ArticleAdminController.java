package com.mallease.content.controller.admin.article;

import com.github.pagehelper.PageHelper;
import com.mallease.common.api.Page;
import com.mallease.common.api.PageUtils;
import com.mallease.common.api.R;
import com.mallease.common.api.ResultCode;
import com.mallease.content.constant.ContentStatusConstants;
import com.mallease.content.controller.admin.article.vo.ArticlePageReqVO;
import com.mallease.content.controller.admin.article.vo.ArticleReqVO;
import com.mallease.content.controller.admin.article.vo.ArticleRespVO;
import com.mallease.content.convert.article.ArticleConvert;
import com.mallease.content.dal.entity.Article;
import com.mallease.content.service.article.ArticleExtrasCodec;
import com.mallease.content.service.article.ArticleService;
import com.mallease.content.service.article.ContentCodec;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Tag(name = "文章管理", description = "文章增删改查")
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/content/articles")
public class ArticleAdminController {

    private final ArticleConvert articleConvert;
    private final ArticleExtrasCodec articleExtrasCodec;
    private final ContentCodec contentCodec;
    private final ArticleService articleService;

    @Operation(summary = "创建文章")
    @PostMapping
    public R<Long> create(@Validated(ArticleReqVO.Create.class) @RequestBody ArticleReqVO reqVO) {
        try {
            return R.success(articleService.create(articleConvert.toArticle(reqVO, contentCodec, articleExtrasCodec)));
        } catch (IllegalArgumentException e) {
            return R.failed(ResultCode.VALIDATE_FAILED, e.getMessage());
        }
    }

    @Operation(summary = "更新文章")
    @PutMapping("/{id}")
    public R<Integer> update(@PathVariable Long id,
                             @Validated(ArticleReqVO.Update.class) @RequestBody ArticleReqVO reqVO) {
        try {
            Article article = articleConvert.toArticle(reqVO, contentCodec, articleExtrasCodec);
            article.setId(id);
            int count = articleService.update(article);
            return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
        } catch (IllegalArgumentException e) {
            return R.failed(ResultCode.VALIDATE_FAILED, e.getMessage());
        }
    }

    @Operation(summary = "删除文章")
    @DeleteMapping("/{id}")
    public R<Integer> delete(@PathVariable Long id) {
        int count = articleService.delete(id);
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "批量删除文章")
    @DeleteMapping("/batch")
    public R<Integer> deleteBatch(@Parameter(description = "文章ID列表") @RequestParam("ids") List<Long> ids) {
        int count = articleService.deleteBatch(ids);
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "查询文章详情")
    @GetMapping("/{id}")
    public R<ArticleRespVO> get(@PathVariable Long id) {
        Article article = articleService.get(id);
        if (article == null) {
            return R.failed("文章不存在");
        }
        return R.success(articleConvert.toArticleResp(
                article,
                articleService.listSpuIds(id),
                contentCodec,
                articleExtrasCodec
        ));
    }

    @Operation(summary = "分页查询文章")
    @GetMapping
    public R<Page<ArticleRespVO>> page(@Validated @ModelAttribute ArticlePageReqVO reqVO) {
        PageHelper.startPage(reqVO.getPageNum(), reqVO.getPageSize());
        List<Article> articleList = articleService.page(reqVO);
        Map<Long, List<Long>> spuIdsMap = articleService.listSpuIdsMap(articleList.stream().map(Article::getId).toList());
        List<ArticleRespVO> respVOList = articleConvert.toArticleRespList(
                articleList,
                spuIdsMap,
                contentCodec,
                articleExtrasCodec
        );
        return R.success(PageUtils.buildPage(articleList, respVOList));
    }

    @Operation(summary = "批量更新状态")
    @PutMapping("/status")
    public R<Integer> updateStatus(@Parameter(description = "文章ID列表") @RequestParam("ids") List<Long> ids,
                                   @Parameter(description = ContentStatusConstants.ARTICLE_STATUS_SCHEMA) @RequestParam("status") Integer status) {
        if (!ContentStatusConstants.isValidArticleStatus(status)) {
            return R.failed(ResultCode.VALIDATE_FAILED, ContentStatusConstants.ARTICLE_STATUS_INVALID_MESSAGE);
        }
        int count = articleService.updateStatusBatch(ids, status);
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

}
