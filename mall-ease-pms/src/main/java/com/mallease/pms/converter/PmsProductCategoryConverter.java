package com.mallease.pms.converter;

import com.mallease.pms.dto.cmd.CreateProductCategoryCmd;
import com.mallease.pms.dto.cmd.UpdateProductCategoryCmd;
import com.mallease.pms.dto.vo.PmsProductCategoryDetailVO;
import com.mallease.pms.dto.vo.PmsProductCategoryListVO;
import com.mallease.pms.dto.vo.PmsProductCategoryVO;
import com.mallease.pms.pojo.PmsProductCategory;
import org.mapstruct.*;

import java.util.List;

/**
 * 产品分类转换器
 *
 * @author: Aulen
 * @create: 2025-11-15
 */
@Mapper(componentModel = "spring")
public interface PmsProductCategoryConverter {

    // ========== Entity → VO ==========

    /**
     * Entity → VO（通用）
     */
    PmsProductCategoryVO entityToVo(PmsProductCategory entity);

    /**
     * Entity → ListVO（列表场景）
     */
    PmsProductCategoryListVO entityToListVo(PmsProductCategory entity);

    /**
     * Entity → DetailVO（详情场景）
     */
    PmsProductCategoryDetailVO entityToDetailVo(PmsProductCategory entity);

    // ========== 列表转换 ==========

    List<PmsProductCategoryVO> entityListToVoList(List<PmsProductCategory> entities);

    List<PmsProductCategoryListVO> entityListToListVoList(List<PmsProductCategory> entities);

    List<PmsProductCategoryDetailVO> entityListToDetailVoList(List<PmsProductCategory> entities);

    // ========== Command → Entity ==========

    /**
     * CreateCmd → Entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "productCount", ignore = true)
    PmsProductCategory createCmdToEntity(CreateProductCategoryCmd cmd);

    /**
     * UpdateCmd → Entity（用于更新现有实体）
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "productCount", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromCmd(@MappingTarget PmsProductCategory entity, UpdateProductCategoryCmd cmd);
}
