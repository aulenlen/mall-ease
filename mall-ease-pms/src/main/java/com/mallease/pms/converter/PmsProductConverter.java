package com.mallease.pms.converter;

import com.mallease.pms.dto.cmd.CreateProductCmd;
import com.mallease.pms.dto.cmd.UpdateProductCmd;
import com.mallease.pms.dto.vo.PmsProductDetailVO;
import com.mallease.pms.dto.vo.PmsProductListVO;
import com.mallease.pms.pojo.PmsProduct;
import org.mapstruct.*;

import java.util.List;

/**
 * 商品转换器
 *
 * @author: Aulen
 * @create: 2025-11-15
 */
@Mapper(componentModel = "spring")
public interface PmsProductConverter {

    // ========== Entity → VO ==========

    /**
     * Entity → ListVO（列表场景）
     */
    PmsProductListVO entityToListVo(PmsProduct entity);

    /**
     * Entity → DetailVO（详情场景）
     */
    @Mapping(target = "cateParentId", ignore = true)
    @Mapping(target = "memberPriceList", ignore = true)
    @Mapping(target = "preferenceAreaProductRelationList", ignore = true)
    @Mapping(target = "productAttributeValueList", ignore = true)
    @Mapping(target = "productFullReductionList", ignore = true)
    @Mapping(target = "productLadderList", ignore = true)
    @Mapping(target = "skuStockList", ignore = true)
    @Mapping(target = "subjectProductRelationList", ignore = true)
    PmsProductDetailVO entityToDetailVo(PmsProduct entity);

    // ========== 列表转换 ==========

    List<PmsProductListVO> entityListToListVoList(List<PmsProduct> entities);

    List<PmsProductDetailVO> entityListToDetailVoList(List<PmsProduct> entities);

    // ========== Command → Entity ==========

    /**
     * CreateCmd → Entity
     */
    @Mapping(target = "id", ignore = true)
    PmsProduct createCmdToEntity(CreateProductCmd cmd);

    /**
     * UpdateCmd → Entity（用于更新现有实体）
     */
    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromCmd(@MappingTarget PmsProduct entity, UpdateProductCmd cmd);
}
