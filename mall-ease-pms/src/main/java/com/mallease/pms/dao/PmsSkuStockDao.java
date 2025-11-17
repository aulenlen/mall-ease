package com.mallease.pms.dao;

import com.mallease.pms.pojo.PmsSkuStock;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * sku的库存 Mapper 接口
 *
 * @author: Aulen
 * @create: 2025-11-09
 */
@Mapper
public interface PmsSkuStockDao {
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
    int insert(PmsSkuStock record);

    /**
     * 选择性插入记录
     *
     * @param record 记录
     * @return 影响行数
     */
    int insertSelective(PmsSkuStock record);

    /**
     * 根据主键查询
     *
     * @param id 主键ID
     * @return 记录
     */
    PmsSkuStock selectByPrimaryKey(Long id);

    /**
     * 根据主键选择性更新
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(PmsSkuStock record);

    /**
     * 根据主键更新
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKey(PmsSkuStock record);

    /**
     * 根据产品ID查询
     *
     * @param productId 产品ID
     * @return 记录列表
     */
    List<PmsSkuStock> selectByProductId(@Param("productId") Long productId);

    /**
     * 根据SKU编码查询
     *
     * @param skuCode SKU编码
     * @return 记录
     */
    PmsSkuStock selectBySkuCode(@Param("skuCode") String skuCode);

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
    int insertBatch(@Param("list") List<PmsSkuStock> list);

    /**
     * 根据产品ID和关键字模糊查询SKU库存
     *
     * @param productId 产品ID
     * @param keyword 关键字 (可选，用于模糊匹配 sku_code)
     * @return 记录列表
     */
    List<PmsSkuStock> selectByProductIdAndKeyword(@Param("productId") Long productId, @Param("keyword") String keyword);

    /**
     * 批量更新SKU库存信息（选择性更新）
     *
     * @param list 记录列表
     * @return 影响行数
     */
    int updateBatchSelective(@Param("list") List<PmsSkuStock> list);

    /**
     * 根据商品ID列表获取库存信息表
     * @param ids 商品ID列表
     * @return  库存信息表
     */
    List<PmsSkuStock> selectByProductIds(@Param("ids") List<Long> ids);
}

