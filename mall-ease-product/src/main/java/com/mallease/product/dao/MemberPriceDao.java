package com.mallease.product.dao;

import com.mallease.product.model.data.entity.MemberPrice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品会员价格表 Mapper 接口
 *
 * @author: Aulen
 * @create: 2025-11-13
 */
@Mapper
public interface MemberPriceDao {
    /**
     * 根据主键删除
     *
     * @param id 主键ID
     * @return 影响行数
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 插入记录
     *
     * @param record 记录
     * @return 影响行数
     */
    int insert(MemberPrice record);

    /**
     * 选择性插入记录
     *
     * @param record 记录
     * @return 影响行数
     */
    int insertSelective(MemberPrice record);

    /**
     * 根据主键查询
     *
     * @param id 主键ID
     * @return 记录
     */
    MemberPrice selectByPrimaryKey(Long id);

    /**
     * 根据主键选择性更新
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(MemberPrice record);

    /**
     * 根据主键更新
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKey(MemberPrice record);

    /**
     * 根据产品ID查询
     *
     * @param productId 产品ID
     * @return 记录列表
     */
    List<MemberPrice> selectByProductId(@Param("productId") Long productId);

    /**
     * 根据产品ID删除
     *
     * @param productId 产品ID
     * @return 影响行数
     */
    int deleteByProductId(@Param("productId") Long productId);

    /**
     * 根据会员等级ID查询
     *
     * @param memberLevelId 会员等级ID
     * @return 记录列表
     */
    List<MemberPrice> selectByMemberLevelId(@Param("memberLevelId") Long memberLevelId);

    /**
     * 批量插入记录
     *
     * @param list 记录列表
     * @return 影响行数
     */
    int insertBatch(@Param("list") List<MemberPrice> list);

    /**
     * 根据产品ID列表查询
     * @param productIds 产品ID列表
     * @return 记录列表
     */
    List<MemberPrice> selectByProductIds(@Param("productIds") List<Long> productIds);
}