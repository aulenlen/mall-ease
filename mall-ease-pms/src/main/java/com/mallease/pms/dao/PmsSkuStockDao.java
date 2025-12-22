package com.mallease.pms.dao;

import com.mallease.pms.pojo.PmsSkuStock;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * SKU库存 Mapper 接口
 * 重构说明：新表结构关联 sku_id 而非 product_id，支持乐观锁
 *
 * @author: Aulen
 * @create: 2025-12-11
 */
@Mapper
public interface PmsSkuStockDao {

    /**
     * 根据主键查询
     *
     * @param id 主键ID
     * @return 库存记录
     */
    PmsSkuStock selectByPrimaryKey(Long id);

    /**
     * 根据SKU ID查询库存（一对一关系）
     *
     * @param skuId SKU ID
     * @return 库存记录
     */
    PmsSkuStock selectBySkuId(@Param("skuId") Long skuId);

    /**
     * 根据SKU ID列表批量查询库存
     *
     * @param skuIds SKU ID列表
     * @return 库存列表
     */
    List<PmsSkuStock> selectBySkuIds(@Param("skuIds") List<Long> skuIds);

    /**
     * 根据SPU ID查询库存列表
     *
     * @param spuId SPU ID
     * @return 库存列表
     */
    List<PmsSkuStock> selectBySpuId(@Param("spuId") Long spuId);

    /**
     * 根据SPU ID列表批量查询库存
     *
     * @param spuIds SPU ID列表
     * @return 库存列表
     */
    List<PmsSkuStock> selectBySpuIds(@Param("spuIds") List<Long> spuIds);

    /**
     * 根据库存状态查询
     *
     * @param stockStatus 库存状态：0-无货 1-有货 2-预售
     * @return 库存列表
     */
    List<PmsSkuStock> selectByStockStatus(@Param("stockStatus") Integer stockStatus);

    /**
     * 查询库存预警（库存低于预警值）
     *
     * @return 库存列表
     */
    List<PmsSkuStock> selectLowStockWarning();

    /**
     * 插入记录
     *
     * @param record 库存记录
     * @return 影响行数
     */
    int insert(PmsSkuStock record);

    /**
     * 选择性插入记录
     *
     * @param record 库存记录
     * @return 影响行数
     */
    int insertSelective(PmsSkuStock record);

    /**
     * 批量插入
     *
     * @param list 库存列表
     * @return 影响行数
     */
    int insertBatch(@Param("list") List<PmsSkuStock> list);

    /**
     * 根据主键更新（全字段）
     *
     * @param record 库存记录
     * @return 影响行数
     */
    int updateByPrimaryKey(PmsSkuStock record);

    /**
     * 根据主键选择性更新
     *
     * @param record 库存记录
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(PmsSkuStock record);

    /**
     * 扣减库存（乐观锁）
     * 库存扣减核心方法，使用乐观锁防止超卖
     *
     * @param skuId SKU ID
     * @param quantity 扣减数量
     * @param version 当前版本号
     * @return 影响行数（0表示扣减失败，需要重试）
     */
    int decreaseStock(@Param("skuId") Long skuId,
                      @Param("quantity") Integer quantity,
                      @Param("version") Integer version);

    /**
     * 增加库存
     *
     * @param skuId SKU ID
     * @param quantity 增加数量
     * @return 影响行数
     */
    int increaseStock(@Param("skuId") Long skuId, @Param("quantity") Integer quantity);

    /**
     * 锁定库存（下单未支付）
     *
     * @param skuId SKU ID
     * @param quantity 锁定数量
     * @param version 当前版本号
     * @return 影响行数
     */
    int lockStock(@Param("skuId") Long skuId,
                  @Param("quantity") Integer quantity,
                  @Param("version") Integer version);

    /**
     * 解锁库存（支付超时或取消订单）
     *
     * @param skuId SKU ID
     * @param quantity 解锁数量
     * @return 影响行数
     */
    int unlockStock(@Param("skuId") Long skuId, @Param("quantity") Integer quantity);

    /**
     * 确认扣减库存（支付成功后，锁定库存转为实际扣减）
     *
     * @param skuId SKU ID
     * @param quantity 确认数量
     * @return 影响行数
     */
    int confirmDecrease(@Param("skuId") Long skuId, @Param("quantity") Integer quantity);

    /**
     * 增加销量
     *
     * @param skuId SKU ID
     * @param quantity 销量增加数量
     * @return 影响行数
     */
    int increaseSale(@Param("skuId") Long skuId, @Param("quantity") Integer quantity);

    /**
     * 批量更新库存状态
     *
     * @param skuIds SKU ID列表
     * @param stockStatus 库存状态
     * @return 影响行数
     */
    int updateStockStatusBatch(@Param("skuIds") List<Long> skuIds, @Param("stockStatus") Integer stockStatus);

    /**
     * 根据SKU ID删除库存
     *
     * @param skuId SKU ID
     * @return 影响行数
     */
    int deleteBySkuId(@Param("skuId") Long skuId);

    /**
     * 查询所有记录
     *
     * @return 库存列表
     */
    List<PmsSkuStock> selectAll();
}