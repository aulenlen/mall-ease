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
}
