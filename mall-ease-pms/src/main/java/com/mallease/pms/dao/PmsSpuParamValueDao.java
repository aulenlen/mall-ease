package com.mallease.pms.dao;

import com.mallease.pms.pojo.PmsSpuParamValue;
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
public interface PmsSpuParamValueDao {

    /**
     * 根据主键查询
     *
     * @param id 主键ID
     * @return 参数值记录
     */
    PmsSpuParamValue selectByPrimaryKey(Long id);

    /**
     * 根据SPU ID查询所有参数值
     *
     * @param spuId SPU ID
     * @return 参数值列表
     */
    List<PmsSpuParamValue> selectBySpuId(@Param("spuId") Long spuId);

    /**
     * 根据SPU ID列表批量查询
     *
     * @param spuIds SPU ID列表
     * @return 参数值列表
     */
    List<PmsSpuParamValue> selectBySpuIds(@Param("spuIds") List<Long> spuIds);

    /**
     * 根据参数ID查询
     *
     * @param paramId 参数ID
     * @return 参数值列表
     */
    List<PmsSpuParamValue> selectByParamId(@Param("paramId") Long paramId);

    /**
     * 插入记录
     *
     * @param record 参数值记录
     * @return 影响行数
     */
    int insert(PmsSpuParamValue record);

    /**
     * 选择性插入记录
     *
     * @param record 参数值记录
     * @return 影响行数
     */
    int insertSelective(PmsSpuParamValue record);

    /**
     * 批量插入
     *
     * @param list 参数值列表
     * @return 影响行数
     */
    int insertBatch(@Param("list") List<PmsSpuParamValue> list);

    /**
     * 根据主键更新
     *
     * @param record 参数值记录
     * @return 影响行数
     */
    int updateByPrimaryKey(PmsSpuParamValue record);

    /**
     * 根据主键选择性更新
     *
     * @param record 参数值记录
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(PmsSpuParamValue record);

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