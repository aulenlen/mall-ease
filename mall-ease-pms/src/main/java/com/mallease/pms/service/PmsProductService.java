package com.mallease.pms.service;

import com.mallease.pms.dto.cmd.CreateProductCmd;
import com.mallease.pms.dto.cmd.UpdateProductCmd;
import com.mallease.pms.dto.query.ProductQuery;
import com.mallease.pms.dto.vo.PmsProductDetailVO;
import com.mallease.pms.pojo.PmsProduct;

import java.util.List;

/**
 * 商品服务接口
 *
 * @author: Aulen
 * @create: 2025-11-15
 */
public interface PmsProductService {

    /**
     * 根据条件查询商品列表（支持分页）
     *
     * @param query 查询条件
     * @return 商品列表
     */
    List<PmsProduct> list(ProductQuery query);

    /**
     * 创建商品（包含所有关联信息）
     *
     * @param cmd 创建商品命令
     * @return 影响行数
     */
    int createProduct(CreateProductCmd cmd);

    /**
     * 批量更新商品上架状态
     *
     * @param ids 商品ID列表
     * @param publishStatus 上架状态(0:下架 1:上架)
     * @return 更新的记录数
     */
    int updatePublishStatusBatch(List<Long> ids, Integer publishStatus);

    /**
     * 批量更新商品新品状态
     *
     * @param ids 商品ID列表
     * @param newStatus 新品状态(0:不是新品 1:新品)
     * @return 更新的记录数
     */
    int updateNewStatusBatch(List<Long> ids, Integer newStatus);

    /**
     * 批量更新商品推荐状态
     *
     * @param ids 商品ID列表
     * @param recommendStatus 推荐状态(0:不推荐 1:推荐)
     * @return 更新的记录数
     */
    int updateRecommendStatusBatch(List<Long> ids, Integer recommendStatus);

    /**
     * 批量修改商品审核状态
     *
     * @param ids 商品ID列表
     * @param verifyStatus 审核状态(0:未审核 1:审核通过)
     * @param detail 审核详情
     * @return 更新的记录数
     */
    int updateVerifyStatusBatch(List<Long> ids, Integer verifyStatus, String detail);

    /**
     * 批量更新商品删除状态
     *
     * @param ids 商品ID列表
     * @param deleteStatus 删除状态(0:未删除 1:已删除)
     * @return 更新的记录数
     */
    int updateDeleteStatusBatch(List<Long> ids, Integer deleteStatus);

    /**
     * 根据商品ID获取商品详情信息
     *
     * @param id 商品ID
     * @return 商品详情
     */
    PmsProductDetailVO getUpdateInfo(Long id);

    /**
     * 更新商品（包含所有关联信息）
     *
     * @param id 商品ID
     * @param cmd 更新商品命令
     * @return 影响行数
     */
    int updateProduct(Long id, UpdateProductCmd cmd);
}
