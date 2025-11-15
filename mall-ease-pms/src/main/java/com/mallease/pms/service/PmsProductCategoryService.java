package com.mallease.pms.service;

import com.mallease.pms.dto.cmd.CreateProductCategoryCmd;
import com.mallease.pms.dto.cmd.UpdateProductCategoryCmd;
import com.mallease.pms.dto.vo.PmsProductCategoryWithChildrenVO;
import com.mallease.pms.pojo.PmsProductCategory;

import java.util.List;

/**
 * 商品分类服务接口
 *
 * @author: Aulen
 * @create: 2025-11-15
 */
public interface PmsProductCategoryService {

    /**
     * 创建商品分类
     *
     * @param cmd 创建命令
     * @return 创建结果
     */
    Integer create(CreateProductCategoryCmd cmd);

    /**
     * 根据父级ID查询商品分类
     *
     * @param parentId 父级ID
     * @return 商品分类列表
     */
    List<PmsProductCategory> listByParentId(Long parentId);

    /**
     * 批量更新导航栏显示状态
     *
     * @param ids       分类ID列表
     * @param navStatus 导航栏显示状态(0:不显示 1:显示)
     * @return 更新的记录数
     */
    int updateNavStatusBatch(List<Long> ids, Integer navStatus);

    /**
     * 批量更新显示状态
     *
     * @param ids        分类ID列表
     * @param showStatus 显示状态(0:不显示 1:显示)
     * @return 更新的记录数
     */
    int updateShowStatusBatch(List<Long> ids, Integer showStatus);

    /**
     * 根据ID获取商品分类
     *
     * @param id 分类ID
     * @return 商品分类信息
     */
    PmsProductCategory getById(Long id);

    /**
     * 更新商品分类
     *
     * @param id  分类ID
     * @param cmd 更新命令
     * @return 影响行数
     */
    Integer update(Long id, UpdateProductCategoryCmd cmd);

    /**
     * 删除商品分类
     *
     * @param id 分类ID
     * @return 影响行数
     */
    Integer delete(Long id);

    /**
     * 查询所有一级分类及其子分类
     *
     * @return 一级分类及子分类列表
     */
    List<PmsProductCategoryWithChildrenVO> listWithChildren();
}

