package com.mallease.product.dal.mapper;

import com.mallease.product.dal.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品分类 Mapper 接口
 * 说明：使用物化路径（Materialized Path）优化树形结构查询
 *
 * @author: Aulen
 * @create: 2025-12-11
 */
@Mapper
public interface CategoryDao {

    /**
     * 根据主键查询
     *
     * @param id 主键ID
     * @return 分类记录
     */
    Category selectByPrimaryKey(Long id);

    /**
     * 根据ID列表批量查询
     *
     * @param ids ID列表
     * @return 分类列表
     */
    List<Category> selectByIds(@Param("ids") List<Long> ids);

    /**
     * 根据父ID查询直接子分类
     *
     * @param parentId 父分类ID
     * @return 子分类列表
     */
    List<Category> selectByParentId(@Param("parentId") Long parentId);

    /**
     * 批量查询子分类（根据多个父ID）
     *
     * @param parentIds 父分类ID列表
     * @return 子分类列表
     */
    List<Category> selectByParentIds(@Param("parentIds") List<Long> parentIds);

    /**
     * 根据路径前缀查询所有子孙分类（物化路径查询）
     * 例如：pathPrefix = '/1/' 查询ID为1的分类的所有子孙
     *
     * @param pathPrefix 路径前缀
     * @return 子孙分类列表
     */
    List<Category> selectByPathPrefix(@Param("pathPrefix") String pathPrefix);

    /**
     * 根据层级查询
     *
     * @param level 层级：0=一级，1=二级，2=三级
     * @return 分类列表
     */
    List<Category> selectByLevel(@Param("level") Integer level);

    /**
     * 根据启用状态查询
     *
     * @param enableStatus 启用状态：0-禁用 1-启用
     * @return 分类列表
     */
    List<Category> selectByEnableStatus(@Param("enableStatus") Integer enableStatus);

    /**
     * 查询导航分类
     *
     * @return 分类列表
     */
    List<Category> selectNavCategories();

    /**
     * 根据名称模糊查询
     *
     * @param name 分类名称
     * @return 分类列表
     */
    List<Category> selectByNameLike(@Param("name") String name);

    /**
     * 查询所有
     *
     * @return 分类列表
     */
    List<Category> selectAll();

    /**
     * 插入记录
     *
     * @param record 分类记录
     * @return 影响行数
     */
    int insert(Category record);

    /**
     * 选择性插入记录
     *
     * @param record 分类记录
     * @return 影响行数
     */
    int insertSelective(Category record);

    /**
     * 根据主键更新
     *
     * @param record 分类记录
     * @return 影响行数
     */
    int updateByPrimaryKey(Category record);

    /**
     * 根据主键选择性更新
     *
     * @param record 分类记录
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(Category record);

    /**
     * 批量更新启用状态
     *
     * @param ids          ID列表
     * @param enableStatus 启用状态
     * @return 影响行数
     */
    int updateEnableStatusBatch(@Param("ids") List<Long> ids, @Param("enableStatus") Integer enableStatus);

    /**
     * 批量更新路径（移动分类时使用）
     *
     * @param oldPathPrefix 旧路径前缀
     * @param newPathPrefix 新路径前缀
     * @param levelDiff 层级变化（正数表示层级加深）
     * @return 影响行数
     */
    int updatePathBatch(@Param("oldPathPrefix") String oldPathPrefix,
                        @Param("newPathPrefix") String newPathPrefix,
                        @Param("levelDiff") Integer levelDiff);

    /**
     * 逻辑删除
     *
     * @param id 主键ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);

    /**
     * 批量逻辑删除
     *
     * @param ids ID列表
     * @return 影响行数
     */
    int deleteBatch(@Param("ids") List<Long> ids);
}
