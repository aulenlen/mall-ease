package com.mallease.product.convert.attribute;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.mallease.product.controller.admin.attribute.vo.AttributeRespVO;
import com.mallease.product.controller.admin.attribute.vo.AttributeSaveReqVO;
import com.mallease.product.controller.admin.attribute.vo.CategoryAttributeRelationRespVO;
import com.mallease.product.controller.admin.attribute.vo.CategoryAttributeRelationSaveReqVO;
import com.mallease.product.dal.entity.Attribute;
import com.mallease.product.dal.entity.CategoryAttributeRelation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 属性转换器
 */
@Mapper(componentModel = "spring")
public interface AttributeConvert {

    @Mapping(target = "deleted", constant = "0")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(source = "options", target = "options", qualifiedByName = "optionsListToJson")
    Attribute reqVOToEntity(AttributeSaveReqVO reqVO);

    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(source = "options", target = "options", qualifiedByName = "optionsListToJson")
    void copyToEntity(@MappingTarget Attribute entity, AttributeSaveReqVO reqVO);

    @Mapping(source = "options", target = "optionList", qualifiedByName = "parseOptions")
    AttributeRespVO entityToRespVO(Attribute entity);

    List<AttributeRespVO> entityListToRespVOList(List<Attribute> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(source = "options", target = "options", qualifiedByName = "optionsListToJson")
    @Mapping(target = "sort", expression = "java(reqVO.getSort() != null ? reqVO.getSort() : 0)")
    @Mapping(target = "required", expression = "java(reqVO.getRequired() != null ? reqVO.getRequired() : 0)")
    CategoryAttributeRelation relationReqVOToEntity(CategoryAttributeRelationSaveReqVO reqVO);

    default List<CategoryAttributeRelation> relationReqVOListToEntityList(Long categoryId, List<CategoryAttributeRelationSaveReqVO> reqVOList) {
        if (CollectionUtils.isEmpty(reqVOList)) {
            return Collections.emptyList();
        }
        return reqVOList.stream()
                .map(reqVO -> {
                    CategoryAttributeRelation entity = relationReqVOToEntity(reqVO);
                    entity.setCategoryId(categoryId);
                    return entity;
                })
                .toList();
    }

    @Named("parseOptions")
    default List<String> parseOptions(String options) {
        if (options == null || options.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return JSONUtil.toList(options, String.class);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Named("optionsListToJson")
    default String optionsListToJson(List<String> options) {
        if (CollectionUtils.isEmpty(options)) {
            return null;
        }
        return JSONUtil.toJsonStr(options);
    }

    default CategoryAttributeRelationRespVO buildCategoryAttributeRelationRespVO(CategoryAttributeRelation relation, Attribute attr) {
        if (attr == null) {
            return null;
        }
        CategoryAttributeRelationRespVO vo = new CategoryAttributeRelationRespVO();
        vo.setRelationId(relation.getId());
        vo.setAttrId(attr.getId());
        vo.setAttrName(attr.getName());
        vo.setType(attr.getType());
        vo.setUnit(attr.getUnit());
        vo.setEntryMethod(attr.getEntryMethod());
        vo.setSearchable(attr.getSearchable());
        vo.setFilterable(attr.getFilterable());
        vo.setGroupName(relation.getGroupName());
        vo.setSort(relation.getSort());
        vo.setRequired(relation.getRequired());
        vo.setGlobalOptions(attr.getOptions());
        vo.setCategoryOptions(relation.getOptions());

        String effectiveOptions = StrUtil.isNotBlank(relation.getOptions()) ? relation.getOptions() : attr.getOptions();
        if (StrUtil.isNotBlank(effectiveOptions)) {
            vo.setOptionList(JSONUtil.toList(effectiveOptions, String.class));
        } else {
            vo.setOptionList(new ArrayList<>());
        }
        return vo;
    }

    default List<CategoryAttributeRelationRespVO> buildCategoryAttributeRelationRespVOList(List<CategoryAttributeRelation> relations,
                                                                                           List<Attribute> attributes) {
        if (CollectionUtils.isEmpty(relations)) {
            return Collections.emptyList();
        }
        Map<Long, Attribute> attrMap = attributes.stream().collect(Collectors.toMap(Attribute::getId, attribute -> attribute));
        return relations.stream()
                .map(relation -> buildCategoryAttributeRelationRespVO(relation, attrMap.get(relation.getAttrId())))
                .filter(vo -> vo != null)
                .toList();
    }
}