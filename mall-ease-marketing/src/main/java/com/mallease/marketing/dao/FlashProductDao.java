package com.mallease.marketing.dao;

import com.mallease.marketing.model.data.entity.FlashProduct;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 秒杀商品关联 Mapper 接口
 *
 * @author: Aulen
 * @create: 2026-01-04
 */
@Mapper
public interface FlashProductDao {

    /**
     * 插入记录
     */
    int insert(FlashProduct record);

    /**
     * 选择性插入记录
     */
    int insertSelective(FlashProduct record);

    /**
     * 批量插入
     *
     * @param list 商品关联列表
     * @return 影响行数
     */
    int insertBatch(@Param("list") List<FlashProduct> list);

    /**
     * 根据主键查询
     */
    FlashProduct selectByPrimaryKey(Long id);

    /**
     * 根据主键选择性更新
     */
    int updateByPrimaryKeySelective(FlashProduct record);

    /**
     * 根据主键删除
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 条件查询商品列表
     */
    List<FlashProduct> listByConditions(@Param("flashSessionId") Long flashSessionId,
                                        @Param("spuIds") List<Long> spuIds,
                                        @Param("spuId") Long spuId,
                                        @Param("skuId") Long skuId,
                                        @Param("routeType") Integer routeType);

    /**
     * 查询候选SPU ID列表
     */
    List<Long> selectDistinctSpuIds(@Param("flashSessionId") Long flashSessionId);

    /**
     * 根据场次ID查询商品列表
     */
    List<FlashProduct> selectBySessionId(@Param("flashSessionId") Long flashSessionId);

    /**
     * 检查SKU是否已存在于指定场次
     *
     * @param flashSessionId 场次ID
     * @param skuId                   SKU ID
     * @return 已存在的记录（null表示不存在）
     */
    FlashProduct selectBySessionAndSku(
            @Param("flashSessionId") Long flashSessionId,
            @Param("skuId") Long skuId);

    /**
     * 扣减秒杀库存
     *
     * @param id       商品关联ID
     * @param quantity 扣减数量
     * @return 影响行数（0表示库存不足）
     */
    int decreaseStock(@Param("id") Long id, @Param("quantity") Integer quantity);

    /**
     * 根据场次ID删除所有商品关联
     *
     * @param flashSessionId 场次ID
     * @return 影响行数
     */
    int deleteBySessionId(@Param("flashSessionId") Long flashSessionId);

    /**
     * 批量删除
     *
     * @param ids 商品关联ID列表
     * @return 影响行数
     */
    int deleteBatch(@Param("ids") List<Long> ids);

    List<FlashProduct> selectBySessionIds(@Param("sessionIds") List<Long> sessionIds);
}
