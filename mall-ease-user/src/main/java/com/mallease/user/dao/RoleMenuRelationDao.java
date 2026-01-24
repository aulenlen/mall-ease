package com.mallease.user.dao;

import com.mallease.user.model.data.RoleMenuRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 后台角色菜单关系表 Mapper 接口
 *
 * @author: Aulen
 * @create: 2026-01-23
 */
@Mapper
public interface RoleMenuRelationDao {
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
    int insert(RoleMenuRelation record);

    /**
     * 选择性插入记录
     *
     * @param record 记录
     * @return 影响行数
     */
    int insertSelective(RoleMenuRelation record);

    /**
     * 批量插入记录
     *
     * @param list 记录列表
     * @return 影响行数
     */
    int insertBatch(@Param("list") List<RoleMenuRelation> list);

    /**
     * 根据主键查询
     *
     * @param id 主键ID
     * @return 记录
     */
    RoleMenuRelation selectByPrimaryKey(Long id);

    /**
     * 根据主键选择性更新
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(RoleMenuRelation record);

    /**
     * 根据主键更新
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKey(RoleMenuRelation record);

    /**
     * 根据角色ID查询
     *
     * @param roleId 角色ID
     * @return 记录列表
     */
    List<RoleMenuRelation> selectByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据菜单ID查询
     *
     * @param menuId 菜单ID
     * @return 记录列表
     */
    List<RoleMenuRelation> selectByMenuId(@Param("menuId") Long menuId);

    /**
     * 根据角色ID删除
     *
     * @param roleId 角色ID
     * @return 影响行数
     */
    int deleteByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据菜单ID删除
     *
     * @param menuId 菜单ID
     * @return 影响行数
     */
    int deleteByMenuId(@Param("menuId") Long menuId);
}