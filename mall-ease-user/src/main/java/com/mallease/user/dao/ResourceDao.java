package com.mallease.user.dao;

import com.mallease.user.model.data.Resource;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 后台资源表 Mapper 接口
 *
 * @author: Aulen
 * @create: 2025-11-07
 */
@Mapper
public interface ResourceDao {
    /**
     * 根据主键删除
     *
     * @param id 主键ID
     * @return 影响行数
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 插入记录
     *
     * @param record 记录
     * @return 影响行数
     */
    int insert(Resource record);

    /**
     * 选择性插入记录
     *
     * @param record 记录
     * @return 影响行数
     */
    int insertSelective(Resource record);

    /**
     * 根据主键查询
     *
     * @param id 主键ID
     * @return 记录
     */
    Resource selectByPrimaryKey(Long id);

    /**
     * 根据主键选择性更新
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(Resource record);

    /**
     * 根据主键更新
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKey(Resource record);

    /**
     * 根据分类ID查询
     *
     * @param categoryId 分类ID
     * @return 记录列表
     */
    List<Resource> selectByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * 查询所有记录
     *
     * @return 记录列表
     */
    List<Resource> selectAll();

    /**
     * 根据ID列表批量查询
     *
     * @param ids ID列表
     * @return 记录列表
     */
    List<Resource> selectByIds(@Param("ids") List<Long> ids);

    /**
     * 根据查询条件查询
     *
     * @param query 查询条件
     * @return 记录列表
     */
    List<Resource> selectByQuery(@Param("query") com.mallease.user.model.client.query.ResourceQuery query);
}

