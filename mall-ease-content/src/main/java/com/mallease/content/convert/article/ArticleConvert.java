package com.mallease.content.convert.article;

import com.mallease.common.constant.ContentEditorConstant;
import com.mallease.common.dto.content.ContentDocument;
import com.mallease.common.dto.remote.ArticleDTO;
import com.mallease.common.dto.remote.ArticleDetailDTO;
import com.mallease.content.controller.admin.article.vo.ArticleReqVO;
import com.mallease.content.controller.admin.article.vo.ArticleRespVO;
import com.mallease.content.dal.entity.Article;
import com.mallease.content.service.article.ArticleExtrasCodec;
import com.mallease.content.service.article.ContentCodec;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface ArticleConvert {

    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "publishTime", ignore = true)
    @Mapping(target = "content", ignore = true)
    @Mapping(target = "extras", ignore = true)
    Article toArticleBase(ArticleReqVO reqVO);

    default Article toArticle(ArticleReqVO reqVO,
                              @Context ContentCodec contentCodec,
                              @Context ArticleExtrasCodec articleExtrasCodec) {
        if (reqVO == null) {
            return null;
        }
        Article article = toArticleBase(reqVO);
        article.setContent(contentCodec.encode(reqVO.getContent()));
        article.setExtras(defaultExtras(articleExtrasCodec.encode(
                reqVO.getCategoryLabel(),
                reqVO.getAuthor()
        )));
        return article;
    }

    @Mapping(target = "content", ignore = true)
    @Mapping(target = "editorSchemaVersion", ignore = true)
    @Mapping(target = "categoryLabel", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "spuIds", ignore = true)
    ArticleRespVO toArticleRespBase(Article entity);

    default ArticleRespVO toArticleResp(Article entity,
                                        List<Long> spuIds,
                                        @Context ContentCodec contentCodec,
                                        @Context ArticleExtrasCodec articleExtrasCodec) {
        if (entity == null) {
            return null;
        }
        ArticleRespVO respVO = toArticleRespBase(entity);
        ArticleExtrasSnapshot extrasSnapshot = decodeExtras(entity.getExtras(), articleExtrasCodec);
        ContentDocument content = contentCodec.decode(entity.getContent());
        respVO.setContent(content);
        respVO.setEditorSchemaVersion(content == null ? ContentEditorConstant.EDITOR_SCHEMA_VERSION_V1 : content.getVersion());
        respVO.setCategoryLabel(extrasSnapshot.categoryLabel());
        respVO.setAuthor(extrasSnapshot.author());
        respVO.setSpuIds(spuIds == null ? Collections.emptyList() : spuIds);
        return respVO;
    }

    default List<ArticleRespVO> toArticleRespList(List<Article> entityList,
                                                  Map<Long, List<Long>> spuIdsMap,
                                                  @Context ContentCodec contentCodec,
                                                  @Context ArticleExtrasCodec articleExtrasCodec) {
        if (entityList == null || entityList.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, List<Long>> safeSpuIdsMap = spuIdsMap == null ? Collections.emptyMap() : spuIdsMap;
        return entityList.stream()
                .map(entity -> toArticleResp(
                        entity,
                        safeSpuIdsMap.getOrDefault(entity.getId(), Collections.emptyList()),
                        contentCodec,
                        articleExtrasCodec
                ))
                .toList();
    }

    @Mapping(target = "categoryLabel", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "spuIds", ignore = true)
    ArticleDTO toArticleRemoteBase(Article entity);

    default ArticleDTO toArticleRemote(Article entity,
                                       List<Long> spuIds,
                                       @Context ArticleExtrasCodec articleExtrasCodec) {
        if (entity == null) {
            return null;
        }
        ArticleDTO dto = toArticleRemoteBase(entity);
        ArticleExtrasSnapshot extrasSnapshot = decodeExtras(entity.getExtras(), articleExtrasCodec);
        dto.setCategoryLabel(extrasSnapshot.categoryLabel());
        dto.setAuthor(extrasSnapshot.author());
        dto.setSpuIds(spuIds == null ? Collections.emptyList() : spuIds);
        return dto;
    }

    @Mapping(target = "content", ignore = true)
    @Mapping(target = "editorSchemaVersion", ignore = true)
    @Mapping(target = "extras", ignore = true)
    @Mapping(target = "spuIds", ignore = true)
    ArticleDetailDTO toArticleDetailRemoteBase(Article entity);

    default ArticleDetailDTO toArticleDetailRemote(Article entity,
                                                   List<Long> spuIds,
                                                   @Context ContentCodec contentCodec,
                                                   @Context ArticleExtrasCodec articleExtrasCodec) {
        if (entity == null) {
            return null;
        }
        ArticleDetailDTO detailDTO = toArticleDetailRemoteBase(entity);
        ContentDocument content = contentCodec.decode(entity.getContent());
        detailDTO.setContent(content);
        detailDTO.setEditorSchemaVersion(content == null ? ContentEditorConstant.EDITOR_SCHEMA_VERSION_V1 : content.getVersion());
        detailDTO.setExtras(articleExtrasCodec.decode(entity.getExtras()));
        detailDTO.setSpuIds(spuIds == null ? Collections.emptyList() : spuIds);
        return detailDTO;
    }

    default List<ArticleDTO> toArticleRemoteList(List<Article> entityList,
                                                 Map<Long, List<Long>> spuIdsMap,
                                                 @Context ArticleExtrasCodec articleExtrasCodec) {
        if (entityList == null || entityList.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, List<Long>> safeSpuIdsMap = spuIdsMap == null ? Collections.emptyMap() : spuIdsMap;
        return entityList.stream()
                .map(entity -> toArticleRemote(
                        entity,
                        safeSpuIdsMap.getOrDefault(entity.getId(), Collections.emptyList()),
                        articleExtrasCodec
                ))
                .toList();
    }

    private String defaultExtras(String extras) {
        return extras == null ? "{}" : extras;
    }

    private ArticleExtrasSnapshot decodeExtras(String extras, ArticleExtrasCodec articleExtrasCodec) {
        Map<String, Object> extrasMap = articleExtrasCodec.decode(extras);
        return new ArticleExtrasSnapshot(
                toText(extrasMap.get("category_label")),
                toText(extrasMap.get("author"))
        );
    }

    private String toText(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    record ArticleExtrasSnapshot(String categoryLabel, String author) {}
}
