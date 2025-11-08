package com.mallease.admin.dao;

import com.mallease.admin.pojo.UmsRole;
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
public interface UmsRoleDao {
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
    int insert(UmsRole record);

    /**
     * 选择性插入记录
     *
     * @param record 记录
     * @return 影响行数
     */
    int insertSelective(UmsRole record);

    /**
     * 根据主键查询
     *
     * @param id 主键ID
     * @return 记录
     */
    UmsRole selectByPrimaryKey(Long id);

    /**
     * 根据主键选择性更新
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(UmsRole record);

    /**
     * 根据主键更新
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKey(UmsRole record);

    /**
     * 查询所有记录
     *
     * @return 记录列表
     */
    List<UmsRole> selectAll();

    /**
     * 根据状态查询
     *
     * @param status 状态：0->禁用；1->启用
     * @return 记录列表
     */
    List<UmsRole> selectByStatus(@Param("status") Integer status);
}

