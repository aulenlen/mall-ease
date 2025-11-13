package com.mallease.cms.dao;

import com.mallease.cms.pojo.CmsPrefrenceAreaProductRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 优选专区和产品关系表 Mapper 接口
 *
 * @author: Aulen
 * @create: 2025-11-13
 */
@Mapper
public interface CmsPrefrenceAreaProductRelationDao {
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
    int insert(CmsPrefrenceAreaProductRelation record);

    /**
     * 选择性插入记录
     *
     * @param record 记录
     * @return 影响行数
     */
    int insertSelective(CmsPrefrenceAreaProductRelation record);

    /**
     * 根据主键查询
     *
     * @param id 主键ID
     * @return 记录
     */
    CmsPrefrenceAreaProductRelation selectByPrimaryKey(Long id);

    /**
     * 根据主键选择性更新
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(CmsPrefrenceAreaProductRelation record);

    /**
     * 根据主键更新
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKey(CmsPrefrenceAreaProductRelation record);

    /**
     * 根据优选专区ID查询
     *
     * @param prefrenceAreaId 优选专区ID
     * @return 记录列表
     */
    List<CmsPrefrenceAreaProductRelation> selectByPrefrenceAreaId(@Param("prefrenceAreaId") Long prefrenceAreaId);

    /**
     * 根据产品ID查询
     *
     * @param productId 产品ID
     * @return 记录列表
     */
    List<CmsPrefrenceAreaProductRelation> selectByProductId(@Param("productId") Long productId);

    /**
     * 根据优选专区ID删除
     *
     * @param prefrenceAreaId 优选专区ID
     * @return 影响行数
     */
    int deleteByPrefrenceAreaId(@Param("prefrenceAreaId") Long prefrenceAreaId);

    /**
     * 根据产品ID删除
     *
     * @param productId 产品ID
     * @return 影响行数
     */
    int deleteByProductId(@Param("productId") Long productId);
}