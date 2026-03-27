package com.mallease.user.convert;

import com.mallease.common.dto.remote.ResourceDTO;
import com.mallease.user.controller.admin.resource.vo.ResourceReqVO;
import com.mallease.user.controller.admin.resource.vo.ResourceRespVO;
import com.mallease.user.dal.entity.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * 资源转换器
 *
 * @author: Aulen
 * @create: 2026-01-23
 */
@Mapper(componentModel = "spring")
public interface ResourceConvert {

    /**
     * Entity -> RespVO
     */
    ResourceRespVO entityToRespVO(Resource entity);

    /**
     * Entity List -> RespVO List
     */
    List<ResourceRespVO> entityListToRespVOList(List<Resource> entities);

    /**
     * Entity -> DTO
     */
    ResourceDTO entityToDTO(Resource entity);

    /**
     * Entity List -> DTO List
     */
    List<ResourceDTO> entityListToDTOList(List<Resource> entities);

    /**
     * ReqVO -> Entity
     */
    Resource reqVOToEntity(ResourceReqVO cmd);

    /**
     * ReqVO -> Entity (更新)
     */
    void updateEntityFromReqVO(@MappingTarget Resource entity, ResourceReqVO cmd);
}