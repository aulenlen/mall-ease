package com.mallease.user.dal.mapper;

import com.mallease.user.dal.entity.RoleResourceRelation;
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
public interface RoleResourceRelationDao {
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
    int insert(RoleResourceRelation record);

    /**
     * 选择性插入记录
     *
     * @param record 记录
     * @return 影响行数
     */
    int insertSelective(RoleResourceRelation record);

    /**
     * 根据主键查询
     *
     * @param id 主键ID
     * @return 记录
     */
    RoleResourceRelation selectByPrimaryKey(Long id);

    /**
     * 根据主键选择性更新
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(RoleResourceRelation record);

    /**
     * 根据主键更新
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKey(RoleResourceRelation record);

    /**
     * 根据角色ID查询
     *
     * @param roleId 角色ID
     * @return 记录列表
     */
    List<RoleResourceRelation> selectByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据资源ID查询
     *
     * @param resourceId 资源ID
     * @return 记录列表
     */
    List<RoleResourceRelation> selectByResourceId(@Param("resourceId") Long resourceId);

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

    /**
     * 根据角色ID列表查询资源ID列表
     *
     * @param roleIds 角色ID列表
     * @return 资源ID列表（已去重）
     */
    List<Long> selectResourceIdsByRoleIds(@Param("roleIds") List<Long> roleIds);

    /**
     * 根据角色ID列表批量删除
     *
     * @param roleIds 角色ID列表
     * @return 影响行数
     */
    int deleteByRoleIds(@Param("roleIds") List<Long> ids);

    /**
     * 批量插入
     *
     * @param list 记录列表
     * @return 影响行数
     */
    int insertBatch(List<RoleResourceRelation> list);
}

