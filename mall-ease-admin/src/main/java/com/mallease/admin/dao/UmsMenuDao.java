package com.mallease.admin.dao;

import com.mallease.admin.pojo.UmsMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 后台菜单表 Mapper 接口
 *
 * @author: Aulen
 * @create: 2025-11-08
 */
@Mapper
public interface UmsMenuDao {
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
    int insert(UmsMenu record);

    /**
     * 选择性插入记录
     *
     * @param record 记录
     * @return 影响行数
     */
    int insertSelective(UmsMenu record);

    /**
     * 根据主键查询
     *
     * @param id 主键ID
     * @return 记录
     */
    UmsMenu selectByPrimaryKey(Long id);

    /**
     * 根据主键选择性更新
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(UmsMenu record);

    /**
     * 根据主键更新
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKey(UmsMenu record);

    /**
     * 根据父级ID查询
     *
     * @param parentId 父级ID
     * @return 记录列表
     */
    List<UmsMenu> selectByParentId(@Param("parentId") Long parentId);

    /**
     * 查询所有记录
     *
     * @return 记录列表
     */
    List<UmsMenu> selectAll();
}

