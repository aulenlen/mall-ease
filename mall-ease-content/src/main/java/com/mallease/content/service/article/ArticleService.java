package com.mallease.content.service.article;

import com.mallease.content.controller.admin.article.vo.ArticlePageReqVO;
import com.mallease.content.dal.entity.Article;

import java.util.List;
import java.util.Map;

/**
 * 文章服务接口。
 */
public interface ArticleService {

    /**
     * 创建文章。
     *
     * @param article 文章实体
     * @return 文章ID
     */
    Long create(Article article);

    /**
     * 更新文章。
     *
     * @param article 文章实体
     * @return 影响行数
     */
    int update(Article article);

    /**
     * 查询文章详情。
     *
     * @param id 文章ID
     * @return 文章实体
     */
    Article get(Long id);

    /**
     * 查询已发布文章详情。
     *
     * @param id 文章ID
     * @return 文章实体
     */
    Article getPublished(Long id);

    /**
     * 查询文章绑定的商品ID列表。
     *
     * @param articleId 文章ID
     * @return 商品ID列表
     */
    List<Long> listSpuIds(Long articleId);

    /**
     * 批量查询文章绑定的商品ID列表。
     *
     * @param articleIds 文章ID列表
     * @return 文章ID与商品ID列表映射
     */
    Map<Long, List<Long>> listSpuIdsMap(List<Long> articleIds);

    /**
     * 删除文章。
     *
     * @param id 文章ID
     * @return 影响行数
     */
    int delete(Long id);

    /**
     * 批量删除文章。
     *
     * @param ids 文章ID列表
     * @return 影响行数
     */
    int deleteBatch(List<Long> ids);

    /**
     * 分页查询文章列表。
     *
     * @param reqVO 查询条件
     * @return 文章列表
     */
    List<Article> page(ArticlePageReqVO reqVO);

    /**
     * 批量更新文章状态。
     *
     * @param ids 文章ID列表
     * @param status 状态值
     * @return 影响行数
     */
    int updateStatusBatch(List<Long> ids, Integer status);

    /**
     * 按槽位编码查询已发布文章。
     *
     * @param slotCode 槽位编码
     * @param limit 返回数量
     * @return 文章列表
     */
    List<Article> listPublishedBySlotCode(String slotCode, Integer limit);
}
