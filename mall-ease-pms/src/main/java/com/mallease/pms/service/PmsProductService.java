package com.mallease.pms.service;

import com.mallease.pms.dto.request.PmsProductAggregationRequest;
import com.mallease.pms.dto.request.PmsProductRequest;
import com.mallease.pms.dto.response.PmsProductResponse;
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
     * 创建商品（包含所有关联信息）
     *
     * @param request 商品聚合请求
     * @return 影响行数
     */
    int createProduct(PmsProductAggregationRequest request);

    /**
     * 批量更新商品上架状态
     *
     * @param ids 商品ID列表
     * @param publishStatus 上架状态：0->下架；1->上架
     * @return 更新的记录数
     */
    int updatePublishStatusBatch(List<Long> ids, Integer publishStatus);

    /**
     * 批量更新商品新品状态
     *
     * @param ids 商品ID列表
     * @param newStatus 新品状态：0->不是新品；1->新品
     * @return 更新的记录数
     */
    int updateNewStatusBatch(List<Long> ids, Integer newStatus);

    /**
     * 批量更新商品推荐状态
     *
     * @param ids 商品ID列表
     * @param recommendStatus 推荐状态：0->不推荐；1->推荐
     * @return 更新的记录数
     */
    int updateRecommendStatusBatch(List<Long> ids, Integer recommendStatus);

    /**
     * 批量修改商品审核状态
     *
     * @param ids 商品ID列表
     * @param verifyStatus 审核状态：0->未审核；1->审核通过
     * @param detail 审核详情
     * @return 更新的记录数
     */
    int updateVerifyStatusBatch(List<Long> ids, Integer verifyStatus, String detail);

    /**
     * 根据商品ID获取商品编辑信息
     *
     * @param id 商品ID
     * @return 商品完整信息
     */
    PmsProductResponse getUpdateInfo(Long id);

    /**
     * 更新商品（包含所有关联信息）
     *
     * @param id 商品ID
     * @param request 商品聚合请求
     * @return 影响行数
     */
    int updateProduct(Long id, PmsProductAggregationRequest request);
}
