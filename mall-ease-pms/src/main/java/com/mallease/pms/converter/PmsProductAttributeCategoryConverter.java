package com.mallease.pms.converter;

import com.mallease.pms.dto.vo.PmsProductAttributeCategoryItemVO;
import com.mallease.pms.dto.vo.PmsProductAttributeCategoryListVO;
import com.mallease.pms.pojo.PmsProductAttributeCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * 商品属性分类转换器
 *
 * @author: Aulen
 * @create: 2025-11-15
 */
@Mapper(componentModel = "spring", uses = PmsProductAttributeConverter.class)
public interface PmsProductAttributeCategoryConverter {

    /**
     * Entity → ListVO
     *
     * @param entity 实体对象
     * @return 列表VO对象
     */
    PmsProductAttributeCategoryListVO entityToListVo(PmsProductAttributeCategory entity);

    /**
     * Entity列表 → ListVO列表
     *
     * @param entityList 实体列表
     * @return VO列表
     */
    List<PmsProductAttributeCategoryListVO> entityListToListVoList(List<PmsProductAttributeCategory> entityList);

    /**
     * Entity → ItemVO（用于带属性列表的分类项）
     *
     * @param entity 实体对象
     * @return ItemVO对象
     */
    @Mapping(target = "productAttributeList", ignore = true)
    PmsProductAttributeCategoryItemVO entityToItemVo(PmsProductAttributeCategory entity);

    /**
     * Entity列表 → ItemVO列表
     *
     * @param entityList 实体列表
     * @return ItemVO列表
     */
    List<PmsProductAttributeCategoryItemVO> entityListToItemVoList(List<PmsProductAttributeCategory> entityList);
}
