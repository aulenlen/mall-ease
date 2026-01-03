package com.mallease.user.dao;

import com.mallease.user.pojo.UserResource;
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
public interface UserResourceDao {
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
    int insert(UserResource record);

    /**
     * 选择性插入记录
     *
     * @param record 记录
     * @return 影响行数
     */
    int insertSelective(UserResource record);

    /**
     * 根据主键查询
     *
     * @param id 主键ID
     * @return 记录
     */
    UserResource selectByPrimaryKey(Long id);

    /**
     * 根据主键选择性更新
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(UserResource record);

    /**
     * 根据主键更新
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKey(UserResource record);

    /**
     * 根据分类ID查询
     *
     * @param categoryId 分类ID
     * @return 记录列表
     */
    List<UserResource> selectByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * 查询所有记录
     *
     * @return 记录列表
     */
    List<UserResource> selectAll();
}

