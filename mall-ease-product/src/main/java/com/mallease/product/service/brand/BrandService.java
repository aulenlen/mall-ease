package com.mallease.product.service.brand;

import com.mallease.common.dto.remote.BrandDTO;
import com.mallease.product.controller.admin.brand.vo.BrandPageReqVO;
import com.mallease.product.controller.admin.brand.vo.BrandRelationBatchUnbindReqVO;
import com.mallease.product.controller.admin.brand.vo.BrandSaveReqVO;
import com.mallease.product.controller.admin.brand.vo.CategoryBrandRelationSaveReqVO;
import com.mallease.product.dal.entity.Brand;

import java.util.List;

/**
 * @author: Aulen
 * @description:
 * @create: 2025-11-10 18:43
 **/
public interface BrandService {
    /**
     * 获取品牌列表（支持模糊搜索品牌名）
     *
     * @param reqVO 查询条件
     * @return 品牌列表
     */
    List<Brand> page(BrandPageReqVO reqVO);

    /**
     * 创建品牌
     *
     * @param reqVO 品牌信息
     * @return 创建成功的品牌ID
     */
    Long create(BrandSaveReqVO reqVO);

    /**
     * 根据ID获取品牌详情
     *
     * @param id 品牌ID
     * @return 品牌信息
     */
    Brand get(Long id);

    /**
     * 更新品牌
     *
     * @param reqVO 品牌信息
     * @return 更新的记录数
     */
    int update(BrandSaveReqVO reqVO);

    /**
     * 删除品牌
     *
     * @param id 品牌ID
     */
    int delete(Long id);

    /**
     * 批量更新品牌显示状态
     *
     * @param ids        品牌ID列表
     * @param showStatus 显示状态（0->隐藏；1->显示）
     * @return 更新的记录数
     */
    int updateShowStatusBatch(List<Long> ids, Integer showStatus);

    /**
     * 批量更新品牌厂家制造商状态
     *
     * @param ids            品牌ID列表
     * @param factoryStatus 厂家制造商状态（0->不是；1->是）
     * @return 更新的记录数
     */
    int updateFactoryStatusBatch(List<Long> ids, Integer factoryStatus);

    /**
     * 根据ID列表批量获取品牌
     *
     * @param ids 品牌ID列表
     * @return 品牌列表
     */
    List<Brand> listByIds(List<Long> ids);

    /**
     * 获取showstatus为启用的品牌列表
     * @return 品牌列表
     */
    List<BrandDTO> listEnabledBrands();

    // ==================== 分类关联品牌 ====================

    /**
     * 为分类关联品牌
     */
    int bindCategory(CategoryBrandRelationSaveReqVO reqVO);

    /**
     * 批量为分类关联品牌
     */
    int bindCategoryBatch(Long categoryId, List<Long> brandIds);

    /**
     * 解除分类与品牌的关联
     */
    int unbindCategory(Long categoryId, Long brandId);

    /**
     * 查询分类已关联的品牌
     */
    List<Brand> listByCategory(Long categoryId);

    /**
     * 查询分类未关联的品牌（供勾选弹窗）
     */
    List<Brand> listUnbindByCategory(Long categoryId);

    /**
     * 从父分类复制品牌关联
     */
    int copyFromParent(Long parentCategoryId, Long childCategoryId);

    /**
     * 批量解绑品牌
     *
     * @param categoryId 分类ID
     * @param brandIds   品牌ID列表
     * @return 解绑数量
     */
    int unbindCategoryBatch(BrandRelationBatchUnbindReqVO reqVO);
}
