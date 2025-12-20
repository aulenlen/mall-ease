package com.mallease.pms.service;

import com.mallease.pms.pojo.PmsProductAttribute;
import com.mallease.pms.pojo.PmsProductAttributeCategory;

import java.util.List;
import java.util.Map;

/**
 * 商品属性分类服务接口
 *
 * @author: Aulen
 * @create: 2025-11-12
 */
public interface PmsProductAttributeCategoryService {

    /**
     * 查询所有分类
     *
     * @return 分类列表
     */
    List<PmsProductAttributeCategory> listAll();

    /**
     * 根据分类ID列表查询关联的属性
     *
     * @param categoryIds 分类ID列表
     * @return 分类ID -> 属性列表的映射
     */
    Map<Long, List<PmsProductAttribute>> getAttributesByCategoryIds(List<Long> categoryIds);

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
     *
     * @param name 分类名称
     * @return 影响行数
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
