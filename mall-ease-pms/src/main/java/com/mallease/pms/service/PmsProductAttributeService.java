package com.mallease.pms.service;

import com.mallease.pms.pojo.PmsProductAttribute;

import java.util.List;

/**
 * 商品属性服务接口
 *
 * @author: Aulen
 * @create: 2025-11-15
 */
public interface PmsProductAttributeService {

    /**
     * 根据商品分类ID查询属性列表
     *
     * @param productCategoryId 商品分类ID
     * @return 商品属性列表
     */
    List<PmsProductAttribute> listByProductCategoryId(Long productCategoryId);

    /**
     * 根据分类ID和类型查询商品属性
     *
     * @param cid  分类ID
     * @param type 属性类型(0:规格 1:参数)
     * @return 商品属性列表
     */
    List<PmsProductAttribute> listByAttributeCategoryIdAndType(Integer cid, Integer type);

    /**
     * 创建商品属性
     *
     * @param attribute 属性实体
     * @return 创建成功的属性ID
     */
    Long create(PmsProductAttribute attribute);

    /**
     * 批量删除商品属性
     *
     * @param ids 属性ID列表
     * @return 删除的记录数
     */
    Integer deleteBatch(List<Long> ids);
}
