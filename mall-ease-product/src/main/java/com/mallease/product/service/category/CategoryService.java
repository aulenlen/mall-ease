package com.mallease.product.service.category;

import com.mallease.common.dto.remote.CategoryDTO;
import com.mallease.common.dto.remote.CategoryTreeDTO;
import com.mallease.product.controller.admin.category.vo.CategoryConfigSnapshotRespVO;
import com.mallease.product.controller.admin.category.vo.CategoryQueryReqVO;
import com.mallease.product.controller.admin.category.vo.CategorySaveReqVO;
import com.mallease.product.dal.entity.Category;

import java.util.List;
import java.util.Map;

/**
 * 商品分类服务接口
 * 提供分类的 CRUD、树形查询、物化路径维护等功能
 *
 * @author: Aulen
 * @create: 2025-12-13
 */
public interface CategoryService {

    /**
     * 创建分类
     * 自动计算 path 和 level 字段
     * @param reqVO 分类保存请求
     * @return 新分类ID
     */
    Long create(CategorySaveReqVO reqVO);

    /**
     * 更新分类
     *
     * @param reqVO 分类保存请求
     * @return 影响行数
     */
    int update(CategorySaveReqVO reqVO);

    /**
     * 删除分类
     *
     * @param id 分类ID
     * @return 影响行数
     */
    int delete(Long id);

    /**
     * 批量删除分类
     *
     * @param ids ID列表
     * @return 影响行数
     */
    int deleteBatch(List<Long> ids);

    /**
     * 根据ID查询分类
     *
     * @param id 分类ID
     * @return 分类实体
     */
    Category getById(Long id);

    /**
     * 查询直接子分类
     *
     * @param parentId 父分类ID，0表示查询一级分类
     * @return 子分类列表
     */
    List<Category> listByParentId(Long parentId);

    /**
     * 批量统计子分类数量
     *
     * @param parentIds 父分类ID列表
     * @return 父分类ID -> 子分类数量的映射
     */
    Map<Long, Long> countChildrenByParentIds(List<Long> parentIds);

    /**
     * 查询所有子孙分类（使用物化路径）
     *
     * @param id 分类ID
     * @return 所有子孙分类列表
     */
    List<Category> listDescendants(Long id);

    /**
     * 根据层级查询分类
     *
     * @param level 层级：0=一级，1=二级，2=三级
     * @return 分类列表
     */
    List<Category> listByLevel(Integer level);

    /**
     * 查询所有分类
     *
     * @return 所有分类列表
     */
    List<Category> listAll();

    /**
     * 按条件查询分类列表
     *
     * @param reqVO 查询条件
     * @return 分类列表
     */
    List<Category> listByQuery(CategoryQueryReqVO reqVO);

    /**
     * 获取面包屑路径中的分类列表
     *
     * @param id 分类ID
     * @return 祖先分类列表（按层级排序）
     */
    List<Category> listAncestors(Long id);

    /**
     * 更新分类启用状态
     *
     * @param id           分类ID
     * @param enableStatus 启用状态：0-禁用，1-启用
     * @return 影响行数
     */
    int updateEnableStatus(Long id, Integer enableStatus);

    /**
     * 批量更新分类启用状态
     *
     * @param ids          ID列表
     * @param enableStatus 启用状态：0-禁用，1-启用
     * @return 影响行数
     */
    int updateEnableStatusBatch(List<Long> ids, Integer enableStatus);

    /**
     * 更新导航显示状态
     *
     * @param id    分类ID
     * @param isNav 是否导航显示：0-否，1-是
     * @return 影响行数
     */
    int updateNavStatus(Long id, Integer isNav);

    /**
     * 根据ID列表批量获取分类
     *
     * @param ids 分类ID列表
     * @return 分类列表
     */
    List<Category> listByIds(List<Long> ids);

    /**
     * 前台app分类树（带缓存）
     * @return 分类树 DTO 列表
     */
    List<CategoryTreeDTO> portalTree();

    /**
     * 查询导航分类（带缓存）
     *
     * @return 导航分类 DTO 列表
     */
    List<CategoryDTO> listNavCategories();

    /**
     * 获取分类快照（聚合接口）
     * 一次请求返回 category + specs + params + brands
     *
     * @param categoryId 分类ID
     * @return 聚合快照
     */
    CategoryConfigSnapshotRespVO getCategoryConfigSnapshot(Long categoryId);
}
