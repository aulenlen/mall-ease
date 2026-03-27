package com.mallease.bff.service.category;

import com.mallease.bff.controller.portal.category.vo.CategoryTreeRespVO;

import java.util.List;

/**
 * 分类页聚合服务。
 *
 * @author: Aulen
 * @create: 2026-03-28
 */
public interface CategoryService {

    /**
     * 获取分类树和推荐商品
     */
    List<CategoryTreeRespVO> getCategoryTree();
}
