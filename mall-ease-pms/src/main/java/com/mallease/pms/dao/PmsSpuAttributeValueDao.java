package com.mallease.pms.dao;

import com.mallease.pms.pojo.PmsSpuAttributeValue;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * SPU参数值 Mapper 接口
 * 说明：存储 SPU 级别的参数属性值（非规格属性）
 *
 * @author: Aulen
 * @create: 2025-12-11
 */
@Mapper
public interface PmsSpuAttributeValueDao {

    /**
     * 根据主键查询
     *
     * @param id 主键ID
     * @return 参数值记录
     */
    PmsSpuAttributeValue selectByPrimaryKey(Long id);

    /**
     * 根据SPU ID查询所有参数值
     *
     * @param spuId SPU ID
     * @return 参数值列表
     */
    List<PmsSpuAttributeValue> selectBySpuId(@Param("spuId") Long spuId);

    /**
     * 根据SPU ID列表批量查询
     *
     * @param spuIds SPU ID列表
     * @return 参数值列表
     */
    List<PmsSpuAttributeValue> selectBySpuIds(@Param("spuIds") List<Long> spuIds);

    /**
     * 根据属性ID查询
     *
     * @param productAttributeId 属性ID
     * @return 参数值列表
     */
    List<PmsSpuAttributeValue> selectByAttributeId(@Param("productAttributeId") Long productAttributeId);

    /**
     * 插入记录
     *
     * @param record 参数值记录
     * @return 影响行数
     */
    int insert(PmsSpuAttributeValue record);

    /**
     * 选择性插入记录
     *
     * @param record 参数值记录
     * @return 影响行数
     */
    int insertSelective(PmsSpuAttributeValue record);

    /**
     * 批量插入
     *
     * @param list 参数值列表
     * @return 影响行数
     */
    int insertBatch(@Param("list") List<PmsSpuAttributeValue> list);

    /**
     * 根据主键更新
     *
     * @param record 参数值记录
     * @return 影响行数
     */
    int updateByPrimaryKey(PmsSpuAttributeValue record);

    /**
     * 根据主键选择性更新
     *
     * @param record 参数值记录
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(PmsSpuAttributeValue record);

    /**
     * 根据SPU ID逻辑删除
     *
     * @param spuId SPU ID
     * @return 影响行数
     */
    int deleteBySpuId(@Param("spuId") Long spuId);

    /**
     * 批量逻辑删除
     *
     * @param ids 主键ID列表
     * @return 影响行数
     */
    int deleteBatch(@Param("ids") List<Long> ids);
}