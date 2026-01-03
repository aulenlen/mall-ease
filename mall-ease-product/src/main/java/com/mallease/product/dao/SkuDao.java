package com.mallease.product.dao;

import com.mallease.product.model.data.entity.Sku;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * SKU Mapper 接口
 *
 * @author: Aulen
 * @create: 2025-12-11
 */
@Mapper
public interface SkuDao {

    /**
     * 根据主键查询
     *
     * @param id 主键ID
     * @return SKU记录
     */
    Sku selectByPrimaryKey(Long id);

    /**
     * 根据主键列表批量查询
     *
     * @param ids 主键ID列表
     * @return SKU列表
     */
    List<Sku> selectByIds(@Param("ids") List<Long> ids);

    /**
     * 根据SKU编码查询
     *
     * @param skuCode SKU编码
     * @return SKU记录
     */
    Sku selectBySkuCode(@Param("skuCode") String skuCode);

    /**
     * 根据SPU ID查询所有SKU
     *
     * @param spuId SPU ID
     * @return SKU列表
     */
    List<Sku> selectBySpuId(@Param("spuId") Long spuId);

    /**
     * 根据SPU ID列表批量查询SKU
     *
     * @param spuIds SPU ID列表
     * @return SKU列表
     */
    List<Sku> selectBySpuIds(@Param("spuIds") List<Long> spuIds);

    /**
     * 根据多条件查询SKU列表
     *
     * @param spuId SPU ID
     * @param enableStatus 启用状态
     * @return SKU列表
     */
    List<Sku> selectByConditions(@Param("spuId") Long spuId,
                                    @Param("enableStatus") Integer enableStatus);

    /**
     * 插入记录
     *
     * @param record SKU记录
     * @return 影响行数
     */
    int insert(Sku record);

    /**
     * 选择性插入记录（只插入非空字段）
     *
     * @param record SKU记录
     * @return 影响行数
     */
    int insertSelective(Sku record);

    /**
     * 批量插入
     *
     * @param list SKU列表
     * @return 影响行数
     */
    int insertBatch(@Param("list") List<Sku> list);

    /**
     * 根据主键更新（全字段）
     *
     * @param record SKU记录
     * @return 影响行数
     */
    int updateByPrimaryKey(Sku record);

    /**
     * 根据主键选择性更新（只更新非空字段）
     *
     * @param record SKU记录
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(Sku record);

    /**
     * 批量更新启用状态
     *
     * @param ids SKU ID列表
     * @param enableStatus 启用状态：0-禁用 1-启用
     * @return 影响行数
     */
    int updateEnableStatusBatch(@Param("ids") List<Long> ids, @Param("enableStatus") Integer enableStatus);

    /**
     * 批量逻辑删除
     *
     * @param ids SKU ID列表
     * @return 影响行数
     */
    int deleteBatch(@Param("ids") List<Long> ids);

    /**
     * 根据SPU ID删除所有SKU（逻辑删除）
     *
     * @param spuId SPU ID
     * @return 影响行数
     */
    int deleteBySpuId(@Param("spuId") Long spuId);

    /**
     * 查询所有记录
     *
     * @return SKU列表
     */
    List<Sku> selectAll();
}
