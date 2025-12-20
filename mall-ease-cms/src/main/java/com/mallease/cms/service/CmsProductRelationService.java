package com.mallease.cms.service;

import java.util.List;

/**
 * 商品关联服务（专题、优选专区）
 *
 * @author: Aulen
 * @create: 2025-12-20
 */
public interface CmsProductRelationService {

    /**
     * 批量绑定商品到专题
     *
     * @param productId  商品ID（SPU ID）
     * @param subjectIds 专题ID列表
     * @return 绑定数量
     */
    int bindSubjects(Long productId, List<Long> subjectIds);

    /**
     * 批量绑定商品到优选专区
     *
     * @param productId          商品ID（SPU ID）
     * @param preferenceAreaIds  优选专区ID列表
     * @return 绑定数量
     */
    int bindPreferenceAreas(Long productId, List<Long> preferenceAreaIds);

    /**
     * 解绑商品的所有专题关联
     *
     * @param productId 商品ID
     * @return 解绑数量
     */
    int unbindSubjects(Long productId);

    /**
     * 解绑商品的所有优选专区关联
     *
     * @param productId 商品ID
     * @return 解绑数量
     */
    int unbindPreferenceAreas(Long productId);
}