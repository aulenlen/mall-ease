package com.mallease.pms.service;

import com.mallease.pms.dto.request.PmsProductAttributeCreateRequest;
import com.mallease.pms.dto.response.ProductAttrResponse;
import com.mallease.pms.pojo.PmsProductAttribute;

import java.util.List;

/**
 * @author: Aulen
 * @description:
 * @create: 2025-11-12 21:42
 **/
public interface PmsProductAttributeService {
    List<ProductAttrResponse> getProductAttrInfo(Long productCategoryId);

    List<PmsProductAttribute> listByAttributeCategoryIdAndType(Integer cid, Integer type);

    /**
     * 创建商品属性
     *
     * @param request 创建请求参数
     * @return 创建后的商品属性信息
     */
    PmsProductAttribute create(PmsProductAttributeCreateRequest request);

    /**
     * 批量删除商品属性
     *
     * @param ids 属性ID列表
     * @return 删除的记录数
     */
    Integer deleteBatch(List<Long> ids);
}
