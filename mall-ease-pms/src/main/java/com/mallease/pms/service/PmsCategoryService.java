package com.mallease.pms.service;

import com.mallease.pms.dto.query.PmsCategoryQuery;
import com.mallease.pms.pojo.PmsCategory;

import java.util.List;
import java.util.Map;

/**
 * 商品分类服务接口
 * 提供分类的 CRUD、树形查询、物化路径维护等功能
 *
 * @author: Aulen
 * @create: 2025-12-13
 */
public interface PmsCategoryService {

    /**
     * 创建分类
     * 自动计算 path 和 level 字段
     * @param entity   分类实体（不含path/level，由Service计算）
     * @param parentId 父分类ID（0或null表示顶级分类）
     * @return 新分类ID
     */
    Long create(PmsCategory entity, Long parentId);

    /**
     * 更新分类
     *
     * @param entity      分类实体
     * @param newParentId 新的父分类ID（null表示不移动）
     * @return 影响行数
     */
    int update(PmsCategory entity, Long newParentId);

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
    PmsCategory getById(Long id);

    /**
     * 查询直接子分类
     *
     * @param parentId 父分类ID，0表示查询一级分类
     * @return 子分类列表
     */
    List<PmsCategory> listByParentId(Long parentId);

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
    List<PmsCategory> listDescendants(Long id);

    /**
     * 根据层级查询分类
     *
     * @param level 层级：0=一级，1=二级，2=三级
     * @return 分类列表
     */
    List<PmsCategory> listByLevel(Integer level);

    /**
     * 查询所有分类
     *
     * @return 所有分类列表
     */
    List<PmsCategory> listAll();

    /**
     * 按条件查询分类列表
     *
     * @param query 查询条件
     * @return 分类列表
     */
    List<PmsCategory> listByQuery(PmsCategoryQuery query);

    /**
     * 查询导航分类
     *
     * @return 导航分类列表
     */
    List<PmsCategory> listNavCategories();

    /**
     * 获取面包屑路径中的分类列表
     *
     * @param id 分类ID
     * @return 祖先分类列表（按层级排序）
     */
    List<PmsCategory> listAncestors(Long id);

    /**
     * 更新分类状态
     *
     * @param id     分类ID
     * @param status 状态：0-禁用，1-启用
     * @return 影响行数
     */
    int updateStatus(Long id, Integer status);

    /**
     * 批量更新分类状态
     *
     * @param ids    ID列表
     * @param status 状态：0-禁用，1-启用
     * @return 影响行数
     */
    int updateStatusBatch(List<Long> ids, Integer status);

    /**
     * 更新导航显示状态
     *
     * @param id    分类ID
     * @param isNav 是否导航显示：0-否，1-是
     * @return 影响行数
     */
    int updateNavStatus(Long id, Integer isNav);
}