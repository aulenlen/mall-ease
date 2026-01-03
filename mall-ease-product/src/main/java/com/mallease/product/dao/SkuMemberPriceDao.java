package com.mallease.product.dao;

import com.mallease.product.model.data.entity.SkuMemberPrice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * SKU会员价格 Mapper 接口
 * 说明：存储不同会员等级的 SKU 专属价格
 *
 * @author: Aulen
 * @create: 2025-12-11
 */
@Mapper
public interface SkuMemberPriceDao {

    /**
     * 根据主键查询
     *
     * @param id 主键ID
     * @return 会员价格记录
     */
    SkuMemberPrice selectByPrimaryKey(Long id);

    /**
     * 根据SKU ID查询所有会员价格
     *
     * @param skuId SKU ID
     * @return 会员价格列表
     */
    List<SkuMemberPrice> selectBySkuId(@Param("skuId") Long skuId);

    /**
     * 根据SKU ID列表批量查询
     *
     * @param skuIds SKU ID列表
     * @return 会员价格列表
     */
    List<SkuMemberPrice> selectBySkuIds(@Param("skuIds") List<Long> skuIds);

    /**
     * 根据SKU ID和会员等级ID查询
     *
     * @param skuId SKU ID
     * @param memberLevelId 会员等级ID
     * @return 会员价格记录
     */
    SkuMemberPrice selectBySkuIdAndMemberLevelId(@Param("skuId") Long skuId,
                                                    @Param("memberLevelId") Long memberLevelId);

    /**
     * 插入记录
     *
     * @param record 会员价格记录
     * @return 影响行数
     */
    int insert(SkuMemberPrice record);

    /**
     * 选择性插入记录
     *
     * @param record 会员价格记录
     * @return 影响行数
     */
    int insertSelective(SkuMemberPrice record);

    /**
     * 批量插入
     *
     * @param list 会员价格列表
     * @return 影响行数
     */
    int insertBatch(@Param("list") List<SkuMemberPrice> list);

    /**
     * 根据主键更新
     *
     * @param record 会员价格记录
     * @return 影响行数
     */
    int updateByPrimaryKey(SkuMemberPrice record);

    /**
     * 根据主键选择性更新
     *
     * @param record 会员价格记录
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(SkuMemberPrice record);

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
     * @param ids ID列表
     * @return 影响行数
     */
    int deleteBatch(@Param("ids") List<Long> ids);
}