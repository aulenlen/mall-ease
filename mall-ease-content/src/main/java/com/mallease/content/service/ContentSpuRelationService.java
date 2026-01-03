package com.mallease.content.service;

import java.util.List;

/**
 * 商品关联服务（专题、优选专区）
 *
 * @author: Aulen
 * @create: 2025-12-20
 */
public interface ContentSpuRelationService {

    /**
     * 批量绑定商品到专题
     *
     * @param spuId  商品ID（SPU ID）
     * @param subjectIds 专题ID列表
     * @return 绑定数量
     */
    int bindSubjects(Long spuId, List<Long> subjectIds);

    /**
     * 批量绑定商品到优选专区
     *
     * @param spuId          商品ID（SPU ID）
     * @param preferenceAreaIds  优选专区ID列表
     * @return 绑定数量
     */
    int bindPreferenceAreas(Long spuId, List<Long> preferenceAreaIds);

    /**
     * 解绑商品的所有专题关联
     *
     * @param spuId 商品ID
     * @return 解绑数量
     */
    int unbindSubjects(Long spuId);

    /**
     * 解绑商品的所有优选专区关联
     *
     * @param spuId 商品ID
     * @return 解绑数量
     */
    int unbindPreferenceAreas(Long spuId);
}