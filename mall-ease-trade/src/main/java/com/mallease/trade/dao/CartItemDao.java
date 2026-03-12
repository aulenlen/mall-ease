package com.mallease.trade.dao;

import com.mallease.trade.model.aggregate.CartCheckedSummary;
import com.mallease.trade.model.data.entity.CartItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 购物车项 Mapper 接口。
 *
 * @author: Aulen
 * @create: 2026-01-27
 */
@Mapper
public interface CartItemDao {

    /**
     * 根据主键查询购物车项。
     *
     * @param id 主键ID
     * @return 购物车项
     */
    CartItem selectByPrimaryKey(Long id);

    /**
     * 根据ID列表批量查询购物车项。
     *
     * @param ids 主键ID列表
     * @return 购物车项列表
     */
    List<CartItem> selectByIds(@Param("ids") List<Long> ids);

    /**
     * 根据用户ID查询购物车列表。
     *
     * @param userId 用户ID
     * @return 购物车项列表
     */
    List<CartItem> selectByUserId(@Param("userId") Long userId);

    /**
     * 鎵归噺鏍规嵁鐢ㄦ埛ID鏌ヨ璐墿杞﹂」銆?
     *
     * @param userIds 鐢ㄦ埛ID鍒楄〃
     * @return 璐墿杞﹂」鍒楄〃
     */
    List<CartItem> selectByUserIds(@Param("userIds") List<Long> userIds);

    /**
     * 查询已选中商品总金额。
     *
     * @param userId 用户ID
     * @return 已选中汇总
     */
    CartCheckedSummary selectCheckedSummaryByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID和SKU ID查询购物车项。
     *
     * @param userId 用户ID
     * @param skuId  SKU ID
     * @return 购物车项
     */
    CartItem selectByUserIdAndSkuId(@Param("userId") Long userId, @Param("skuId") Long skuId);

    /**
     * 根据用户ID和选中状态查询购物车项。
     *
     * @param userId  用户ID
     * @param checked 选中状态
     * @return 购物车项列表
     */
    List<CartItem> selectByUserIdAndChecked(@Param("userId") Long userId, @Param("checked") Integer checked);

    /**
     * 统计用户购物车SKU条目数。
     *
     * @param userId 用户ID
     * @return SKU条目数
     */
    int countByUserId(@Param("userId") Long userId);

    /**
     * 新增购物车项。
     *
     * @param record 购物车项
     * @return 影响行数
     */
    int insert(CartItem record);

    /**
     * 按条件新增购物车项。
     *
     * @param record 购物车项
     * @return 影响行数
     */
    int insertSelective(CartItem record);

    /**
     * 批量新增购物车项。
     *
     * @param list 购物车项列表
     * @return 影响行数
     */
    int insertBatch(@Param("list") List<CartItem> list);

    /**
     * 根据主键更新购物车项。
     *
     * @param record 购物车项
     * @return 影响行数
     */
    int updateByPrimaryKey(CartItem record);

    /**
     * 按条件更新购物车项。
     *
     * @param record 购物车项
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(CartItem record);

    /**
     * 累加购物车商品数量。
     *
     * @param id       主键ID
     * @param quantity 需要累加的数量
     * @return 影响行数
     */
    int updateQuantity(@Param("id") Long id, @Param("quantity") Integer quantity);

    /**
     * 鎵归噺绱姞璐墿杞﹀晢鍝佹暟閲忋€?
     *
     * @param list 闇€瑕佺疮鍔犳暟閲忕殑璐墿杞﹂」鍒楄〃
     * @return 褰卞搷琛屾暟
     */
    int batchIncreaseQuantity(@Param("list") List<CartItem> list);

    /**
     * 批量更新选中状态。
     *
     * @param ids     主键ID列表
     * @param checked 选中状态
     * @return 影响行数
     */
    int updateCheckedBatch(@Param("ids") List<Long> ids, @Param("checked") Integer checked);

    /**
     * 根据用户ID更新全选状态。
     *
     * @param userId  用户ID
     * @param checked 选中状态
     * @return 影响行数
     */
    int updateCheckedByUserId(@Param("userId") Long userId, @Param("checked") Integer checked);

    /**
     * 根据主键删除购物车项。
     *
     * @param id 主键ID
     * @return 影响行数
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 批量删除购物车项。
     *
     * @param ids 主键ID列表
     * @return 影响行数
     */
    int deleteBatch(@Param("ids") List<Long> ids);

    /**
     * 根据用户ID清空购物车。
     *
     * @param userId 用户ID
     * @return 影响行数
     */
    int deleteByUserId(@Param("userId") Long userId);

    /**
     * 删除用户已选中的购物车项。
     *
     * @param userId 用户ID
     * @return 影响行数
     */
    int deleteCheckedByUserId(@Param("userId") Long userId);

    /**
     * 查询当前用户已选中的购物车项。
     *
     * @param userId 用户ID
     * @return 购物车项列表
     */
    List<CartItem> listChecked(Long userId);
}
