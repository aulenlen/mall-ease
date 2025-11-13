package com.mallease.cms.dao;

import com.mallease.cms.pojo.CmsSubject;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 专题表 Mapper 接口
 *
 * @author: Claude
 * @create: 2025-11-13
 */
@Mapper
public interface CmsSubjectDao {
    /**
     * 根据主键删除
     *
     * @param id 主键ID
     * @return 影响行数
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 插入记录
     *
     * @param record 记录
     * @return 影响行数
     */
    int insert(CmsSubject record);

    /**
     * 选择性插入记录
     *
     * @param record 记录
     * @return 影响行数
     */
    int insertSelective(CmsSubject record);

    /**
     * 根据主键查询
     *
     * @param id 主键ID
     * @return 记录
     */
    CmsSubject selectByPrimaryKey(Long id);

    /**
     * 根据主键选择性更新
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(CmsSubject record);

    /**
     * 根据主键更新
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKey(CmsSubject record);

    /**
     * 查询所有专题
     *
     * @return 专题列表
     */
    List<CmsSubject> selectAll();

    /**
     * 根据关键字查询专题列表
     *
     * @param keyword 关键字（专题标题模糊匹配）
     * @return 专题列表
     */
    List<CmsSubject> selectByKeyword(@Param("keyword") String keyword);

    /**
     * 根据分类ID查询专题列表
     *
     * @param categoryId 分类ID
     * @return 专题列表
     */
    List<CmsSubject> selectByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * 根据推荐状态查询专题列表
     *
     * @param recommendStatus 推荐状态：0->不推荐；1->推荐
     * @return 专题列表
     */
    List<CmsSubject> selectByRecommendStatus(@Param("recommendStatus") Integer recommendStatus);

    /**
     * 根据显示状态查询专题列表
     *
     * @param showStatus 显示状态：0->不显示；1->显示
     * @return 专题列表
     */
    List<CmsSubject> selectByShowStatus(@Param("showStatus") Integer showStatus);

    /**
     * 批量更新推荐状态
     *
     * @param ids 专题ID列表
     * @param recommendStatus 推荐状态：0->不推荐；1->推荐
     * @return 更新的记录数
     */
    int updateRecommendStatusBatch(@Param("ids") List<Long> ids, @Param("recommendStatus") Integer recommendStatus);

    /**
     * 批量更新显示状态
     *
     * @param ids 专题ID列表
     * @param showStatus 显示状态：0->不显示；1->显示
     * @return 更新的记录数
     */
    int updateShowStatusBatch(@Param("ids") List<Long> ids, @Param("showStatus") Integer showStatus);
}
