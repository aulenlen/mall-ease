package com.mallease.product.converter;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.mallease.product.model.client.cmd.AttributeCmd;
import com.mallease.product.model.client.cmd.CategoryAttrRelationCmd;
import com.mallease.product.model.client.vo.AttributeVO;
import com.mallease.product.model.client.vo.CategoryAttributeVO;
import com.mallease.product.model.data.entity.Attribute;
import com.mallease.product.model.data.entity.CategoryAttributeRelation;
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
 * 属性转换器（全局属性池）
 *
 * @author: Aulen
 * @create: 2026-01-11
 */
@Mapper(componentModel = "spring")
public interface AttributeConverter {

    // Attribute 转换

    /**
     * Cmd → Entity
     */
    @Mapping(target = "deleted", constant = "0")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(source = "options", target = "options", qualifiedByName = "optionsListToJson")
    Attribute cmdToEntity(AttributeCmd cmd);

    /**
     * 更新 Entity（用于 update 场景，保留 id）
     */
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(source = "options", target = "options", qualifiedByName = "optionsListToJson")
    Attribute cmdToEntityForUpdate(AttributeCmd cmd);

    /**
     * 更新 Entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(source = "options", target = "options", qualifiedByName = "optionsListToJson")
    void updateEntityFromCmd(@MappingTarget Attribute entity, AttributeCmd cmd);

    /**
     * Entity → VO
     */
    @Mapping(source = "options", target = "optionList", qualifiedByName = "parseOptions")
    AttributeVO entityToVo(Attribute entity);

    /**
     * Entity List → VO List
     */
    List<AttributeVO> entityListToVoList(List<Attribute> entities);

    // CategoryAttributeRelation 转换

    /**
     * Cmd → Entity（分类-属性关联）
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(source = "options", target = "options", qualifiedByName = "optionsListToJson")
    @Mapping(target = "sort", expression = "java(cmd.getSort() != null ? cmd.getSort() : 0)")
    @Mapping(target = "required", expression = "java(cmd.getRequired() != null ? cmd.getRequired() : 0)")
    CategoryAttributeRelation relationCmdToEntity(CategoryAttrRelationCmd cmd);

    /**
     * 批量转换 Cmd → Entity（分类-属性关联）
     */
    default List<CategoryAttributeRelation> relationCmdListToEntityList(Long categoryId, List<CategoryAttrRelationCmd> cmdList) {
        if (CollectionUtils.isEmpty(cmdList)) {
            return Collections.emptyList();
        }
        return cmdList.stream()
                .map(cmd -> {
                    CategoryAttributeRelation entity = relationCmdToEntity(cmd);
                    entity.setCategoryId(categoryId);
                    return entity;
                })
                .toList();
    }

    // 工具方法

    /**
     * 解析 options JSON 为 List
     */
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

    /**
     * 将 options List 转为 JSON 字符串
     */
    @Named("optionsListToJson")
    default String optionsListToJson(List<String> options) {
        if (CollectionUtils.isEmpty(options)) {
            return null;
        }
        return JSONUtil.toJsonStr(options);
    }

    /**
     * 构建分类属性视图对象（关联表 + 属性表）
     */
    default CategoryAttributeVO buildCategoryAttributeVO(CategoryAttributeRelation relation, Attribute attr) {
        if (attr == null) {
            return null;
        }

        CategoryAttributeVO vo = new CategoryAttributeVO();
        vo.setRelationId(relation.getId());
        vo.setAttrId(attr.getId());
        vo.setAttrName(attr.getName());
        vo.setType(attr.getType());
        vo.setUnit(attr.getUnit());
        vo.setEntryMethod(attr.getEntryMethod());
        vo.setSearchable(attr.getSearchable());
        vo.setFilterable(attr.getFilterable());

        // 关联表字段
        vo.setGroupName(relation.getGroupName());
        vo.setSort(relation.getSort());
        vo.setRequired(relation.getRequired());
        vo.setGlobalOptions(attr.getOptions());
        vo.setCategoryOptions(relation.getOptions());

        // 计算有效选项列表（优先关联表选项）
        String effectiveOptions = StrUtil.isNotBlank(relation.getOptions()) ? relation.getOptions() : attr.getOptions();
        if (StrUtil.isNotBlank(effectiveOptions)) {
            vo.setOptionList(JSONUtil.toList(effectiveOptions, String.class));
        } else {
            vo.setOptionList(new ArrayList<>());
        }

        return vo;
    }

    /**
     * 批量构建分类属性视图对象
     */
    default List<CategoryAttributeVO> buildCategoryAttributeVOList(List<CategoryAttributeRelation> relations, List<Attribute> attributes) {
        if (CollectionUtils.isEmpty(relations)) {
            return Collections.emptyList();
        }

        Map<Long, Attribute> attrMap = attributes.stream()
                .collect(Collectors.toMap(Attribute::getId, a -> a));

        return relations.stream()
                .map(relation -> buildCategoryAttributeVO(relation, attrMap.get(relation.getAttrId())))
                .filter(vo -> vo != null)
                .toList();
    }
}