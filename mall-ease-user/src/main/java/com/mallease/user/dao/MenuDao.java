package com.mallease.user.dao;

import com.mallease.user.model.data.Menu;
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
public interface MenuDao {
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
    int insert(Menu record);

    /**
     * 选择性插入记录
     *
     * @param record 记录
     * @return 影响行数
     */
    int insertSelective(Menu record);

    /**
     * 根据主键查询
     *
     * @param id 主键ID
     * @return 记录
     */
    Menu selectByPrimaryKey(Long id);

    /**
     * 根据主键选择性更新
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(Menu record);

    /**
     * 根据主键更新
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKey(Menu record);

    /**
     * 根据父级ID查询
     *
     * @param parentId 父级ID
     * @return 记录列表
     */
    List<Menu> selectByParentId(@Param("parentId") Long parentId);

    /**
     * 查询所有记录
     *
     * @return 记录列表
     */
    List<Menu> selectAll();

    /**
     * 根据ID列表批量查询
     *
     * @param ids ID列表
     * @return 记录列表
     */
    List<Menu> selectByIds(@Param("ids") List<Long> ids);
}

