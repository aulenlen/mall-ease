package com.mallease.pms.service;

import com.mallease.pms.dto.request.PmsProductCategoryCreateRequest;
import com.mallease.pms.dto.request.PmsProductCategoryUpdateRequest;
import com.mallease.pms.dto.response.PmsProductCategoryWithChildrenResponse;
import com.mallease.pms.pojo.PmsProductCategory;

import java.util.List;

/**
 * @author: Aulen
 * @description: 商品分类服务接口
 * @create: 2025-11-12
 **/
public interface PmsProductCategoryService {
    /**
     * 创建商品分类
     *
     * @param request 创建请求参数
     * @return 创建后的商品分类信息
     */
    Integer create(PmsProductCategoryCreateRequest request);
    /**
     * 根据父级ID查询商品分类
     *
     * @param parentId 父级ID
     * @return 商品分类列表
     */
    List<PmsProductCategory> listByParentId(Long parentId);

    /**
     * 批量更新导航栏显示状态
     *
     * @param ids       分类ID列表
     * @param navStatus 导航栏显示状态（0->不显示；1->显示）
     * @return 更新的记录数
     */
    int updateNavStatusBatch(List<Long> ids, Integer navStatus);

    /**
     * 批量更新显示状态
     *
     * @param ids        分类ID列表
     * @param showStatus 显示状态（0->不显示；1->显示）
     * @return 更新的记录数
     */
    int updateShowStatusBatch(List<Long> ids, Integer showStatus);

    /**
     * 根据ID获取商品分类
     *
     * @param id 分类ID
     * @return 商品分类信息
     */
    PmsProductCategory getById(Long id);

    /**
     * 更新商品分类
     *
     * @param id      分类ID
     * @param request 更新请求参数
     * @return 影响行数
     */
    Integer update(Long id, PmsProductCategoryUpdateRequest request);

    /**
     * 删除商品分类
     *
     * @param id 分类ID
     * @return 影响行数
     */
    Integer delete(Long id);

    /**
     * 查询所有一级分类及其子分类
     *
     * @return 一级分类及子分类列表
     */
    List<PmsProductCategoryWithChildrenResponse> listWithChildren();
}

