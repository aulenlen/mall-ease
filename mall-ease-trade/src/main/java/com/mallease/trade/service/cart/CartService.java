package com.mallease.trade.service.cart;

import com.mallease.common.api.Page;
import com.mallease.trade.controller.portal.cart.vo.CartPageRespVO;
import com.mallease.trade.controller.portal.cart.vo.CartSummaryRespVO;
import com.mallease.trade.dal.entity.CartItem;

import java.util.List;

/**
 * 购物车服务接口。
 *
 * @author: Aulen
 * @create: 2026-01-27
 */
public interface CartService {

    /**
     * 前台分页获取购物车，并返回整车汇总。
     *
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 购物车分页结果
     */
    CartPageRespVO getCartPage(Integer pageNum, Integer pageSize);

    /**
     * 前台添加商品到购物车。
     *
     * @param userId 用户ID
     * @param skuId SKU ID
     * @param quantity 数量
     * @return 购物车项ID
     */
    Long addToCart(Long userId, Long skuId, Integer quantity);

    /**
     * 根据ID获取购物车项。
     *
     * @param id 主键ID
     * @return 购物车项
     */
    CartItem getById(Long id);

    /**
     * 根据ID列表批量获取购物车项。
     *
     * @param ids 主键ID列表
     * @return 购物车项列表
     */
    List<CartItem> listByIds(List<Long> ids);

    /**
     * 获取用户购物车列表。
     *
     * @param userId 用户ID
     * @return 购物车项列表
     */
    List<CartItem> listByUserId(Long userId);

    /**
     * 分页获取用户购物车列表。
     *
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 分页结果
     */
    Page<CartItem> pageByUserId(Long userId, int pageNum, int pageSize);

    /**
     * 获取用户已选中的购物车项。
     *
     * @param userId 用户ID
     * @return 购物车项列表
     */
    List<CartItem> listCheckedByUserId(Long userId);

    /**
     * 统计用户购物车 SKU 条目数。
     *
     * @param userId 用户ID
     * @return SKU 条目数
     */
    int countByUserId(Long userId);

    /**
     * 添加商品到购物车，已存在则增加数量。
     *
     * @param cartItem 购物车项
     * @return 购物车项ID
     */
    Long add(CartItem cartItem);

    /**
     * 更新购物车项数量。
     *
     * @param id 主键ID
     * @param quantity 新数量
     * @return 最新购物车汇总
     */
    CartSummaryRespVO updateQuantity(Long id, Integer quantity);

    /**
     * 批量更新选中状态，并返回最新结算金额。
     *
     * @param userId 用户ID
     * @param ids 主键ID列表
     * @param checked 选中状态
     * @return 最新购物车汇总
     */
    CartSummaryRespVO updateChecked(Long userId, List<Long> ids, Integer checked);

    /**
     * 全选/取消全选，并返回最新结算金额。
     *
     * @param userId 用户ID
     * @param checked 选中状态
     * @return 最新购物车汇总
     */
    CartSummaryRespVO updateCheckedAll(Long userId, Integer checked);

    /**
     * 删除购物车项。
     *
     * @param id 主键ID
     * @return 影响行数
     */
    int delete(Long id);

    /**
     * 批量删除购物车项。
     *
     * @param ids 主键ID列表
     * @return 影响行数
     */
    int deleteBatch(List<Long> ids);

    /**
     * 清空用户购物车。
     *
     * @param userId 用户ID
     * @return 影响行数
     */
    int clearByUserId(Long userId);

    /**
     * 删除用户已选中的购物车项，下单后调用。
     *
     * @param userId 用户ID
     * @return 影响行数
     */
    int deleteCheckedByUserId(Long userId);

    /**
     * 获取当前登录用户已选中的购物车项。
     *
     * @return 购物车项列表
     */
    List<CartItem> listChecked();
}
