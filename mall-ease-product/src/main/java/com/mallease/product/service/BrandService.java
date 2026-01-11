package com.mallease.product.service;

import com.mallease.common.api.R;
import com.mallease.common.dto.remote.BrandDTO;
import com.mallease.product.model.data.entity.Brand;

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
     * @param keyword 品牌名关键字（可选，为空时查询所有）
     * @return 品牌列表
     */
    List<Brand> list(String keyword);

    /**
     * 创建品牌
     *
     * @param brand 品牌信息
     * @return 创建成功的品牌ID
     */
    Long create(Brand brand);

    /**
     * 根据ID获取品牌详情
     *
     * @param id 品牌ID
     * @return 品牌信息
     */
    Brand getById(Long id);

    /**
     * 更新品牌
     *
     * @param brand 品牌信息
     * @return 更新的记录数
     */
    int update(Brand brand);

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
}
