package com.mallease.cms.dao;

import com.mallease.cms.pojo.CmsSubjectProductRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 专题商品关系表 Mapper 接口
 *
 * @author: Aulen
 * @create: 2025-11-13
 */
@Mapper
public interface CmsSubjectProductRelationDao {
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
    int insert(CmsSubjectProductRelation record);

    /**
     * 选择性插入记录
     *
     * @param record 记录
     * @return 影响行数
     */
    int insertSelective(CmsSubjectProductRelation record);

    /**
     * 根据主键查询
     *
     * @param id 主键ID
     * @return 记录
     */
    CmsSubjectProductRelation selectByPrimaryKey(Long id);

    /**
     * 根据主键选择性更新
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(CmsSubjectProductRelation record);

    /**
     * 根据主键更新
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKey(CmsSubjectProductRelation record);

    /**
     * 根据专题ID查询
     *
     * @param subjectId 专题ID
     * @return 记录列表
     */
    List<CmsSubjectProductRelation> selectBySubjectId(@Param("subjectId") Long subjectId);

    /**
     * 根据产品ID查询
     *
     * @param productId 产品ID
     * @return 记录列表
     */
    List<CmsSubjectProductRelation> selectByProductId(@Param("productId") Long productId);

    /**
     * 根据专题ID删除
     *
     * @param subjectId 专题ID
     * @return 影响行数
     */
    int deleteBySubjectId(@Param("subjectId") Long subjectId);

    /**
     * 根据产品ID删除
     *
     * @param productId 产品ID
     * @return 影响行数
     */
    int deleteByProductId(@Param("productId") Long productId);

    /**
     * 批量插入记录
     *
     * @param list 记录列表
     * @return 影响行数
     */
    int insertBatch(@Param("list") List<CmsSubjectProductRelation> list);
}