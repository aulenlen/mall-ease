package com.mallease.content.dal.mapper;

import com.mallease.content.dal.entity.ArticleSpuRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文章商品关联 Mapper 接口。
 */
@Mapper
public interface ArticleSpuRelationDao {

    /**
     * 查询单篇文章的商品关联。
     *
     * @param articleId 文章ID
     * @return 关联列表
     */
    List<ArticleSpuRelation> selectByArticleId(@Param("articleId") Long articleId);

    /**
     * 批量查询文章商品关联。
     *
     * @param articleIds 文章ID列表
     * @return 关联列表
     */
    List<ArticleSpuRelation> selectByArticleIds(@Param("articleIds") List<Long> articleIds);

    /**
     * 删除单篇文章的商品关联。
     *
     * @param articleId 文章ID
     * @return 影响行数
     */
    int deleteByArticleId(@Param("articleId") Long articleId);

    /**
     * 批量删除文章商品关联。
     *
     * @param articleIds 文章ID列表
     * @return 影响行数
     */
    int deleteByArticleIds(@Param("articleIds") List<Long> articleIds);

    /**
     * 批量插入文章商品关联。
     *
     * @param list 关联列表
     * @return 影响行数
     */
    int insertBatch(@Param("list") List<ArticleSpuRelation> list);

    /**
     * 删除文章下指定商品关联。
     *
     * @param articleId 文章ID
     * @param spuIds 商品ID列表
     * @return 影响行数
     */
    int deleteByArticleIdAndSpuIds(@Param("articleId") Long articleId, @Param("spuIds") List<Long> spuIds);
}
