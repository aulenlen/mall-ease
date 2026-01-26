package com.mallease.user.dao;

import com.mallease.user.model.data.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 后台用户表 Mapper 接口
 *
 * @author: Aulen
 * @create: 2025-11-07
 */
@Mapper
public interface AdminDao {
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
    int insert(Admin record);

    /**
     * 选择性插入记录
     *
     * @param record 记录
     * @return 影响行数
     */
    int insertSelective(Admin record);

    /**
     * 根据主键查询
     *
     * @param id 主键ID
     * @return 记录
     */
    Admin selectByPrimaryKey(Long id);

    /**
     * 根据主键选择性更新
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(Admin record);

    /**
     * 根据主键更新
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKey(Admin record);

    /**
     * 根据用户名查询
     *
     * @param username 用户名
     * @return 记录
     */
    Admin selectByUsername(@Param("username") String username);

    /**
     * 查询所有记录
     *
     * @return 记录列表
     */
    List<Admin> selectAll();

    /**
     * 根据查询条件查询
     *
     * @param query 查询条件
     * @return 记录列表
     */
    List<Admin> selectByQuery(@Param("query") com.mallease.user.model.client.query.AdminQuery query);
}

