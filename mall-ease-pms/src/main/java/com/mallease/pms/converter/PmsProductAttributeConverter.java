package com.mallease.pms.converter;

import com.mallease.pms.dto.cmd.CreateProductAttributeCmd;
import com.mallease.pms.dto.cmd.UpdateProductAttributeCmd;
import com.mallease.pms.dto.vo.PmsProductAttributeCategoryItemVO;
import com.mallease.pms.dto.vo.PmsProductAttributeListVO;
import com.mallease.pms.dto.vo.PmsProductAttributeVO;
import com.mallease.pms.pojo.PmsProductAttribute;
import org.mapstruct.*;

import java.util.List;

/**
 * 商品属性转换器
 *
 * @author: Aulen
 * @create: 2025-11-15
 */
@Mapper(componentModel = "spring")
public interface PmsProductAttributeConverter {

    // ========== Entity → VO ==========

    /**
     * Entity → VO（通用）
     */
    PmsProductAttributeVO entityToVo(PmsProductAttribute entity);

    /**
     * Entity → ListVO（列表场景）
     */
    @Mapping(source = "selectType", target = "selectTypeName", qualifiedByName = "selectTypeToName")
    @Mapping(source = "inputType", target = "inputTypeName", qualifiedByName = "inputTypeToName")
    @Mapping(source = "filterType", target = "filterTypeName", qualifiedByName = "filterTypeToName")
    @Mapping(source = "searchType", target = "searchTypeName", qualifiedByName = "searchTypeToName")
    @Mapping(source = "type", target = "typeName", qualifiedByName = "typeToName")
    PmsProductAttributeListVO entityToListVo(PmsProductAttribute entity);

    // ========== 列表转换 ==========

    List<PmsProductAttributeVO> entityListToVoList(List<PmsProductAttribute> entities);

    List<PmsProductAttributeListVO> entityListToListVoList(List<PmsProductAttribute> entities);

    /**
     * Entity → ProductAttributeItemVO（用于分类项中的属性列表）
     */
    PmsProductAttributeCategoryItemVO.ProductAttributeItemVO entityToItemVo(PmsProductAttribute entity);

    List<PmsProductAttributeCategoryItemVO.ProductAttributeItemVO> entityListToItemVoList(List<PmsProductAttribute> entities);

    // ========== Command → Entity ==========

    /**
     * CreateCmd → Entity
     */
    @Mapping(target = "id", ignore = true)
    PmsProductAttribute createCmdToEntity(CreateProductAttributeCmd cmd);

    /**
     * UpdateCmd → Entity（用于更新现有实体）
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "productAttributeCategoryId", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromCmd(@MappingTarget PmsProductAttribute entity, UpdateProductAttributeCmd cmd);

    // ========== 自定义映射方法 ==========

    /**
     * 属性选择类型转中文
     */
    @Named("selectTypeToName")
    default String selectTypeToName(Integer selectType) {
        if (selectType == null) {
            return "未知";
        }
        switch (selectType) {
            case 0:
                return "唯一";
            case 1:
                return "单选";
            case 2:
                return "多选";
            default:
                return "未知";
        }
    }

    /**
     * 属性录入方式转中文
     */
    @Named("inputTypeToName")
    default String inputTypeToName(Integer inputType) {
        if (inputType == null) {
            return "未知";
        }
        return inputType == 0 ? "手工录入" : "从列表中选取";
    }

    /**
     * 分类筛选样式转中文
     */
    @Named("filterTypeToName")
    default String filterTypeToName(Integer filterType) {
        if (filterType == null) {
            return "未知";
        }
        return filterType == 0 ? "普通" : "颜色";
    }

    /**
     * 检索类型转中文
     */
    @Named("searchTypeToName")
    default String searchTypeToName(Integer searchType) {
        if (searchType == null) {
            return "未知";
        }
        switch (searchType) {
            case 0:
                return "不需要进行检索";
            case 1:
                return "关键字检索";
            case 2:
                return "范围检索";
            default:
                return "未知";
        }
    }

    /**
     * 属性类型转中文
     */
    @Named("typeToName")
    default String typeToName(Integer type) {
        if (type == null) {
            return "未知";
        }
        return type == 0 ? "规格" : "参数";
    }
}
