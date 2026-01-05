package com.mallease.product.converter;

import com.mallease.product.model.client.cmd.BrandCmd;
import com.mallease.product.model.client.vo.BrandDetailVO;

import com.mallease.product.model.client.vo.BrandListVO;
import com.mallease.product.model.data.entity.Brand;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * 品牌转换器
 *
 * @author: Aulen
 * @create: 2025-11-15
 */
@Mapper(componentModel = "spring")
public interface BrandConverter {

    /**
     * Entity → ListVO
     */
    BrandListVO entityToListVo(Brand entity);

    /**
     * Entity → DetailVO
     */
    BrandDetailVO entityToDetailVo(Brand entity);
    List<BrandListVO> entityListToListVoList(List<Brand> entities);

    /**
     * SaveCmd → Entity
     */
    Brand saveCmdToEntity(BrandCmd cmd);

    /**
     * SaveCmd → Entity
     */
    void updateEntityFromCmd(@MappingTarget Brand entity, BrandCmd cmd);
}
