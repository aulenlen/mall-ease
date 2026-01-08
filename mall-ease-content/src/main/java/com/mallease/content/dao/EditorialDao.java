package com.mallease.content.dao;

import com.mallease.content.model.client.query.EditorialQuery;
import com.mallease.content.model.data.entity.Editorial;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 编辑精选表 Mapper 接口
 *
 * @author: Aulen
 * @create: 2026-01-07
 */
@Mapper
public interface EditorialDao {

    /**
     * 插入记录
     *
     * @param record 记录
     * @return 影响行数
     */
    int insert(Editorial record);

    /**
     * 选择性插入记录
     *
     * @param record 记录
     * @return 影响行数
     */
    int insertSelective(Editorial record);

    /**
     * 根据主键查询
     *
     * @param id 主键ID
     * @return 记录
     */
    Editorial selectByPrimaryKey(Long id);

    /**
     * 根据主键选择性更新
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(Editorial record);

    /**
     * 条件查询列表
     *
     * @param query 查询条件
     * @return 编辑精选列表
     */
    List<Editorial> selectByQuery(@Param("query") EditorialQuery query);

    /**
     * 批量更新状态
     *
     * @param ids    编辑精选ID列表
     * @param status 状态：0-草稿 1-已发布 2-已下架
     * @return 更新的记录数
     */
    int updateStatusBatch(@Param("ids") List<Long> ids, @Param("status") Integer status);

    /**
     * 逻辑删除
     *
     * @param id 主键ID
     * @return 影响行数
     */
    int logicDeleteByPrimaryKey(Long id);

    /**
     * 批量逻辑删除
     *
     * @param ids 主键ID列表
     * @return 影响行数
     */
    int logicDeleteBatch(@Param("ids") List<Long> ids);

    /**
     * 查询已发布的编辑精选列表
     *
     * @param limit 返回数量
     * @return 编辑精选列表
     */
    List<Editorial> listPublished(@Param("limit") Integer limit);
}
