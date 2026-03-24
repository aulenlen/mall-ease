package com.mallease.product.service.attribute;

import com.mallease.product.controller.admin.attribute.vo.AttributePageReqVO;
import com.mallease.product.controller.admin.attribute.vo.AttributeRelationBatchUnbindReqVO;
import com.mallease.product.controller.admin.attribute.vo.AttributeSaveReqVO;
import com.mallease.product.controller.admin.attribute.vo.AttributeTemplateApplyReqVO;
import com.mallease.product.controller.admin.attribute.vo.AttributeTemplateApplyRespVO;
import com.mallease.product.controller.admin.attribute.vo.AttributeTemplatePreviewReqVO;
import com.mallease.product.controller.admin.attribute.vo.AttributeTemplatePreviewRespVO;
import com.mallease.product.controller.admin.attribute.vo.CategoryAttributeRelationSaveReqVO;
import com.mallease.product.dal.entity.Attribute;
import com.mallease.product.dal.entity.AttributeValue;
import com.mallease.product.dal.entity.CategoryAttributeRelation;

import java.util.List;

/**
 * 管理商品属性、类目绑定关系和属性模板能力。
 */
public interface AttributeService {

    /**
     * 创建属性定义。
     */
    Long create(AttributeSaveReqVO reqVO);

    /**
     * 更新属性定义。
     */
    int update(AttributeSaveReqVO reqVO);

    /**
     * 删除单个属性。
     */
    int delete(Long id);

    /**
     * 批量删除属性。
     */
    int deleteBatch(List<Long> ids);

    /**
     * 查询单个属性详情。
     */
    Attribute get(Long id);

    /**
     * 查询全部属性。
     */
    List<Attribute> list();

    /**
     * 按后台筛选条件分页查询属性。
     */
    List<Attribute> page(AttributePageReqVO reqVO);

    /**
     * 按关键字模糊查询属性。
     */
    List<Attribute> listByKeyword(String keyword);

    /**
     * 按属性类型查询。
     */
    List<Attribute> listByType(Integer type);

    /**
     * 查询可参与搜索的属性。
     */
    List<Attribute> listSearchable();

    /**
     * 查询可参与筛选的属性。
     */
    List<Attribute> listFilterable();

    /**
     * 按 ID 批量查询属性。
     */
    List<Attribute> listByIds(List<Long> ids);

    /**
     * 绑定单个类目与属性关系。
     */
    int bindCategory(CategoryAttributeRelationSaveReqVO reqVO);

    /**
     * 批量绑定类目与属性关系。
     */
    int bindCategoryBatch(Long categoryId, List<CategoryAttributeRelationSaveReqVO> reqVOList);

    /**
     * 解绑单个类目与属性关系。
     */
    int unbindCategory(Long categoryId, Long attrId);

    /**
     * 更新单个类目属性关系配置。
     */
    int updateRelation(CategoryAttributeRelationSaveReqVO reqVO);

    /**
     * 批量更新类目属性关系配置。
     */
    int updateRelationBatch(Long categoryId, List<CategoryAttributeRelationSaveReqVO> reqVOList);

    /**
     * 查询类目下全部属性关系。
     */
    List<CategoryAttributeRelation> listByCategory(Long categoryId);

    /**
     * 查询类目下规格属性关系。
     */
    List<CategoryAttributeRelation> listSpecsByCategory(Long categoryId);

    /**
     * 查询类目下参数属性关系。
     */
    List<CategoryAttributeRelation> listParamsByCategory(Long categoryId);

    /**
     * 查询当前类目下尚未绑定的属性。
     */
    List<Attribute> listUnbindByCategory(Long categoryId);

    /**
     * 按分页条件查询当前类目下尚未绑定的属性。
     */
    List<Attribute> listUnbindByCategory(Long categoryId, AttributePageReqVO reqVO);

    /**
     * 将父类目属性配置复制到子类目。
     */
    int copyFromParent(Long parentCategoryId, Long childCategoryId);

    /**
     * 批量解绑类目属性关系。
     */
    int unbindCategoryBatch(AttributeRelationBatchUnbindReqVO reqVO);

    /**
     * 预览属性模板应用结果，不落库。
     */
    AttributeTemplatePreviewRespVO templatePreview(AttributeTemplatePreviewReqVO reqVO);

    /**
     * 应用属性模板到目标类目。
     */
    AttributeTemplateApplyRespVO templateApply(AttributeTemplateApplyReqVO reqVO);

    /**
     * 查询 SPU 参数型属性值，供快照和搜索构建使用。
     */
    List<AttributeValue> listParamValuesBySpuIds(List<Long> spuIds);

    /**
     * 查询属性可选项，供后台表单回显和选择使用。
     */
    List<String> getAttrOptions(Long categoryId, Long attrId);
}
