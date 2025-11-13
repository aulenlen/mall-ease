package com.mallease.pms.service;

import com.mallease.pms.dto.request.PmsProductRequest;
import com.mallease.pms.pojo.PmsProduct;

import java.util.List;

/**
 * 商品服务接口
 *
 * @author: Aulen
 * @create: 2025-11-13
 */
public interface PmsProductService {
    /**
     * 根据条件查询商品列表（支持分页）
     *
     * @param request 查询请求参数
     * @return 商品列表
     */
    List<PmsProduct> list(PmsProductRequest request);

    /**
     * 批量更新商品上架状态
     *
     * @param ids 商品ID列表
     * @param publishStatus 上架状态：0->下架；1->上架
     * @return 更新的记录数
     */
    int updatePublishStatusBatch(List<Long> ids, Integer publishStatus);
}
