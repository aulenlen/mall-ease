package com.mallease.pms.service;

import com.mallease.pms.dto.cmd.CreateProductAttributeCmd;
import com.mallease.pms.dto.vo.PmsProductAttributeRelationVO;
import com.mallease.pms.pojo.PmsProductAttribute;

import java.util.List;

/**
 * 商品属性服务接口
 *
 * @author: Aulen
 * @create: 2025-11-15
 */
public interface PmsProductAttributeService {

    /**
     * 获取商品属性信息
     *
     * @param productCategoryId 商品分类ID
     * @return 商品属性关联列表
     */
    List<PmsProductAttributeRelationVO> getProductAttrInfo(Long productCategoryId);

    /**
     * 根据分类ID和类型查询商品属性
     *
     * @param cid  分类ID
     * @param type 属性类型(0:规格 1:参数)
     * @return 商品属性列表
     */
    List<PmsProductAttribute> listByAttributeCategoryIdAndType(Integer cid, Integer type);

    /**
     * 创建商品属性
     *
     * @param cmd 创建命令
     * @return 创建后的商品属性信息
     */
    PmsProductAttribute create(CreateProductAttributeCmd cmd);

    /**
     * 批量删除商品属性
     *
     * @param ids 属性ID列表
     * @return 删除的记录数
     */
    Integer deleteBatch(List<Long> ids);
}
