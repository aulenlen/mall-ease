package com.mallease.product.converter;

import com.mallease.common.api.R;
import com.mallease.common.dto.remote.BrandDTO;
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
     * cmd → Entity
     */
    Brand cmdToEntity(BrandCmd cmd);

    /**
     * cmd → Entity
     */
    void updateEntityFromCmd(@MappingTarget Brand entity, BrandCmd cmd);


    List<BrandDTO> entityToDTO(List<Brand> brands);
}
