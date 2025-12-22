package com.mallease.pms.service;

import com.mallease.pms.dto.context.SkuCreateData;
import com.mallease.pms.dto.query.PmsSkuQuery;
import com.mallease.pms.pojo.PmsSku;
import com.mallease.pms.pojo.PmsSkuStock;

import java.util.List;
import java.util.Map;

public interface PmsSkuService {

    /**
     * 批量创建SKU（含库存、促销、价格策略）
     *
     * @param spuId       SPU ID
     * @param skuDataList SKU创建数据列表（已转换的Entity）
     * @return 影响行数
     */
    int createBatch(Long spuId, List<SkuCreateData> skuDataList);

    /**
     * 更新SKU
     *
     * @param sku SKU实体
     * @return 影响行数
     */
    int update(PmsSku sku);

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
    PmsSku getById(Long id);

    /**
     * 根据SPU ID查询SKU列表
     *
     * @param spuId SPU ID
     * @return SKU列表
     */
    List<PmsSku> listBySpuId(Long spuId);

    /**
     * 分页查询SKU
     *
     * @param query 查询条件
     * @return SKU列表
     */
    List<PmsSku> list(PmsSkuQuery query);

    /**
     * 批量更新启用状态
     *
     * @param ids    SKU ID列表
     * @param status 状态值
     * @return 影响行数
     */
    int updateEnableStatus(List<Long> ids, Integer status);

}
