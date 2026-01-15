package com.mallease.product.dao;

import com.mallease.product.model.data.entity.CategoryAttributeRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 分类-属性关联 DAO
 *
 * @author: Aulen
 * @create: 2026-01-12
 */
@Mapper
public interface CategoryAttributeRelationDao {

    /**
     * 根据ID查询
     */
    CategoryAttributeRelation selectById(@Param("id") Long id);

    /**
     * 根据分类ID查询
     */
    List<CategoryAttributeRelation> selectByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * 根据分类ID和属性类型查询（联表查询）
     */
    List<CategoryAttributeRelation> selectByCategoryIdAndType(@Param("categoryId") Long categoryId, @Param("type") Integer type);

    /**
     * 根据属性ID查询
     */
    List<CategoryAttributeRelation> selectByAttrId(@Param("attrId") Long attrId);

    /**
     * 根据分类ID和属性ID查询
     */
    CategoryAttributeRelation selectByCategoryIdAndAttrId(@Param("categoryId") Long categoryId, @Param("attrId") Long attrId);

    /**
     * 查询分类未关联的属性ID列表
     */
    List<Long> selectUnbindAttrIds(@Param("categoryId") Long categoryId);

    /**
     * 插入
     */
    int insert(CategoryAttributeRelation entity);

    /**
     * 批量插入
     */
    int insertBatch(@Param("list") List<CategoryAttributeRelation> list);

    /**
     * 更新
     */
    int updateById(CategoryAttributeRelation entity);

    /**
     * 根据ID删除
     */
    int deleteById(@Param("id") Long id);

    /**
     * 根据分类ID和属性ID删除
     */
    int deleteByCategoryIdAndAttrId(@Param("categoryId") Long categoryId, @Param("attrId") Long attrId);

    /**
     * 根据分类ID删除所有关联
     */
    int deleteByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * 根据属性ID删除所有关联
     */
    int deleteByAttrId(@Param("attrId") Long attrId);

    /**
     * 根据属性ID列表批量删除所有关联
     */
    int deleteByAttrIds(@Param("attrIds") List<Long> attrIds);

    /**
     * 批量删除指定分类下的指定属性关联
     */
    int deleteByCategoryIdAndAttrIds(@Param("categoryId") Long categoryId, @Param("attrIds") List<Long> attrIds);

    /**
     * 批量更新关联信息
     */
    int updateBatch(@Param("list") List<CategoryAttributeRelation> list);
}
