package com.mallease.user.dao;

import com.mallease.user.pojo.UserAdminRoleRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 后台用户和角色关系表 Mapper 接口
 *
 * @author: Aulen
 * @create: 2025-11-08
 */
@Mapper
public interface UserAdminRoleRelationDao {
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
    int insert(UserAdminRoleRelation record);

    /**
     * 选择性插入记录
     *
     * @param record 记录
     * @return 影响行数
     */
    int insertSelective(UserAdminRoleRelation record);

    /**
     * 根据主键查询
     *
     * @param id 主键ID
     * @return 记录
     */
    UserAdminRoleRelation selectByPrimaryKey(Long id);

    /**
     * 根据主键选择性更新
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(UserAdminRoleRelation record);

    /**
     * 根据主键更新
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKey(UserAdminRoleRelation record);

    /**
     * 根据管理员ID查询
     *
     * @param adminId 管理员ID
     * @return 记录列表
     */
    List<UserAdminRoleRelation> selectByAdminId(@Param("adminId") Long adminId);

    /**
     * 根据角色ID查询
     *
     * @param roleId 角色ID
     * @return 记录列表
     */
    List<UserAdminRoleRelation> selectByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据管理员ID删除
     *
     * @param adminId 管理员ID
     * @return 影响行数
     */
    int deleteByAdminId(@Param("adminId") Long adminId);

    /**
     * 根据角色ID删除
     *
     * @param roleId 角色ID
     * @return 影响行数
     */
    int deleteByRoleId(@Param("roleId") Long roleId);
}

