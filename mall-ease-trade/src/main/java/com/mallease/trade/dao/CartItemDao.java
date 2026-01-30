package com.mallease.trade.dao;

import com.mallease.trade.model.data.entity.CartItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 购物车 Mapper 接口
 *
 * @author: Aulen
 * @create: 2026-01-27
 */
@Mapper
public interface CartItemDao {

    /**
     * 根据主键查询
     *
     * @param id 主键ID
     * @return 购物车项
     */
    CartItem selectByPrimaryKey(Long id);

    /**
     * 根据主键列表批量查询
     *
     * @param ids 主键ID列表
     * @return 购物车项列表
     */
    List<CartItem> selectByIds(@Param("ids") List<Long> ids);

    /**
     * 根据用户ID查询购物车列表
     *
     * @param userId 用户ID
     * @return 购物车项列表
     */
    List<CartItem> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID和SKU ID查询（用于判断是否已存在）
     *
     * @param userId 用户ID
     * @param skuId  SKU ID
     * @return 购物车项
     */
    CartItem selectByUserIdAndSkuId(@Param("userId") Long userId, @Param("skuId") Long skuId);

    /**
     * 根据用户ID和选中状态查询
     *
     * @param userId  用户ID
     * @param checked 选中状态
     * @return 购物车项列表
     */
    List<CartItem> selectByUserIdAndChecked(@Param("userId") Long userId, @Param("checked") Integer checked);

    /**
     * 统计用户购物车商品数量
     *
     * @param userId 用户ID
     * @return 商品数量
     */
    int countByUserId(@Param("userId") Long userId);

    /**
     * 插入记录
     *
     * @param record 购物车项
     * @return 影响行数
     */
    int insert(CartItem record);

    /**
     * 选择性插入记录（只插入非空字段）
     *
     * @param record 购物车项
     * @return 影响行数
     */
    int insertSelective(CartItem record);

    /**
     * 批量插入
     *
     * @param list 购物车项列表
     * @return 影响行数
     */
    int insertBatch(@Param("list") List<CartItem> list);

    /**
     * 根据主键更新（全字段）
     *
     * @param record 购物车项
     * @return 影响行数
     */
    int updateByPrimaryKey(CartItem record);

    /**
     * 根据主键选择性更新（只更新非空字段）
     *
     * @param record 购物车项
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(CartItem record);

    /**
     * 更新数量（增量更新）
     *
     * @param id       主键ID
     * @param quantity 增量数量（可为负数）
     * @return 影响行数
     */
    int updateQuantity(@Param("id") Long id, @Param("quantity") Integer quantity);

    /**
     * 批量更新选中状态
     *
     * @param ids     主键ID列表
     * @param checked 选中状态
     * @return 影响行数
     */
    int updateCheckedBatch(@Param("ids") List<Long> ids, @Param("checked") Integer checked);

    /**
     * 更新用户所有购物车项的选中状态
     *
     * @param userId  用户ID
     * @param checked 选中状态
     * @return 影响行数
     */
    int updateCheckedByUserId(@Param("userId") Long userId, @Param("checked") Integer checked);

    /**
     * 根据主键删除
     *
     * @param id 主键ID
     * @return 影响行数
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 批量删除
     *
     * @param ids 主键ID列表
     * @return 影响行数
     */
    int deleteBatch(@Param("ids") List<Long> ids);

    /**
     * 根据用户ID删除所有购物车项
     *
     * @param userId 用户ID
     * @return 影响行数
     */
    int deleteByUserId(@Param("userId") Long userId);

    /**
     * 删除用户已选中的购物车项（下单后清理）
     *
     * @param userId 用户ID
     * @return 影响行数
     */
    int deleteCheckedByUserId(@Param("userId") Long userId);

    /**
     * 获取用户勾选的购物车项
     * @param userId 会员ID
     * @return 购物车项列表
     */
    List<CartItem> listChecked(Long userId);
}
