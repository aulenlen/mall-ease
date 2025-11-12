package com.mallease.pms.service;

import com.mallease.pms.pojo.PmsBrand;

import java.util.List;

/**
 * @author: Aulen
 * @description:
 * @create: 2025-11-10 18:43
 **/
public interface PmsBrandService {
    /**
     * 获取品牌列表（支持模糊搜索品牌名）
     *
     * @param keyword 品牌名关键字（可选，为空时查询所有）
     * @return 品牌列表
     */
    List<PmsBrand> list(String keyword);

    /**
     * 创建品牌
     *
     * @param brand 品牌信息
     * @return 创建的品牌信息
     */
    PmsBrand create(PmsBrand brand);

    /**
     * 根据ID获取品牌详情
     *
     * @param id 品牌ID
     * @return 品牌信息
     */
    PmsBrand getById(Long id);

    /**
     * 更新品牌
     *
     * @param brand 品牌信息
     * @return 更新后的品牌信息
     */
    PmsBrand update(PmsBrand brand);

    /**
     * 删除品牌
     *
     * @param id 品牌ID
     */
    void delete(Long id);

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
}
