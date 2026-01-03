package com.mallease.user.dao;

import com.mallease.user.pojo.UserRoleResourceRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 后台角色资源关系表 Mapper 接口
 *
 * @author: Aulen
 * @create: 2025-11-08
 */
@Mapper
public interface UserRoleResourceRelationDao {
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
    int insert(UserRoleResourceRelation record);

    /**
     * 选择性插入记录
     *
     * @param record 记录
     * @return 影响行数
     */
    int insertSelective(UserRoleResourceRelation record);

    /**
     * 根据主键查询
     *
     * @param id 主键ID
     * @return 记录
     */
    UserRoleResourceRelation selectByPrimaryKey(Long id);

    /**
     * 根据主键选择性更新
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(UserRoleResourceRelation record);

    /**
     * 根据主键更新
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKey(UserRoleResourceRelation record);

    /**
     * 根据角色ID查询
     *
     * @param roleId 角色ID
     * @return 记录列表
     */
    List<UserRoleResourceRelation> selectByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据资源ID查询
     *
     * @param resourceId 资源ID
     * @return 记录列表
     */
    List<UserRoleResourceRelation> selectByResourceId(@Param("resourceId") Long resourceId);

    /**
     * 根据角色ID删除
     *
     * @param roleId 角色ID
     * @return 影响行数
     */
    int deleteByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据资源ID删除
     *
     * @param resourceId 资源ID
     * @return 影响行数
     */
    int deleteByResourceId(@Param("resourceId") Long resourceId);
}

