package com.mallease.ums.dao;

import com.mallease.ums.pojo.UmsAdmin;
import com.mallease.ums.pojo.UmsMenu;
import com.mallease.ums.pojo.UmsResource;
import com.mallease.ums.pojo.UmsRole;
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
public interface UmsAdminDao {
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
    int insert(UmsAdmin record);

    /**
     * 选择性插入记录
     *
     * @param record 记录
     * @return 影响行数
     */
    int insertSelective(UmsAdmin record);

    /**
     * 根据主键查询
     *
     * @param id 主键ID
     * @return 记录
     */
    UmsAdmin selectByPrimaryKey(Long id);

    /**
     * 根据主键选择性更新
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(UmsAdmin record);

    /**
     * 根据主键更新
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKey(UmsAdmin record);

    /**
     * 根据用户名查询
     *
     * @param username 用户名
     * @return 记录
     */
    UmsAdmin selectByUsername(@Param("username") String username);

    /**
     * 查询所有记录
     *
     * @return 记录列表
     */
    List<UmsAdmin> selectAll();

    /**
     * 获取用户拥有的资源
     * @param adminId
     * @return
     */
    List<UmsResource> getResourceList(@Param("adminId") Long adminId);

    /**
     * 获取用户角色
     * @param adminId
     * @return
     */
    List<UmsRole> getRolesByAdminId(@Param("adminId") Long adminId);

    /**
     * 获取用户菜单
     * @param adminId
     * @return
     */
    List<UmsMenu> getMenusByAdminId(@Param("adminId") Long adminId);
}

