package com.mallease.product.dal.mapper;

import com.mallease.product.dal.entity.CategoryBrandRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 分类-品牌关联 DAO
 *
 * @author: Aulen
 * @create: 2026-01-12
 */
@Mapper
public interface CategoryBrandRelationDao {

    /**
     * 根据ID查询
     */
    CategoryBrandRelation selectById(@Param("id") Long id);

    /**
     * 根据分类ID查询
     */
    List<CategoryBrandRelation> selectByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * 根据品牌ID查询
     */
    List<CategoryBrandRelation> selectByBrandId(@Param("brandId") Long brandId);

    /**
     * 根据分类ID和品牌ID查询
     */
    CategoryBrandRelation selectByCategoryIdAndBrandId(@Param("categoryId") Long categoryId, @Param("brandId") Long brandId);

    /**
     * 查询分类已关联的品牌ID列表
     */
    List<Long> selectBrandIdsByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * 查询分类未关联的品牌ID列表
     */
    List<Long> selectUnbindBrandIds(@Param("categoryId") Long categoryId);

    /**
     * 插入
     */
    int insert(CategoryBrandRelation entity);

    /**
     * 批量插入
     */
    int insertBatch(@Param("list") List<CategoryBrandRelation> list);

    /**
     * 根据ID删除
     */
    int deleteById(@Param("id") Long id);

    /**
     * 根据分类ID和品牌ID删除
     */
    int deleteByCategoryIdAndBrandId(@Param("categoryId") Long categoryId, @Param("brandId") Long brandId);

    /**
     * 根据分类ID删除所有关联
     */
    int deleteByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * 根据品牌ID删除所有关联
     */
    int deleteByBrandId(@Param("brandId") Long brandId);

    /**
     * 批量删除指定分类下的指定品牌关联
     */
    int deleteByCategoryIdAndBrandIds(@Param("categoryId") Long categoryId, @Param("brandIds") List<Long> brandIds);
}
