package com.mallease.pms.dao;

import com.mallease.pms.pojo.PmsProductCategoryAttributeRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 产品的分类和属性的关系表 Mapper 接口
 *
 * @author: Aulen
 * @create: 2025-11-09
 */
@Mapper
public interface PmsProductCategoryAttributeRelationDao {
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
    int insert(PmsProductCategoryAttributeRelation record);

    /**
     * 选择性插入记录
     *
     * @param record 记录
     * @return 影响行数
     */
    int insertSelective(PmsProductCategoryAttributeRelation record);

    /**
     * 根据主键查询
     *
     * @param id 主键ID
     * @return 记录
     */
    PmsProductCategoryAttributeRelation selectByPrimaryKey(Long id);

    /**
     * 根据主键选择性更新
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(PmsProductCategoryAttributeRelation record);

    /**
     * 根据主键更新
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKey(PmsProductCategoryAttributeRelation record);

    /**
     * 根据产品分类ID查询
     *
     * @param productCategoryId 产品分类ID
     * @return 记录列表
     */
    List<PmsProductCategoryAttributeRelation> selectByProductCategoryId(@Param("productCategoryId") Long productCategoryId);

    /**
     * 根据产品属性ID查询
     *
     * @param productAttributeId 产品属性ID
     * @return 记录列表
     */
    List<PmsProductCategoryAttributeRelation> selectByProductAttributeId(@Param("productAttributeId") Long productAttributeId);

    /**
     * 根据产品分类ID删除
     *
     * @param productCategoryId 产品分类ID
     * @return 影响行数
     */
    int deleteByProductCategoryId(@Param("productCategoryId") Long productCategoryId);

    /**
     * 根据产品属性ID删除
     *
     * @param productAttributeId 产品属性ID
     * @return 影响行数
     */
    int deleteByProductAttributeId(@Param("productAttributeId") Long productAttributeId);
}

