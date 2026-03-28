package com.mallease.content.dal.mapper;

import com.mallease.content.dal.entity.EditorialSpuRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 编辑精选商品关系表 Mapper 接口
 *
 * @author: Aulen
 * @create: 2026-01-07
 */
@Mapper
public interface EditorialSpuRelationDao {
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
    int insert(EditorialSpuRelation record);

    /**
     * 选择性插入记录
     *
     * @param record 记录
     * @return 影响行数
     */
    int insertSelective(EditorialSpuRelation record);

    /**
     * 根据主键查询
     *
     * @param id 主键ID
     * @return 记录
     */
    EditorialSpuRelation selectByPrimaryKey(Long id);

    /**
     * 根据主键选择性更新
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(EditorialSpuRelation record);

    /**
     * 根据主键更新
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKey(EditorialSpuRelation record);

    /**
     * 根据文章ID查询
     *
     * @param editorialId 文章ID
     * @return 记录列表
     */
    List<EditorialSpuRelation> selectByEditorialId(@Param("editorialId") Long editorialId);

    /**
     * 根据多个文章ID批量查询
     *
     * @param editorialIds 文章ID列表
     * @return 记录列表
     */
    List<EditorialSpuRelation> selectByEditorialIds(@Param("editorialIds") List<Long> editorialIds);

    /**
     * 根据产品ID查询
     *
     * @param spuId 产品ID
     * @return 记录列表
     */
    List<EditorialSpuRelation> selectBySpuId(@Param("spuId") Long spuId);

    /**
     * 根据文章ID删除
     *
     * @param editorialId 文章ID
     * @return 影响行数
     */
    int deleteByEditorialId(@Param("editorialId") Long editorialId);

    /**
     * 根据产品ID删除
     *
     * @param spuId 产品ID
     * @return 影响行数
     */
    int deleteBySpuId(@Param("spuId") Long spuId);

    /**
     * 批量插入记录
     *
     * @param list 记录列表
     * @return 影响行数
     */
    int insertBatch(@Param("list") List<EditorialSpuRelation> list);

    /**
     * 根据文章ID和商品ID列表删除
     *
     * @param editorialId 文章ID
     * @param spuIds      商品ID列表
     * @return 影响行数
     */
    int deleteByEditorialIdAndSpuIds(@Param("editorialId") Long editorialId, @Param("spuIds") List<Long> spuIds);
}
