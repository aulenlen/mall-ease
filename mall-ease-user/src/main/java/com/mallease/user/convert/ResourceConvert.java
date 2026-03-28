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
    ResourceRespVO toResourceResp(Resource entity);

    /**
     * Entity List -> RespVO List
     */
    List<ResourceRespVO> toResourceRespList(List<Resource> entities);

    /**
     * Entity -> DTO
     */
    ResourceDTO toResourceRemote(Resource entity);

    /**
     * Entity List -> DTO List
     */
    List<ResourceDTO> toResourceRemoteList(List<Resource> entities);

    /**
     * ReqVO -> Entity
     */
    Resource toResource(ResourceReqVO cmd);

    /**
     * ReqVO -> Entity (更新)
     */
    void copyToResource(@MappingTarget Resource entity, ResourceReqVO cmd);
}