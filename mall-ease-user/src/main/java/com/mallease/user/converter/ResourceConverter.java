package com.mallease.user.converter;

import com.mallease.user.model.client.cmd.SaveResourceCmd;
import com.mallease.user.model.client.vo.ResourceVO;
import com.mallease.user.model.data.Resource;
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
public interface ResourceConverter {

    /**
     * Entity → VO
     */
    ResourceVO entityToVo(Resource entity);

    /**
     * Entity List → VO List
     */
    List<ResourceVO> entityListToVoList(List<Resource> entities);

    /**
     * Cmd → Entity
     */
    Resource cmdToEntity(SaveResourceCmd cmd);

    /**
     * Cmd → Entity (更新)
     */
    void updateEntityFromCmd(@MappingTarget Resource entity, SaveResourceCmd cmd);
}