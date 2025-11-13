package com.mallease.pms.dao;

import com.mallease.pms.pojo.PmsMemberPrice;
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
public interface PmsMemberPriceDao {
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
    int insert(PmsMemberPrice record);

    /**
     * 选择性插入记录
     *
     * @param record 记录
     * @return 影响行数
     */
    int insertSelective(PmsMemberPrice record);

    /**
     * 根据主键查询
     *
     * @param id 主键ID
     * @return 记录
     */
    PmsMemberPrice selectByPrimaryKey(Long id);

    /**
     * 根据主键选择性更新
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(PmsMemberPrice record);

    /**
     * 根据主键更新
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKey(PmsMemberPrice record);

    /**
     * 根据产品ID查询
     *
     * @param productId 产品ID
     * @return 记录列表
     */
    List<PmsMemberPrice> selectByProductId(@Param("productId") Long productId);

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
    List<PmsMemberPrice> selectByMemberLevelId(@Param("memberLevelId") Long memberLevelId);
}