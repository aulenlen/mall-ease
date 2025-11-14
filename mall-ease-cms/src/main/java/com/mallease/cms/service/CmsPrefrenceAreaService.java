package com.mallease.cms.service;

import com.mallease.cms.pojo.CmsPrefrenceArea;
import com.mallease.cms.pojo.CmsPrefrenceAreaProductRelation;

import java.util.List;

/**
 * 优选专区管理 Service
 *
 * @author: Claude
 * @create: 2025-11-13
 */
public interface CmsPrefrenceAreaService {
    /**
     * 创建优选专区
     *
     * @param prefrenceArea 优选专区信息
     * @return 创建记录数
     */
    int create(CmsPrefrenceArea prefrenceArea);

    /**
     * 更新优选专区
     *
     * @param id 优选专区ID
     * @param prefrenceArea 优选专区信息
     * @return 更新记录数
     */
    int update(Long id, CmsPrefrenceArea prefrenceArea);

    /**
     * 删除优选专区
     *
     * @param id 优选专区ID
     * @return 删除记录数
     */
    int delete(Long id);

    /**
     * 批量删除优选专区
     *
     * @param ids 优选专区ID列表
     * @return 删除记录数
     */
    int deleteBatch(List<Long> ids);

    /**
     * 根据ID获取优选专区详情
     *
     * @param id 优选专区ID
     * @return 优选专区信息
     */
    CmsPrefrenceArea getById(Long id);

    /**
     * 查询所有优选专区
     *
     * @return 优选专区列表
     */
    List<CmsPrefrenceArea> listAll();

    /**
     * 根据名称查询优选专区列表
     *
     * @param name 名称（模糊匹配）
     * @return 优选专区列表
     */
    List<CmsPrefrenceArea> listByName(String name);

    /**
     * 根据显示状态查询优选专区列表
     *
     * @param showStatus 显示状态：0->不显示；1->显示
     * @return 优选专区列表
     */
    List<CmsPrefrenceArea> listByShowStatus(Integer showStatus);

    /**
     * 批量更新显示状态
     *
     * @param ids 优选专区ID列表
     * @param showStatus 显示状态：0->不显示；1->显示
     * @return 更新记录数
     */
    int updateShowStatusBatch(List<Long> ids, Integer showStatus);

    /**
     * 批量添加优选专区商品关联
     *
     * @param relationList 关联列表
     * @return 添加的记录数
     */
    int batchAddProductRelation(List<CmsPrefrenceAreaProductRelation> relationList);

    /**
     * 根据商品ID查询优选专区商品关联列表
     *
     * @param productId 商品ID
     * @return 关联列表
     */
    List<CmsPrefrenceAreaProductRelation> getRelationsByProductId(Long productId);

    /**
     * 根据商品ID删除优选专区商品关联
     *
     * @param productId 商品ID
     * @return 删除的记录数
     */
    int deleteRelationsByProductId(Long productId);
}
