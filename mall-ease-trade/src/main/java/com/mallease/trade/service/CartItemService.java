package com.mallease.trade.service;

import com.mallease.trade.model.data.entity.CartItem;

import java.util.List;

/**
 * 购物车服务接口
 *
 * @author: Aulen
 * @create: 2026-01-27
 */
public interface CartItemService {

    /**
     * 根据ID获取购物车项
     *
     * @param id 主键ID
     * @return 购物车项
     */
    CartItem getById(Long id);

    /**
     * 根据ID列表批量获取购物车项
     *
     * @param ids 主键ID列表
     * @return 购物车项列表
     */
    List<CartItem> listByIds(List<Long> ids);

    /**
     * 获取用户购物车列表
     *
     * @param userId 用户ID
     * @return 购物车项列表
     */
    List<CartItem> listByUserId(Long userId);

    /**
     * 获取用户已选中的购物车项
     *
     * @param userId 用户ID
     * @return 购物车项列表
     */
    List<CartItem> listCheckedByUserId(Long userId);

    /**
     * 统计用户购物车商品数量
     *
     * @param userId 用户ID
     * @return 商品数量
     */
    int countByUserId(Long userId);

    /**
     * 添加商品到购物车（已存在则增加数量）
     *
     * @param cartItem 购物车项
     * @return 购物车项ID
     */
    Long add(CartItem cartItem);

    /**
     * 更新购物车项数量
     *
     * @param id       主键ID
     * @param quantity 新数量
     * @return 影响行数
     */
    int updateQuantity(Long id, Integer quantity);

    /**
     * 更新选中状态
     *
     * @param ids     主键ID列表
     * @param checked 选中状态
     * @return 影响行数
     */
    int updateChecked(List<Long> ids, Integer checked);

    /**
     * 全选/取消全选
     *
     * @param userId  用户ID
     * @param checked 选中状态
     * @return 影响行数
     */
    int updateCheckedAll(Long userId, Integer checked);

    /**
     * 删除购物车项
     *
     * @param id 主键ID
     * @return 影响行数
     */
    int delete(Long id);

    /**
     * 批量删除购物车项
     *
     * @param ids 主键ID列表
     * @return 影响行数
     */
    int deleteBatch(List<Long> ids);

    /**
     * 清空用户购物车
     *
     * @param userId 用户ID
     * @return 影响行数
     */
    int clearByUserId(Long userId);

    /**
     * 删除用户已选中的购物车项（下单后调用）
     *
     * @param userId 用户ID
     * @return 影响行数
     */
    int deleteCheckedByUserId(Long userId);
}
