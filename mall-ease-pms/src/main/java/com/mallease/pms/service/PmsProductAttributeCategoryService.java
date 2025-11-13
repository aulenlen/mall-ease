package com.mallease.pms.service;

import com.mallease.pms.dto.response.ProductAttributeCategoryItemResponse;
import com.mallease.pms.pojo.PmsProductAttributeCategory;

import java.util.List;

/**
 * @author: Aulen
 * @description:
 * @create: 2025-11-12 22:32
 **/
public interface PmsProductAttributeCategoryService {
    List<ProductAttributeCategoryItemResponse> getCategoryWithAttrList();

    /**
     * 分页查询所有商品属性分类
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 商品属性分类列表
     */
    List<PmsProductAttributeCategory> list(Integer pageNum, Integer pageSize);

    /**
     * 更新商品属性分类
     *
     * @param id   分类ID
     * @param name 分类名称
     * @return 影响行数
     */
    Integer update(Long id, String name);

    /**
     * 添加属性分类
     * @param name
     * @return
     */
    int create(String name);

    /**
     * 删除商品属性分类
     *
     * @param id 分类ID
     * @return 影响行数
     */
    Integer delete(Long id);
}
