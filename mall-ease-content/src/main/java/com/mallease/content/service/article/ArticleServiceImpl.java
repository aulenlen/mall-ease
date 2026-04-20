package com.mallease.content.service.article;

import com.mallease.content.controller.admin.article.vo.ArticlePageReqVO;
import com.mallease.content.dal.entity.Article;
import com.mallease.content.dal.entity.ArticleSpuRelation;
import com.mallease.content.dal.mapper.ArticleDao;
import com.mallease.content.dal.mapper.ArticleSpuRelationDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 文章服务实现。
 */
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleDao articleDao;
    private final ArticleSpuRelationDao articleSpuRelationDao;
    private final ContentCodec contentCodec;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(Article article) {
        article.setDeleted(0);
        if (article.getStatus() == null) {
            article.setStatus(0);
        }
        if (article.getSort() == null) {
            article.setSort(0);
        }
        if (Integer.valueOf(1).equals(article.getStatus()) && article.getPublishTime() == null) {
            article.setPublishTime(LocalDateTime.now());
        }
        article.setCreateTime(LocalDateTime.now());
        List<Long> derivedSpuIds = extractProductSpuIds(article);
        articleDao.insertSelective(article);
        saveSpuRelations(article.getId(), derivedSpuIds);
        return article.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(Article article) {
        if (Integer.valueOf(1).equals(article.getStatus()) && article.getPublishTime() == null) {
            Article existingArticle = articleDao.selectByPrimaryKey(article.getId());
            if (existingArticle != null && existingArticle.getPublishTime() == null) {
                article.setPublishTime(LocalDateTime.now());
            }
        }
        article.setUpdateTime(LocalDateTime.now());
        List<Long> derivedSpuIds = extractProductSpuIds(article);
        int count = articleDao.updateByPrimaryKeySelective(article);
        if (count <= 0) {
            return count;
        }
        articleSpuRelationDao.deleteByArticleId(article.getId());
        saveSpuRelations(article.getId(), derivedSpuIds);
        return count;
    }

    @Override
    public Article get(Long id) {
        return articleDao.selectByPrimaryKey(id);
    }

    @Override
    public Article getPublished(Long id) {
        return articleDao.selectPublishedById(id);
    }

    @Override
    public List<Long> listSpuIds(Long articleId) {
        List<ArticleSpuRelation> relations = articleSpuRelationDao.selectByArticleId(articleId);
        if (CollectionUtils.isEmpty(relations)) {
            return Collections.emptyList();
        }
        return relations.stream().map(ArticleSpuRelation::getSpuId).toList();
    }

    private List<ArticleSpuRelation> listSpuRelations(List<Long> articleIds) {
        if (CollectionUtils.isEmpty(articleIds)) {
            return Collections.emptyList();
        }
        return articleSpuRelationDao.selectByArticleIds(articleIds);
    }

    @Override
    public Map<Long, List<Long>> listSpuIdsMap(List<Long> articleIds) {
        if (CollectionUtils.isEmpty(articleIds)) {
            return Collections.emptyMap();
        }
        return listSpuRelations(articleIds).stream()
                .collect(Collectors.groupingBy(
                        ArticleSpuRelation::getArticleId,
                        Collectors.mapping(ArticleSpuRelation::getSpuId, Collectors.toList())
                ));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(Long id) {
        articleSpuRelationDao.deleteByArticleId(id);
        return articleDao.logicDeleteByPrimaryKey(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBatch(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return 0;
        }
        articleSpuRelationDao.deleteByArticleIds(ids);
        return articleDao.logicDeleteBatch(ids);
    }

    @Override
    public List<Article> page(ArticlePageReqVO reqVO) {
        return articleDao.selectByQuery(reqVO);
    }

    @Override
    public int updateStatusBatch(List<Long> ids, Integer status) {
        if (CollectionUtils.isEmpty(ids)) {
            return 0;
        }
        return articleDao.updateStatusBatch(ids, status);
    }

    @Override
    public List<Article> listPublishedBySlotCode(String slotCode, Integer limit) {
        return articleDao.listPublishedBySlotCode(slotCode, limit);
    }

    /**
     * 从正文中提取商品关联
     *
     * @param article 文章实体
     * @return 商品ID列表
     */
    private List<Long> extractProductSpuIds(Article article) {
        return contentCodec.extractProductSpuIds(contentCodec.decode(article.getContent()));
    }

    /**
     * 保存文章商品关联关系。
     *
     * @param articleId 文章ID
     * @param spuIds 商品ID列表
     */
    private void saveSpuRelations(Long articleId, List<Long> spuIds) {
        if (CollectionUtils.isEmpty(spuIds)) {
            return;
        }
        List<ArticleSpuRelation> relations = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < spuIds.size(); i++) {
            relations.add(ArticleSpuRelation.builder()
                    .articleId(articleId)
                    .spuId(spuIds.get(i))
                    .sort(i)
                    .createTime(now)
                    .build());
        }
        articleSpuRelationDao.insertBatch(relations);
    }
}
