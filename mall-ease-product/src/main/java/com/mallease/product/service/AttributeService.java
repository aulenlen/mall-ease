package com.mallease.product.service;

import com.mallease.product.model.client.query.AttributeQuery;
import com.mallease.product.model.client.vo.TemplateApplyVO;
import com.mallease.product.model.client.vo.TemplatePreviewVO;
import com.mallease.product.model.data.entity.Attribute;
import com.mallease.product.model.data.entity.AttributeValue;
import com.mallease.product.model.data.entity.CategoryAttributeRelation;

import java.util.List;

/**
 * 商品属性服务接口（全局属性池）
 *
 * @author: Aulen
 * @create: 2026-01-11
 */
public interface AttributeService {

    // ==================== 属性池管理（全局） ====================

    /**
     * 创建全局属性
     */
    Long create(Attribute entity);

    /**
     * 更新属性
     */
    int update(Attribute entity);

    /**
     * 删除属性
     */
    int delete(Long id);

    /**
     * 批量删除属性
     */
    int deleteBatch(List<Long> ids);

    /**
     * 根据ID查询
     */
    Attribute getById(Long id);

    /**
     * 查询所有属性（属性池列表）
     */
    List<Attribute> list();

    /**
     * 条件查询属性（支持分页）
     */
    List<Attribute> list(AttributeQuery query);

    /**
     * 根据关键词搜索属性
     */
    List<Attribute> listByKeyword(String keyword);

    /**
     * 根据类型查询属性
     */
    List<Attribute> listByType(Integer type);

    /**
     * 查询可搜索的属性
     */
    List<Attribute> listSearchable();

    /**
     * 查询可筛选的属性
     */
    List<Attribute> listFilterable();

    /**
     * 根据ID列表查询
     */
    List<Attribute> listByIds(List<Long> ids);

    // ==================== 分类关联属性 ====================

    /**
     * 为分类关联属性
     */
    int bindCategory(CategoryAttributeRelation entity);

    /**
     * 批量为分类关联属性
     */
    int bindCategoryBatch(List<CategoryAttributeRelation> entities);

    /**
     * 解除分类与属性的关联
     */
    int unbindCategory(Long categoryId, Long attrId);

    /**
     * 更新分类-属性关联信息（分组、排序、选项）
     */
    int updateRelation(CategoryAttributeRelation entity);

    /**
     * 批量更新分类-属性关联信息
     *
     * @param entities 关联实体列表
     * @return 更新数量
     */
    int updateRelationBatch(List<CategoryAttributeRelation> entities);

    /**
     * 查询分类已关联的属性（含 groupName, sort, options）
     */
    List<CategoryAttributeRelation> listByCategory(Long categoryId);

    /**
     * 查询分类已关联的规格属性（type=1）
     */
    List<CategoryAttributeRelation> listSpecsByCategory(Long categoryId);

    /**
     * 查询分类已关联的参数属性（type=0）
     */
    List<CategoryAttributeRelation> listParamsByCategory(Long categoryId);

    /**
     * 查询分类未关联的属性（供勾选弹窗）
     */
    List<Attribute> listUnbindByCategory(Long categoryId);

    /**
     * 查询分类未关联的属性（支持分页）
     */
    List<Attribute> listUnbindByCategory(Long categoryId, AttributeQuery query);

    /**
     * 从父分类复制属性关联
     */
    int copyFromParent(Long parentCategoryId, Long childCategoryId);

    /**
     * 批量解绑属性
     *
     * @param categoryId 分类ID
     * @param attrIds    属性ID列表
     * @return 解绑数量
     */
    int unbindCategoryBatch(Long categoryId, List<Long> attrIds);

    /**
     * 模板预览 - 计算从模板分类复制属性的影响
     *
     * @param templateCategoryId 模板分类ID
     * @param targetCategoryId   目标分类ID（必须是叶子）
     * @param mode               replace/merge
     * @param scope              spec/param/both
     * @return 预览结果
     */
    TemplatePreviewVO templatePreview(Long templateCategoryId, Long targetCategoryId, String mode, String scope);

    /**
     * 模板应用 - 执行模板复制
     *
     * @param templateCategoryId   模板分类ID
     * @param targetCategoryId     目标分类ID（必须是叶子）
     * @param mode                 replace/merge
     * @param scope                spec/param/both
     * @param traceId              追踪ID（来自 preview）
     * @param selectedAddAttrIds   要新增的属性ID列表（为空则全部新增）
     * @return 应用结果
     */
    TemplateApplyVO templateApply(Long templateCategoryId, Long targetCategoryId, String mode, String scope,
                                   String traceId, List<Long> selectedAddAttrIds);

    // 属性值相关

    /**
     * 根据SPU ID列表查询参数属性值（sku_id 为 null）
     */
    List<AttributeValue> listParamValuesBySpuIds(List<Long> spuIds);

    /**
     * 获取属性选项（优先关联表选项，其次全局选项）
     */
    List<String> getAttrOptions(Long categoryId, Long attrId);
}