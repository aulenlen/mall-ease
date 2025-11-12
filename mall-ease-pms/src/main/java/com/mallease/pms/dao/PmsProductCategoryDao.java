package com.mallease.pms.dao;

import com.mallease.pms.pojo.PmsProductCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 产品分类 Mapper 接口
 *
 * @author: Aulen
 * @create: 2025-11-09
 */
@Mapper
public interface PmsProductCategoryDao {
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
    int insert(PmsProductCategory record);

    /**
     * 选择性插入记录
     *
     * @param record 记录
     * @return 影响行数
     */
    int insertSelective(PmsProductCategory record);

    /**
     * 根据主键查询
     *
     * @param id 主键ID
     * @return 记录
     */
    PmsProductCategory selectByPrimaryKey(Long id);

    /**
     * 根据主键选择性更新
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(PmsProductCategory record);

    /**
     * 根据主键更新
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKey(PmsProductCategory record);

    /**
     * 根据父级ID查询
     *
     * @param parentId 父级ID
     * @return 记录列表
     */
    List<PmsProductCategory> selectByParentId(@Param("parentId") Long parentId);

    /**
     * 根据级别查询
     *
     * @param level 分类级别：0->1级；1->2级
     * @return 记录列表
     */
    List<PmsProductCategory> selectByLevel(@Param("level") Integer level);

    /**
     * 查询所有记录
     *
     * @return 记录列表
     */
    List<PmsProductCategory> selectAll();

    /**
     * 批量更新导航栏显示状态
     *
     * @param ids       分类ID列表
     * @param navStatus 导航栏显示状态（0->不显示；1->显示）
     * @return 更新的记录数
     */
    int updateNavStatusBatch(@Param("ids") List<Long> ids, @Param("navStatus") Integer navStatus);

    /**
     * 批量更新显示状态
     *
     * @param ids        分类ID列表
     * @param showStatus 显示状态（0->不显示；1->显示）
     * @return 更新的记录数
     */
    int updateShowStatusBatch(@Param("ids") List<Long> ids, @Param("showStatus") Integer showStatus);
}


