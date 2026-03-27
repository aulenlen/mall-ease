package com.mallease.user.dal.mapper;

import com.mallease.user.dal.entity.Role;
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
public interface RoleDao {
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
    int insert(Role record);

    /**
     * 选择性插入记录
     *
     * @param record 记录
     * @return 影响行数
     */
    int insertSelective(Role record);

    /**
     * 根据主键查询
     *
     * @param id 主键ID
     * @return 记录
     */
    Role selectByPrimaryKey(Long id);

    /**
     * 根据主键选择性更新
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(Role record);

    /**
     * 根据主键更新
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKey(Role record);

    /**
     * 查询所有记录
     *
     * @return 记录列表
     */
    List<Role> selectAll();

    /**
     * 根据状态查询
     *
     * @param status 状态：0->禁用；1->启用
     * @return 记录列表
     */
    List<Role> selectByStatus(@Param("status") Integer status);

    /**
     * 根据关键词模糊查询
     *
     * @param keyword 关键词（角色名称）
     * @return 记录列表
     */
    List<Role> selectByKeyword(@Param("keyword") String keyword);

    /**
     * 批量删除
     *
     * @param ids ID列表
     * @return 影响行数
     */
    int deleteBatch(@Param("ids") List<Long> ids);

    /**
     * 根据ID列表批量查询
     *
     * @param ids ID列表
     * @return 记录列表
     */
    List<Role> selectByIds(@Param("ids") List<Long> ids);
}

