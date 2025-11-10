package com.mallease.pms.dao;

import com.mallease.pms.pojo.PmsProductAttributeCategory;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 产品属性分类表 Mapper 接口
 *
 * @author: Aulen
 * @create: 2025-11-09
 */
@Mapper
public interface PmsProductAttributeCategoryDao {
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
    int insert(PmsProductAttributeCategory record);

    /**
     * 选择性插入记录
     *
     * @param record 记录
     * @return 影响行数
     */
    int insertSelective(PmsProductAttributeCategory record);

    /**
     * 根据主键查询
     *
     * @param id 主键ID
     * @return 记录
     */
    PmsProductAttributeCategory selectByPrimaryKey(Long id);

    /**
     * 根据主键选择性更新
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(PmsProductAttributeCategory record);

    /**
     * 根据主键更新
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKey(PmsProductAttributeCategory record);

    /**
     * 查询所有记录
     *
     * @return 记录列表
     */
    List<PmsProductAttributeCategory> selectAll();
}

