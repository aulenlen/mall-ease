package com.mallease.product.dao;

import com.mallease.product.model.data.entity.ParamGroup;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 参数组 Mapper 接口
 * 说明：参数组用于组织参数定义，参数仅用于商品信息展示
 *
 * @author: Aulen
 * @create: 2025-12-11
 */
@Mapper
public interface ParamGroupDao {

    /**
     * 根据主键查询
     *
     * @param id 主键ID
     * @return 参数组记录
     */
    ParamGroup selectByPrimaryKey(Long id);

    /**
     * 根据ID列表批量查询
     *
     * @param ids ID列表
     * @return 参数组列表
     */
    List<ParamGroup> selectByIds(@Param("ids") List<Long> ids);

    /**
     * 根据名称查询
     *
     * @param name 参数组名称
     * @return 参数组记录
     */
    ParamGroup selectByName(@Param("name") String name);

    /**
     * 查询所有
     *
     * @return 参数组列表
     */
    List<ParamGroup> selectAll();

    /**
     * 根据关键字查询（模糊匹配名称）
     *
     * @param keyword 关键字（可为空）
     * @return 参数组列表
     */
    List<ParamGroup> selectByKeyword(@Param("keyword") String keyword);

    /**
     * 插入记录
     *
     * @param record 参数组记录
     * @return 影响行数
     */
    int insert(ParamGroup record);

    /**
     * 选择性插入记录
     *
     * @param record 参数组记录
     * @return 影响行数
     */
    int insertSelective(ParamGroup record);

    /**
     * 根据主键更新
     *
     * @param record 参数组记录
     * @return 影响行数
     */
    int updateByPrimaryKey(ParamGroup record);

    /**
     * 根据主键选择性更新
     *
     * @param record 参数组记录
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(ParamGroup record);

    /**
     * 逻辑删除
     *
     * @param id 主键ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);

    /**
     * 批量逻辑删除
     *
     * @param ids ID列表
     * @return 影响行数
     */
    int deleteBatch(@Param("ids") List<Long> ids);
}