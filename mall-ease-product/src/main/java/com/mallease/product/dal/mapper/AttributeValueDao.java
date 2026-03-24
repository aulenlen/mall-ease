package com.mallease.product.dal.mapper;

import com.mallease.product.dal.entity.AttributeValue;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品属性值 DAO
 *
 * @author: Aulen
 * @create: 2026-01-11
 */
@Mapper
public interface AttributeValueDao {

    /**
     * 根据ID查询
     */
    AttributeValue selectById(@Param("id") Long id);

    /**
     * 根据SPU ID查询所有属性值（参数 + SKU规格）
     */
    List<AttributeValue> selectBySpuId(@Param("spuId") Long spuId);

    /**
     * 根据SPU ID查询参数（sku_id IS NULL）
     */
    List<AttributeValue> selectParamsBySpuId(@Param("spuId") Long spuId);

    /**
     * 根据SPU ID列表批量查询参数（sku_id IS NULL）
     */
    List<AttributeValue> selectParamsBySpuIds(@Param("spuIds") List<Long> spuIds);

    /**
     * 根据SKU ID查询规格
     */
    List<AttributeValue> selectSpecsBySkuId(@Param("skuId") Long skuId);

    /**
     * 根据SKU ID列表批量查询规格
     */
    List<AttributeValue> selectSpecsBySkuIds(@Param("skuIds") List<Long> skuIds);

    /**
     * 根据属性ID查询
     */
    List<AttributeValue> selectByAttrId(@Param("attrId") Long attrId);

    /**
     * 插入
     */
    int insert(AttributeValue entity);

    /**
     * 批量插入
     */
    int insertBatch(@Param("list") List<AttributeValue> list);

    /**
     * 更新
     */
    int updateById(AttributeValue entity);

    /**
     * 逻辑删除
     */
    int deleteById(@Param("id") Long id);

    /**
     * 根据SPU ID删除参数
     */
    int deleteParamsBySpuId(@Param("spuId") Long spuId);

    /**
     * 根据SPU ID物理删除参数
     */
    int removeParamsBySpuId(@Param("spuId") Long spuId);

    /**
     * 根据SKU ID删除规格
     */
    int deleteSpecsBySkuId(@Param("skuId") Long skuId);

    /**
     * 根据SKU ID物理删除规格
     */
    int removeSpecsBySkuId(@Param("skuId") Long skuId);

    /**
     * 根据SKU ID列表批量删除规格
     */
    int deleteSpecsBySkuIds(@Param("skuIds") List<Long> skuIds);

    /**
     * 根据SKU ID列表批量物理删除规格
     */
    int removeSpecsBySkuIds(@Param("skuIds") List<Long> skuIds);

    /**
     * 根据SPU ID删除所有属性值
     */
    int deleteBySpuId(@Param("spuId") Long spuId);

    /**
     * 根据SPU ID物理删除所有属性值
     */
    int removeBySpuId(@Param("spuId") Long spuId);
}
