package com.mallease.pms.dao;

import com.mallease.pms.pojo.PmsParamGroup;
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
public interface PmsParamGroupDao {

    /**
     * 根据主键查询
     *
     * @param id 主键ID
     * @return 参数组记录
     */
    PmsParamGroup selectByPrimaryKey(Long id);

    /**
     * 根据ID列表批量查询
     *
     * @param ids ID列表
     * @return 参数组列表
     */
    List<PmsParamGroup> selectByIds(@Param("ids") List<Long> ids);

    /**
     * 根据名称查询
     *
     * @param name 参数组名称
     * @return 参数组记录
     */
    PmsParamGroup selectByName(@Param("name") String name);

    /**
     * 根据状态查询
     *
     * @param status 状态：0-禁用 1-启用
     * @return 参数组列表
     */
    List<PmsParamGroup> selectByStatus(@Param("status") Integer status);

    /**
     * 查询所有
     *
     * @return 参数组列表
     */
    List<PmsParamGroup> selectAll();

    /**
     * 插入记录
     *
     * @param record 参数组记录
     * @return 影响行数
     */
    int insert(PmsParamGroup record);

    /**
     * 选择性插入记录
     *
     * @param record 参数组记录
     * @return 影响行数
     */
    int insertSelective(PmsParamGroup record);

    /**
     * 根据主键更新
     *
     * @param record 参数组记录
     * @return 影响行数
     */
    int updateByPrimaryKey(PmsParamGroup record);

    /**
     * 根据主键选择性更新
     *
     * @param record 参数组记录
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(PmsParamGroup record);

    /**
     * 批量更新状态
     *
     * @param ids ID列表
     * @param status 状态
     * @return 影响行数
     */
    int updateStatusBatch(@Param("ids") List<Long> ids, @Param("status") Integer status);

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