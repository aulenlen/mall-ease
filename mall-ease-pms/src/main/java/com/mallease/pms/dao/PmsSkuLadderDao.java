package com.mallease.pms.dao;

import com.mallease.pms.pojo.PmsSkuLadder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * SKU阶梯价格 Mapper 接口
 * 说明：存储 SKU 的阶梯价格（买得越多越便宜）
 *
 * @author: Aulen
 * @create: 2025-12-11
 */
@Mapper
public interface PmsSkuLadderDao {

    /**
     * 根据主键查询
     *
     * @param id 主键ID
     * @return 阶梯价格记录
     */
    PmsSkuLadder selectByPrimaryKey(Long id);

    /**
     * 根据SKU ID查询所有阶梯价格
     *
     * @param skuId SKU ID
     * @return 阶梯价格列表（按数量升序排列）
     */
    List<PmsSkuLadder> selectBySkuId(@Param("skuId") Long skuId);

    /**
     * 根据SKU ID列表批量查询
     *
     * @param skuIds SKU ID列表
     * @return 阶梯价格列表
     */
    List<PmsSkuLadder> selectBySkuIds(@Param("skuIds") List<Long> skuIds);

    /**
     * 查询满足数量的最优阶梯价格
     *
     * @param skuId SKU ID
     * @param count 购买数量
     * @return 阶梯价格记录（返回满足条件的最大阶梯）
     */
    PmsSkuLadder selectBestLadder(@Param("skuId") Long skuId, @Param("count") Integer count);

    /**
     * 插入记录
     *
     * @param record 阶梯价格记录
     * @return 影响行数
     */
    int insert(PmsSkuLadder record);

    /**
     * 选择性插入记录
     *
     * @param record 阶梯价格记录
     * @return 影响行数
     */
    int insertSelective(PmsSkuLadder record);

    /**
     * 批量插入
     *
     * @param list 阶梯价格列表
     * @return 影响行数
     */
    int insertBatch(@Param("list") List<PmsSkuLadder> list);

    /**
     * 根据主键更新
     *
     * @param record 阶梯价格记录
     * @return 影响行数
     */
    int updateByPrimaryKey(PmsSkuLadder record);

    /**
     * 根据主键选择性更新
     *
     * @param record 阶梯价格记录
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(PmsSkuLadder record);

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