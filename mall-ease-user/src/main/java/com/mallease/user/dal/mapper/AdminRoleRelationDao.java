package com.mallease.user.dal.mapper;

import com.mallease.user.dal.entity.AdminRoleRelation;
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
public interface AdminRoleRelationDao {
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
    int insert(AdminRoleRelation record);

    /**
     * 选择性插入记录
     *
     * @param record 记录
     * @return 影响行数
     */
    int insertSelective(AdminRoleRelation record);

    /**
     * 根据主键查询
     *
     * @param id 主键ID
     * @return 记录
     */
    AdminRoleRelation selectByPrimaryKey(Long id);

    /**
     * 根据主键选择性更新
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(AdminRoleRelation record);

    /**
     * 根据主键更新
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKey(AdminRoleRelation record);

    /**
     * 根据管理员ID查询
     *
     * @param adminId 管理员ID
     * @return 记录列表
     */
    List<AdminRoleRelation> selectByAdminId(@Param("adminId") Long adminId);

    /**
     * 根据角色ID查询
     *
     * @param roleId 角色ID
     * @return 记录列表
     */
    List<AdminRoleRelation> selectByRoleId(@Param("roleId") Long roleId);

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

    /**
     * 根据管理员ID查询角色ID列表
     *
     * @param adminId 管理员ID
     * @return 角色ID列表
     */
    List<Long> selectRoleIdsByAdminId(@Param("adminId") Long adminId);

    /**
     * 根据角色ID列表批量删除
     *
     * @param roleIds 角色ID列表
     * @return 影响行数
     */
    int deleteByRoleIds(@Param("roleIds") List<Long> roleIds);

    /**
     * 批量插入
     *
     * @param list 记录列表
     * @return 影响行数
     */
    int insertBatch(List<AdminRoleRelation> list);
}

