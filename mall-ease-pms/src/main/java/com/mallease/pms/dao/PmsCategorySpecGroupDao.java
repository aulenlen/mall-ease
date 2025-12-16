package com.mallease.pms.dao;

import com.mallease.pms.pojo.PmsCategorySpecGroup;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 分类-规格组关联 Mapper 接口
 * 说明：建立分类与规格组的多对多关系
 *
 * @author: Aulen
 * @create: 2025-12-11
 */
@Mapper
public interface PmsCategorySpecGroupDao {

    /**
     * 根据分类ID查询
     *
     * @param categoryId 分类ID
     * @return 关联列表
     */
    List<PmsCategorySpecGroup> selectByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * 根据规格组ID查询
     *
     * @param specGroupId 规格组ID
     * @return 关联列表
     */
    List<PmsCategorySpecGroup> selectBySpecGroupId(@Param("specGroupId") Long specGroupId);

    /**
     * 检查关联是否存在
     *
     * @param categoryId 分类ID
     * @param specGroupId 规格组ID
     * @return 关联记录（存在则返回，否则返回null）
     */
    PmsCategorySpecGroup selectByCategoryIdAndSpecGroupId(@Param("categoryId") Long categoryId,
                                                          @Param("specGroupId") Long specGroupId);

    /**
     * 插入记录
     *
     * @param record 关联记录
     * @return 影响行数
     */
    int insert(PmsCategorySpecGroup record);

    /**
     * 批量插入
     *
     * @param list 关联列表
     * @return 影响行数
     */
    int insertBatch(@Param("list") List<PmsCategorySpecGroup> list);

    /**
     * 根据主键删除
     *
     * @param id 主键ID
     * @return 影响行数
     */
    int deleteByPrimaryKey(@Param("id") Long id);

    /**
     * 根据规格组ID删除
     *
     * @param specGroupId 规格组ID
     * @return 影响行数
     */
    int deleteBySpecGroupId(@Param("specGroupId") Long specGroupId);

    /**
     * 根据规格组ID列表批量删除
     *
     * @param specGroupIds 规格组ID列表
     * @return 影响行数
     */
    int deleteBySpecGroupIds(@Param("specGroupIds") List<Long> specGroupIds);

    /**
     * 根据分类ID和规格组ID列表批量删除
     *
     * @param categoryId 分类ID
     * @param specGroupIds 规格组ID列表
     * @return 影响行数
     */
    int deleteByCategoryIdAndSpecGroupIds(@Param("categoryId") Long categoryId,
                                          @Param("specGroupIds") List<Long> specGroupIds);
}