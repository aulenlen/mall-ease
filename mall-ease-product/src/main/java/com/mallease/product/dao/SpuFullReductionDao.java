package com.mallease.product.dao;

import com.mallease.product.model.data.entity.SpuFullReduction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * SPU满减 Mapper 接口
 * 说明：存储 SPU 级别的满减规则（满足金额减免金额）
 *
 * @author: Aulen
 * @create: 2025-12-11
 */
@Mapper
public interface SpuFullReductionDao {

    /**
     * 根据主键查询
     *
     * @param id 主键ID
     * @return 满减记录
     */
    SpuFullReduction selectByPrimaryKey(Long id);

    /**
     * 根据SPU ID查询所有满减规则
     *
     * @param spuId SPU ID
     * @return 满减列表（按满足金额升序排列）
     */
    List<SpuFullReduction> selectBySpuId(@Param("spuId") Long spuId);

    /**
     * 根据SPU ID列表批量查询
     *
     * @param spuIds SPU ID列表
     * @return 满减列表
     */
    List<SpuFullReduction> selectBySpuIds(@Param("spuIds") List<Long> spuIds);

    /**
     * 查询满足金额的最优满减规则
     *
     * @param spuId SPU ID
     * @param price 订单金额
     * @return 满减记录（返回满足条件的最大档位）
     */
    SpuFullReduction selectBestReduction(@Param("spuId") Long spuId, @Param("price") BigDecimal price);

    /**
     * 插入记录
     *
     * @param record 满减记录
     * @return 影响行数
     */
    int insert(SpuFullReduction record);

    /**
     * 选择性插入记录
     *
     * @param record 满减记录
     * @return 影响行数
     */
    int insertSelective(SpuFullReduction record);

    /**
     * 批量插入
     *
     * @param list 满减列表
     * @return 影响行数
     */
    int insertBatch(@Param("list") List<SpuFullReduction> list);

    /**
     * 根据主键更新
     *
     * @param record 满减记录
     * @return 影响行数
     */
    int updateByPrimaryKey(SpuFullReduction record);

    /**
     * 根据主键选择性更新
     *
     * @param record 满减记录
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(SpuFullReduction record);

    /**
     * 根据SPU ID删除
     *
     * @param spuId SPU ID
     * @return 影响行数
     */
    int deleteBySpuId(@Param("spuId") Long spuId);

    /**
     * 批量删除
     *
     * @param ids ID列表
     * @return 影响行数
     */
    int deleteBatch(@Param("ids") List<Long> ids);
}