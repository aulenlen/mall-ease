package com.mallease.pms.dao;

import com.mallease.pms.pojo.PmsCategoryParamGroup;
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
public interface PmsCategoryParamGroupDao {

    /**
     * 根据主键查询
     *
     * @param id 主键ID
     * @return 关联记录
     */
    PmsCategoryParamGroup selectByPrimaryKey(Long id);

    /**
     * 根据分类ID查询
     *
     * @param categoryId 分类ID
     * @return 关联列表
     */
    List<PmsCategoryParamGroup> selectByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * 根据分类ID列表批量查询
     *
     * @param categoryIds 分类ID列表
     * @return 关联列表
     */
    List<PmsCategoryParamGroup> selectByCategoryIds(@Param("categoryIds") List<Long> categoryIds);

    /**
     * 根据参数组ID查询
     *
     * @param paramGroupId 参数组ID
     * @return 关联列表
     */
    List<PmsCategoryParamGroup> selectByParamGroupId(@Param("paramGroupId") Long paramGroupId);

    /**
     * 检查关联是否存在
     *
     * @param categoryId 分类ID
     * @param paramGroupId 参数组ID
     * @return 关联记录（存在则返回，否则返回null）
     */
    PmsCategoryParamGroup selectByCategoryIdAndParamGroupId(@Param("categoryId") Long categoryId,
                                                            @Param("paramGroupId") Long paramGroupId);

    /**
     * 插入记录
     *
     * @param record 关联记录
     * @return 影响行数
     */
    int insert(PmsCategoryParamGroup record);

    /**
     * 批量插入
     *
     * @param list 关联列表
     * @return 影响行数
     */
    int insertBatch(@Param("list") List<PmsCategoryParamGroup> list);

    /**
     * 根据主键删除
     *
     * @param id 主键ID
     * @return 影响行数
     */
    int deleteByPrimaryKey(@Param("id") Long id);

    /**
     * 根据分类ID删除
     *
     * @param categoryId 分类ID
     * @return 影响行数
     */
    int deleteByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * 根据参数组ID删除
     *
     * @param paramGroupId 参数组ID
     * @return 影响行数
     */
    int deleteByParamGroupId(@Param("paramGroupId") Long paramGroupId);
}