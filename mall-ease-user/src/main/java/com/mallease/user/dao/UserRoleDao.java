package com.mallease.user.dao;

import com.mallease.user.pojo.UserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 后台用户角色表 Mapper 接口
 *
 * @author: Aulen
 * @create: 2025-11-08
 */
@Mapper
public interface UserRoleDao {
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
    int insert(UserRole record);

    /**
     * 选择性插入记录
     *
     * @param record 记录
     * @return 影响行数
     */
    int insertSelective(UserRole record);

    /**
     * 根据主键查询
     *
     * @param id 主键ID
     * @return 记录
     */
    UserRole selectByPrimaryKey(Long id);

    /**
     * 根据主键选择性更新
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(UserRole record);

    /**
     * 根据主键更新
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKey(UserRole record);

    /**
     * 查询所有记录
     *
     * @return 记录列表
     */
    List<UserRole> selectAll();

    /**
     * 根据状态查询
     *
     * @param status 状态：0->禁用；1->启用
     * @return 记录列表
     */
    List<UserRole> selectByStatus(@Param("status") Integer status);
}

