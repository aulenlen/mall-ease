package com.mallease.content.service;

import com.mallease.content.model.data.entity.Subject;
import com.mallease.content.model.data.entity.SubjectSpuRelation;

import java.util.List;

/**
 * 专题服务接口
 *
 * @author: Aulen
 * @create: 2025-11-13
 */
public interface SubjectService {
    /**
     * 创建专题
     *
     * @param subject 专题信息
     * @return 影响行数
     */
    int create(Subject subject);

    /**
     * 更新专题
     *
     * @param id 专题ID
     * @param subject 专题信息
     * @return 影响行数
     */
    int update(Long id, Subject subject);

    /**
     * 删除专题
     *
     * @param id 专题ID
     * @return 影响行数
     */
    int delete(Long id);

    /**
     * 根据ID获取专题
     *
     * @param id 专题ID
     * @return 专题信息
     */
    Subject getById(Long id);

    /**
     * 获取所有专题列表
     *
     * @return 专题列表
     */
    List<Subject> list();

    /**
     * 根据关键字获取专题列表
     *
     * @param keyword 关键字（专题标题模糊匹配）
     * @return 专题列表
     */
    List<Subject> listByKeyword(String keyword);

    /**
     * 根据分类ID获取专题列表
     *
     * @param categoryId 分类ID
     * @return 专题列表
     */
    List<Subject> listByCategoryId(Long categoryId);

    /**
     * 获取推荐专题列表
     *
     * @return 推荐专题列表
     */
    List<Subject> listRecommend();

    /**
     * 批量更新推荐状态
     *
     * @param ids 专题ID列表
     * @param recommendStatus 推荐状态：0->不推荐；1->推荐
     * @return 更新的记录数
     */
    int updateRecommendStatusBatch(List<Long> ids, Integer recommendStatus);

    /**
     * 批量更新显示状态
     *
     * @param ids 专题ID列表
     * @param showStatus 显示状态：0->不显示；1->显示
     * @return 更新的记录数
     */
    int updateShowStatusBatch(List<Long> ids, Integer showStatus);

    /**
     * 批量添加专题商品关联
     *
     * @param relationList 关联列表
     * @return 添加的记录数
     */
    int batchAddSpuRelation(List<SubjectSpuRelation> relationList);

    /**
     * 根据商品ID查询专题商品关联列表
     *
     * @param spuId 商品ID
     * @return 关联列表
     */
    List<SubjectSpuRelation> getRelationsBySpuId(Long spuId);

    /**
     * 根据商品ID删除专题商品关联
     *
     * @param spuId 商品ID
     * @return 删除的记录数
     */
    int deleteRelationsBySpuId(Long spuId);
}
