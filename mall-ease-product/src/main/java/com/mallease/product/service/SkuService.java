package com.mallease.product.service;

import com.mallease.common.dto.remote.SkuSimpleDTO;
import com.mallease.product.model.aggregate.SpuAggregate;
import com.mallease.product.model.client.query.SkuQuery;
import com.mallease.product.model.data.entity.Sku;

import java.util.List;

public interface SkuService {

    /**
     * 根据SKU ID列表批量获取简要信息（内部服务调用）
     *
     * @param skuIds SKU ID列表
     * @return SKU简要信息列表
     */
    List<SkuSimpleDTO> listSimpleByIds(List<Long> skuIds);

    /**
     * 创建单个SKU（含库存）
     *
     * @param spuId   SPU ID
     * @param skuData SKU聚合数据（已转换的Entity）
     * @return SKU ID
     */
    Long create(Long spuId, SpuAggregate.SkuData skuData);

    /**
     * 批量创建SKU（含库存）
     *
     * @param spuId   SPU ID
     * @param skuList SKU数据列表（已转换的Entity）
     * @return 影响行数
     */
    int createBatch(Long spuId, List<SpuAggregate.SkuData> skuList);

    /**
     * 更新SKU
     *
     * @param sku SKU实体
     * @return 影响行数
     */
    int update(Sku sku);

    /**
     * 删除SKU（级联删除关联数据）
     *
     * @param id SKU ID
     * @return 影响行数
     */
    int delete(Long id);

    /**
     * 删除SPU下所有SKU
     *
     * @param spuId SPU ID
     * @return 影响行数
     */
    int deleteBySpuId(Long spuId);

    /**
     * 根据ID获取SKU
     *
     * @param id SKU ID
     * @return SKU实体
     */
    Sku getById(Long id);

    /**
     * 根据SPU ID查询SKU列表
     *
     * @param spuId SPU ID
     * @return SKU列表
     */
    List<Sku> listBySpuId(Long spuId);

    /**
     * 分页查询SKU
     *
     * @param query 查询条件
     * @return SKU列表
     */
    List<Sku> list(SkuQuery query);

    /**
     * 批量更新启用状态
     *
     * @param ids    SKU ID列表
     * @param status 状态值
     * @return 影响行数
     */
    int updateEnableStatus(List<Long> ids, Integer status);

    /**
     * 批量获取SKU
     *
     * @param spuIds spuId列表
     * @return sku列表
     */
    List<Sku> selectBySpuIds(List<Long> spuIds);

}
