package com.mallease.product.dao;

import com.mallease.product.model.data.entity.CategoryParamGroup;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 分类-参数组关联 Mapper 接口
 * 说明：建立分类与参数组的多对多关系
 *
 * @author: Aulen
 * @create: 2025-12-11
 */
@Mapper
public interface CategoryParamGroupDao {

    /**
     * 根据分类ID查询
     *
     * @param categoryId 分类ID
     * @return 关联列表
     */
    List<CategoryParamGroup> selectByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * 根据参数组ID查询
     *
     * @param paramGroupId 参数组ID
     * @return 关联列表
     */
    List<CategoryParamGroup> selectByParamGroupId(@Param("paramGroupId") Long paramGroupId);

    /**
     * 检查关联是否存在
     *
     * @param categoryId 分类ID
     * @param paramGroupId 参数组ID
     * @return 关联记录（存在则返回，否则返回null）
     */
    CategoryParamGroup selectByCategoryIdAndParamGroupId(@Param("categoryId") Long categoryId,
                                                            @Param("paramGroupId") Long paramGroupId);

    /**
     * 插入记录
     *
     * @param record 关联记录
     * @return 影响行数
     */
    int insert(CategoryParamGroup record);

    /**
     * 批量插入
     *
     * @param list 关联列表
     * @return 影响行数
     */
    int insertBatch(@Param("list") List<CategoryParamGroup> list);

    /**
     * 根据参数组ID删除
     *
     * @param paramGroupId 参数组ID
     * @return 影响行数
     */
    int deleteByParamGroupId(@Param("paramGroupId") Long paramGroupId);

    /**
     * 根据分类ID和参数组ID列表批量删除
     *
     * @param categoryId 分类ID
     * @param paramGroupIds 参数组ID列表
     * @return 影响行数
     */
    int deleteByCategoryIdAndParamGroupIds(@Param("categoryId") Long categoryId,
                                           @Param("paramGroupIds") List<Long> paramGroupIds);

    /**
     * 根据参数组ID列表批量删除
     *
     * @param paramGroupIds 参数组ID列表
     * @return 影响行数
     */
    int deleteByParamGroupIds(@Param("paramGroupIds") List<Long> paramGroupIds);
}
