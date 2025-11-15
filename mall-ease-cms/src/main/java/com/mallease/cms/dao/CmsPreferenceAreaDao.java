package com.mallease.cms.dao;

import com.mallease.cms.pojo.CmsPreferenceArea;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 优选专区表 Mapper 接口
 *
 * @author: Aulen
 * @create: 2025-11-13
 */
@Mapper
public interface CmsPreferenceAreaDao {
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
    int insert(CmsPreferenceArea record);

    /**
     * 选择性插入记录
     *
     * @param record 记录
     * @return 影响行数
     */
    int insertSelective(CmsPreferenceArea record);

    /**
     * 根据主键查询
     *
     * @param id 主键ID
     * @return 记录
     */
    CmsPreferenceArea selectByPrimaryKey(Long id);

    /**
     * 根据主键选择性更新
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(CmsPreferenceArea record);

    /**
     * 根据主键更新
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKey(CmsPreferenceArea record);

    /**
     * 查询所有优选专区
     *
     * @return 优选专区列表
     */
    List<CmsPreferenceArea> selectAll();

    /**
     * 根据名称查询优选专区列表
     *
     * @param name 名称（模糊匹配）
     * @return 优选专区列表
     */
    List<CmsPreferenceArea> selectByName(@Param("name") String name);

    /**
     * 根据显示状态查询优选专区列表
     *
     * @param showStatus 显示状态：0->不显示；1->显示
     * @return 优选专区列表
     */
    List<CmsPreferenceArea> selectByShowStatus(@Param("showStatus") Integer showStatus);

    /**
     * 批量更新显示状态
     *
     * @param ids 优选专区ID列表
     * @param showStatus 显示状态：0->不显示；1->显示
     * @return 更新的记录数
     */
    int updateShowStatusBatch(@Param("ids") List<Long> ids, @Param("showStatus") Integer showStatus);

    /**
     * 批量删除优选专区
     *
     * @param ids 优选专区ID列表
     * @return 删除的记录数
     */
    int deleteBatch(@Param("ids") List<Long> ids);
}
