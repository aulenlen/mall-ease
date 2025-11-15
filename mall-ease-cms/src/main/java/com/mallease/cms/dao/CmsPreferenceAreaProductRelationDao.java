package com.mallease.cms.dao;

import com.mallease.cms.pojo.CmsPreferenceAreaProductRelation;
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
public interface CmsPreferenceAreaProductRelationDao {
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
    int insert(CmsPreferenceAreaProductRelation record);

    /**
     * 选择性插入记录
     *
     * @param record 记录
     * @return 影响行数
     */
    int insertSelective(CmsPreferenceAreaProductRelation record);

    /**
     * 根据主键查询
     *
     * @param id 主键ID
     * @return 记录
     */
    CmsPreferenceAreaProductRelation selectByPrimaryKey(Long id);

    /**
     * 根据主键选择性更新
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(CmsPreferenceAreaProductRelation record);

    /**
     * 根据主键更新
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKey(CmsPreferenceAreaProductRelation record);

    /**
     * 根据优选专区ID查询
     *
     * @param prefrenceAreaId 优选专区ID
     * @return 记录列表
     */
    List<CmsPreferenceAreaProductRelation> selectByPreferenceAreaId(@Param("prefrenceAreaId") Long prefrenceAreaId);

    /**
     * 根据产品ID查询
     *
     * @param productId 产品ID
     * @return 记录列表
     */
    List<CmsPreferenceAreaProductRelation> selectByProductId(@Param("productId") Long productId);

    /**
     * 根据优选专区ID删除
     *
     * @param prefrenceAreaId 优选专区ID
     * @return 影响行数
     */
    int deleteByPreferenceAreaId(@Param("prefrenceAreaId") Long prefrenceAreaId);

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
    int insertBatch(@Param("list") List<CmsPreferenceAreaProductRelation> list);
}