package com.mallease.pms.dao;

import com.mallease.pms.pojo.PmsSpuDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * SPU详情 Mapper 接口
 * 说明：与 pms_spu 为一对一关系，存储低频访问的大文本字段
 *
 * @author: Aulen
 * @create: 2025-12-11
 */
@Mapper
public interface PmsSpuDetailDao {

    /**
     * 根据主键查询
     *
     * @param id 主键ID
     * @return SPU详情记录
     */
    PmsSpuDetail selectByPrimaryKey(Long id);

    /**
     * 根据SPU ID查询（一对一关系）
     *
     * @param spuId SPU ID
     * @return SPU详情记录
     */
    PmsSpuDetail selectBySpuId(@Param("spuId") Long spuId);

    /**
     * 根据SPU ID列表批量查询
     *
     * @param spuIds SPU ID列表
     * @return SPU详情列表
     */
    List<PmsSpuDetail> selectBySpuIds(@Param("spuIds") List<Long> spuIds);

    /**
     * 插入记录
     *
     * @param record SPU详情记录
     * @return 影响行数
     */
    int insert(PmsSpuDetail record);

    /**
     * 选择性插入记录
     *
     * @param record SPU详情记录
     * @return 影响行数
     */
    int insertSelective(PmsSpuDetail record);

    /**
     * 根据主键更新
     *
     * @param record SPU详情记录
     * @return 影响行数
     */
    int updateByPrimaryKey(PmsSpuDetail record);

    /**
     * 根据主键选择性更新
     *
     * @param record SPU详情记录
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(PmsSpuDetail record);

    /**
     * 根据SPU ID更新
     *
     * @param record SPU详情记录（需包含spuId）
     * @return 影响行数
     */
    int updateBySpuId(PmsSpuDetail record);

    /**
     * 根据SPU ID删除
     *
     * @param spuId SPU ID
     * @return 影响行数
     */
    int deleteBySpuId(@Param("spuId") Long spuId);
}