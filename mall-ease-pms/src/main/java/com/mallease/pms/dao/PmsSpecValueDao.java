package com.mallease.pms.dao;

import com.mallease.pms.pojo.PmsSpecValue;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 规格值 Mapper 接口
 * 说明：存储规格的具体可选值，如"黑色"、"128GB"、"XL"
 *
 * @author: Aulen
 * @create: 2025-12-11
 */
@Mapper
public interface PmsSpecValueDao {

    /**
     * 根据主键查询
     *
     * @param id 主键ID
     * @return 规格值记录
     */
    PmsSpecValue selectByPrimaryKey(Long id);

    /**
     * 根据ID列表批量查询
     *
     * @param ids ID列表
     * @return 规格值列表
     */
    List<PmsSpecValue> selectByIds(@Param("ids") List<Long> ids);

    /**
     * 根据规格ID查询
     *
     * @param specId 规格ID
     * @return 规格值列表
     */
    List<PmsSpecValue> selectBySpecId(@Param("specId") Long specId);

    /**
     * 根据规格ID列表批量查询
     *
     * @param specIds 规格ID列表
     * @return 规格值列表
     */
    List<PmsSpecValue> selectBySpecIds(@Param("specIds") List<Long> specIds);

    /**
     * 根据规格ID和值查询
     *
     * @param specId 规格ID
     * @param value 规格值
     * @return 规格值记录
     */
    PmsSpecValue selectBySpecIdAndValue(@Param("specId") Long specId, @Param("value") String value);

    /**
     * 插入记录
     *
     * @param record 规格值记录
     * @return 影响行数
     */
    int insert(PmsSpecValue record);

    /**
     * 选择性插入记录
     *
     * @param record 规格值记录
     * @return 影响行数
     */
    int insertSelective(PmsSpecValue record);

    /**
     * 批量插入
     *
     * @param list 规格值列表
     * @return 影响行数
     */
    int insertBatch(@Param("list") List<PmsSpecValue> list);

    /**
     * 根据主键更新
     *
     * @param record 规格值记录
     * @return 影响行数
     */
    int updateByPrimaryKey(PmsSpecValue record);

    /**
     * 根据主键选择性更新
     *
     * @param record 规格值记录
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(PmsSpecValue record);

    /**
     * 逻辑删除
     *
     * @param id 主键ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);

    /**
     * 根据规格ID逻辑删除
     *
     * @param specId 规格ID
     * @return 影响行数
     */
    int deleteBySpecId(@Param("specId") Long specId);

    /**
     * 批量逻辑删除
     *
     * @param ids ID列表
     * @return 影响行数
     */
    int deleteBatch(@Param("ids") List<Long> ids);

    /**
     * 根据规格ID列表批量逻辑删除
     *
     * @param specIds 规格ID列表
     * @return 影响行数
     */
    int deleteBySpecIds(@Param("specIds") List<Long> specIds);
}