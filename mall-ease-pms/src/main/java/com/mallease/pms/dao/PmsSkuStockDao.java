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

    // ========================================================================
    // 遗留方法（基于 product_id）- 待迁移后删除
    // 这些方法是为了兼容旧代码，新代码请使用基于 sku_id 的方法
    // TODO: 完成 SKU 重构后删除以下方法
    // ========================================================================

    /**
     * 根据主键删除
     *
     * @param id 主键ID
     * @return 影响行数
     * @deprecated 使用 {@link #deleteBySkuId(Long)} 替代
     */
    @Deprecated
    int deleteByPrimaryKey(Long id);

    /**
     * 根据产品ID查询
     *
     * @param productId 产品ID
     * @return 记录列表
     * @deprecated 新架构中 SKU 与 SPU 通过 pms_sku.spu_id 关联，库存通过 sku_id 查询
     */
    @Deprecated
    List<PmsSkuStock> selectByProductId(@Param("productId") Long productId);

    /**
     * 根据SKU编码查询
     *
     * @param skuCode SKU编码
     * @return 记录
     * @deprecated 新架构中 sku_code 在 pms_sku 表，需先查 sku_id 再查库存
     */
    @Deprecated
    PmsSkuStock selectBySkuCode(@Param("skuCode") String skuCode);

    /**
     * 根据产品ID删除
     *
     * @param productId 产品ID
     * @return 影响行数
     * @deprecated 新架构中需通过 sku_id 删除
     */
    @Deprecated
    int deleteByProductId(@Param("productId") Long productId);

    /**
     * 根据产品ID和关键字模糊查询SKU库存
     *
     * @param productId 产品ID
     * @param keyword   关键字 (可选，用于模糊匹配 sku_code)
     * @return 记录列表
     * @deprecated 新架构中 sku_code 在 pms_sku 表
     */
    @Deprecated
    List<PmsSkuStock> selectByProductIdAndKeyword(@Param("productId") Long productId, @Param("keyword") String keyword);

    /**
     * 批量更新SKU库存信息（选择性更新）
     *
     * @param list 记录列表
     * @return 影响行数
     * @deprecated 使用 {@link #updateByPrimaryKeySelective(PmsSkuStock)} 循环或新的批量方法
     */
    @Deprecated
    int updateBatchSelective(@Param("list") List<PmsSkuStock> list);

    /**
     * 根据商品ID列表获取库存信息表
     *
     * @param ids 商品ID列表
     * @return 库存信息表
     * @deprecated 使用 {@link #selectBySkuIds(List)} 替代
     */
    @Deprecated
    List<PmsSkuStock> selectByProductIds(@Param("ids") List<Long> ids);

    // ========================================================================
    // 新方法（基于 sku_id）- 推荐使用
    // ========================================================================

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