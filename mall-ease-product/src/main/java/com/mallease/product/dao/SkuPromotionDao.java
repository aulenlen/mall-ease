package com.mallease.product.dao;

import com.mallease.product.model.data.entity.SkuPromotion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * SKU促销信息 Mapper 接口
 * 说明：促销价格统一在此表管理，避免多处存储导致不一致
 *
 * @author: Aulen
 * @create: 2025-12-11
 */
@Mapper
public interface SkuPromotionDao {

    /**
     * 根据主键查询
     *
     * @param id 主键ID
     * @return 促销信息记录
     */
    SkuPromotion selectByPrimaryKey(Long id);

    /**
     * 根据SKU ID查询（一对一关系）
     *
     * @param skuId SKU ID
     * @return 促销信息记录
     */
    SkuPromotion selectBySkuId(@Param("skuId") Long skuId);

    /**
     * 根据SKU ID列表批量查询
     *
     * @param skuIds SKU ID列表
     * @return 促销信息列表
     */
    List<SkuPromotion> selectBySkuIds(@Param("skuIds") List<Long> skuIds);

    /**
     * 根据促销类型查询
     *
     * @param promotionType 促销类型：0-无促销 1-促销价 2-会员价 3-阶梯价 4-满减价 5-限时购
     * @return 促销信息列表
     */
    List<SkuPromotion> selectByPromotionType(@Param("promotionType") Integer promotionType);

    /**
     * 查询进行中的促销
     * 条件：promotionType > 0 AND 当前时间在促销时间范围内
     *
     * @return 促销信息列表
     */
    List<SkuPromotion> selectActivePromotions();

    /**
     * 查询预告中的促销商品
     *
     * @return 促销信息列表
     */
    List<SkuPromotion> selectPreviewPromotions();

    /**
     * 插入记录
     *
     * @param record 促销信息记录
     * @return 影响行数
     */
    int insert(SkuPromotion record);

    /**
     * 选择性插入记录
     *
     * @param record 促销信息记录
     * @return 影响行数
     */
    int insertSelective(SkuPromotion record);

    /**
     * 批量插入
     *
     * @param list 促销信息列表
     * @return 影响行数
     */
    int insertBatch(@Param("list") List<SkuPromotion> list);

    /**
     * 根据主键更新
     *
     * @param record 促销信息记录
     * @return 影响行数
     */
    int updateByPrimaryKey(SkuPromotion record);

    /**
     * 根据主键选择性更新
     *
     * @param record 促销信息记录
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(SkuPromotion record);

    /**
     * 根据SKU ID更新促销信息
     *
     * @param record 促销信息记录（需包含skuId）
     * @return 影响行数
     */
    int updateBySkuId(SkuPromotion record);

    /**
     * 批量更新促销类型
     *
     * @param skuIds SKU ID列表
     * @param promotionType 促销类型
     * @return 影响行数
     */
    int updatePromotionTypeBatch(@Param("skuIds") List<Long> skuIds, @Param("promotionType") Integer promotionType);

    /**
     * 结束过期促销
     * 将已过期的促销类型重置为0
     *
     * @param now 当前时间
     * @return 影响行数
     */
    int expirePromotions(@Param("now") LocalDateTime now);

    /**
     * 根据SKU ID删除
     *
     * @param skuId SKU ID
     * @return 影响行数
     */
    int deleteBySkuId(@Param("skuId") Long skuId);

    /**
     * 批量删除
     *
     * @param ids 主键ID列表
     * @return 影响行数
     */
    int deleteBatch(@Param("ids") List<Long> ids);
}