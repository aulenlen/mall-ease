package com.mallease.trade.dao;

import com.mallease.trade.model.aggregate.CartCheckedSummary;
import com.mallease.trade.model.data.entity.CartItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 购物车项 Mapper 接口
 *
 * @author: Aulen
 * @create: 2026-01-27
 */
@Mapper
public interface CartItemDao {

    /**
     * 根据主键查询购物车项
     */
    CartItem selectByPrimaryKey(Long id);

    /**
     * 根据主键列表批量查询购物车项
     */
    List<CartItem> selectByIds(@Param("ids") List<Long> ids);

    /**
     * 根据用户ID查询购物车列表
     */
    List<CartItem> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID列表批量查询购物车项
     */
    List<CartItem> selectByUserIds(@Param("userIds") List<Long> userIds);

    /**
     * 查询已选中商品的汇总金额
     */
    CartCheckedSummary selectCheckedSummaryByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID和 SKU ID 查询购物车项
     */
    CartItem selectByUserIdAndSkuId(@Param("userId") Long userId, @Param("skuId") Long skuId);

    /**
     * 根据用户ID和选中状态查询购物车项
     */
    List<CartItem> selectByUserIdAndChecked(@Param("userId") Long userId, @Param("checked") Integer checked);

    /**
     * 统计用户购物车 SKU 条目数
     */
    int countByUserId(@Param("userId") Long userId);

    /**
     * 新增购物车项
     */
    int insert(CartItem record);

    /**
     * 按条件新增购物车项
     */
    int insertSelective(CartItem record);

    /**
     * 批量新增购物车项
     */
    int insertBatch(@Param("list") List<CartItem> list);

    /**
     * 根据主键更新购物车项
     */
    int updateByPrimaryKey(CartItem record);

    /**
     * 按条件更新购物车项
     */
    int updateByPrimaryKeySelective(CartItem record);

    /**
     * 增加购物车商品数量
     */
    int updateQuantity(@Param("id") Long id, @Param("quantity") Integer quantity);

    /**
     * 批量增加购物车商品数量
     */
    int batchIncreaseQuantity(@Param("list") List<CartItem> list);

    /**
     * 批量更新选中状态
     */
    int updateCheckedBatch(@Param("ids") List<Long> ids, @Param("checked") Integer checked);

    /**
     * 根据用户ID更新全选状态
     */
    int updateCheckedByUserId(@Param("userId") Long userId, @Param("checked") Integer checked);

    /**
     * 根据主键删除购物车项
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 批量删除购物车项
     */
    int deleteBatch(@Param("ids") List<Long> ids);

    /**
     * 根据用户ID清空购物车
     */
    int deleteByUserId(@Param("userId") Long userId);

    /**
     * 删除用户已选中的购物车项
     */
    int deleteCheckedByUserId(@Param("userId") Long userId);

    /**
     * 查询当前用户已选中的购物车项
     */
    List<CartItem> listChecked(Long userId);
}
