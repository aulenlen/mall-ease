package com.mallease.content.dal.mapper;

import com.mallease.content.controller.admin.article.vo.ArticlePageReqVO;
import com.mallease.content.dal.entity.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文章 Mapper 接口。
 */
@Mapper
public interface ArticleDao {

    /**
     * 选择性插入文章。
     *
     * @param record 文章实体
     * @return 影响行数
     */
    int insertSelective(Article record);

    /**
     * 按主键查询文章。
     *
     * @param id 文章ID
     * @return 文章实体
     */
    Article selectByPrimaryKey(Long id);

    /**
     * 查询已发布文章。
     *
     * @param id 文章ID
     * @return 文章实体
     */
    Article selectPublishedById(Long id);

    /**
     * 按条件查询文章列表。
     *
     * @param query 查询条件
     * @return 文章列表
     */
    List<Article> selectByQuery(@Param("query") ArticlePageReqVO query);

    /**
     * 按主键选择性更新文章。
     *
     * @param record 文章实体
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(Article record);

    /**
     * 批量更新文章状态。
     *
     * @param ids 文章ID列表
     * @param status 状态值
     * @return 影响行数
     */
    int updateStatusBatch(@Param("ids") List<Long> ids, @Param("status") Integer status);

    /**
     * 逻辑删除文章。
     *
     * @param id 文章ID
     * @return 影响行数
     */
    int logicDeleteByPrimaryKey(Long id);

    /**
     * 批量逻辑删除文章。
     *
     * @param ids 文章ID列表
     * @return 影响行数
     */
    int logicDeleteBatch(@Param("ids") List<Long> ids);

    /**
     * 按槽位编码查询已发布文章。
     *
     * @param slotCode 槽位编码
     * @param limit 返回数量
     * @return 文章列表
     */
    List<Article> listPublishedBySlotCode(@Param("slotCode") String slotCode, @Param("limit") Integer limit);
}
