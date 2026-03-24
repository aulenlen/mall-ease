package com.mallease.product.service.sku;

import com.mallease.common.dto.remote.SkuSimpleDTO;
import com.mallease.product.controller.admin.sku.vo.SkuPageReqVO;
import com.mallease.product.controller.admin.sku.vo.SkuSaveReqVO;
import com.mallease.product.dal.entity.Sku;

import java.util.List;

/**
 * 管理 SKU 的后台维护与基础查询能力。
 */
public interface SkuService {

    /**
     * 按 SKU ID 批量查询轻量信息，供远程调用使用。
     */
    List<SkuSimpleDTO> listSimpleByIds(List<Long> skuIds);

    /**
     * 为指定 SPU 创建 SKU。
     */
    Long create(Long spuId, SkuSaveReqVO reqVO);

    /**
     * 按后台表单更新 SKU。
     */
    int update(SkuSaveReqVO reqVO);

    /**
     * 按实体直接更新 SKU。
     */
    int update(Sku sku);

    /**
     * 删除单个 SKU。
     */
    int delete(Long id);

    /**
     * 删除指定 SPU 下全部 SKU。
     */
    int deleteBySpuId(Long spuId);

    /**
     * 查询单个 SKU 详情。
     */
    Sku get(Long id);

    /**
     * 查询指定 SPU 下的全部 SKU。
     */
    List<Sku> listBySpuId(Long spuId);

    /**
     * 按后台筛选条件分页查询 SKU。
     */
    List<Sku> page(SkuPageReqVO reqVO);

    /**
     * 批量更新 SKU 上下架状态。
     */
    int updateEnableStatus(List<Long> ids, Integer status);

    /**
     * 按 SPU ID 批量查询 SKU，供发布、索引等链路使用。
     */
    List<Sku> selectBySpuIds(List<Long> spuIds);
}
