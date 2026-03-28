package com.mallease.content.convert.editorial;

import com.mallease.common.dto.remote.EditorialDTO;
import com.mallease.content.controller.admin.editorial.vo.EditorialReqVO;
import com.mallease.content.controller.admin.editorial.vo.EditorialRespVO;
import com.mallease.content.dal.entity.Editorial;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface EditorialConvert {

    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "publishTime", ignore = true)
    Editorial toEditorial(EditorialReqVO reqVO);

    @Mapping(target = "spuIds", ignore = true)
    EditorialRespVO toEditorialResp(Editorial entity);

    default List<EditorialRespVO> toEditorialRespList(List<Editorial> entityList) {
        if (entityList == null || entityList.isEmpty()) {
            return Collections.emptyList();
        }
        return entityList.stream().map(this::toEditorialResp).toList();
    }

    @Mapping(target = "spuIds", ignore = true)
    EditorialDTO toEditorialRemote(Editorial entity);

    default List<EditorialDTO> toEditorialRemoteList(List<Editorial> entityList) {
        if (entityList == null || entityList.isEmpty()) {
            return Collections.emptyList();
        }
        return entityList.stream().map(this::toEditorialRemote).toList();
    }
}
