package com.mallease.cms.service;

import com.mallease.cms.pojo.CmsSubject;

import java.util.List;

/**
 * 专题服务接口
 *
 * @author: Claude
 * @create: 2025-11-13
 */
public interface CmsSubjectService {
    /**
     * 创建专题
     *
     * @param subject 专题信息
     * @return 影响行数
     */
    int create(CmsSubject subject);

    /**
     * 更新专题
     *
     * @param id 专题ID
     * @param subject 专题信息
     * @return 影响行数
     */
    int update(Long id, CmsSubject subject);

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
    CmsSubject getById(Long id);

    /**
     * 获取所有专题列表
     *
     * @return 专题列表
     */
    List<CmsSubject> list();

    /**
     * 根据关键字获取专题列表
     *
     * @param keyword 关键字（专题标题模糊匹配）
     * @return 专题列表
     */
    List<CmsSubject> listByKeyword(String keyword);

    /**
     * 根据分类ID获取专题列表
     *
     * @param categoryId 分类ID
     * @return 专题列表
     */
    List<CmsSubject> listByCategoryId(Long categoryId);

    /**
     * 获取推荐专题列表
     *
     * @return 推荐专题列表
     */
    List<CmsSubject> listRecommend();

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
}
