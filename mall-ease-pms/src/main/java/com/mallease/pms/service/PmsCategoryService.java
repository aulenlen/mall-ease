package com.mallease.pms.service;

import com.mallease.pms.dto.cmd.CreatePmsCategoryCmd;
import com.mallease.pms.dto.cmd.UpdatePmsCategoryCmd;
import com.mallease.pms.dto.query.PmsCategoryQuery;
import com.mallease.pms.dto.vo.PmsCategoryDetailVO;
import com.mallease.pms.dto.vo.PmsCategoryListVO;
import com.mallease.pms.dto.vo.PmsCategoryTreeVO;

import java.util.List;

/**
 * 商品分类服务接口
 * <p>
 * 提供分类的 CRUD、树形查询、物化路径维护等功能
 *
 * @author: Aulen
 * @create: 2025-12-13
 */
public interface PmsCategoryService {

    /**
     * 创建分类
     * <p>
     * 自动计算 path 和 level 字段
     *
     * @param cmd 创建命令
     * @return 新分类ID
     */
    Long create(CreatePmsCategoryCmd cmd);

    /**
     * 更新分类
     * <p>
     * 注意：修改 parentId 会触发分类移动，批量更新所有子孙的 path
     *
     * @param cmd 更新命令
     * @return 影响行数
     */
    int update(UpdatePmsCategoryCmd cmd);

    /**
     * 删除分类
     * <p>
     * 前置检查：
     * 1. 是否有子分类（有则禁止删除）
     * 2. 是否有关联商品（有则禁止删除）
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
     * 根据ID查询分类详情
     * <p>
     * 包含：基础信息 + 父分类名称 + 面包屑 + 关联的规格组/参数组ID
     *
     * @param id 分类ID
     * @return 分类详情
     */
    PmsCategoryDetailVO getById(Long id);

    /**
     * 查询直接子分类
     *
     * @param parentId 父分类ID，0表示查询一级分类
     * @return 子分类列表
     */
    List<PmsCategoryListVO> listByParentId(Long parentId);

    /**
     * 查询所有子孙分类（使用物化路径）
     *
     * @param id 分类ID
     * @return 所有子孙分类列表
     */
    List<PmsCategoryListVO> listDescendants(Long id);

    /**
     * 根据层级查询分类
     *
     * @param level 层级：0=一级，1=二级，2=三级
     * @return 分类列表
     */
    List<PmsCategoryListVO> listByLevel(Integer level);


    /**
     * 获取完整分类树
     * <p>
     * 适用场景：后台分类管理、分类选择器
     *
     * @return 完整分类树
     */
    List<PmsCategoryTreeVO> getFullTree();

    /**
     * 获取分类树（支持筛选条件）
     *
     * @param query 查询条件
     * @return 分类树
     */
    List<PmsCategoryTreeVO> getTree(PmsCategoryQuery query);

    /**
     * 获取导航分类树
     * <p>
     * 只返回 isNav=1 的分类，适用于前台导航栏
     *
     * @return 导航分类树
     */
    List<PmsCategoryTreeVO> getNavTree();

    /**
     * 获取面包屑路径
     * <p>
     * 从根分类到当前分类的完整路径
     *
     * @param id 分类ID
     * @return 面包屑列表（按层级排序）
     */
    List<PmsCategoryDetailVO.BreadcrumbItem> getBreadcrumb(Long id);

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