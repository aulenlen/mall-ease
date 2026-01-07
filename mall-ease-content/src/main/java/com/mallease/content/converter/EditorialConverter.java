package com.mallease.content.converter;

import com.mallease.content.model.client.cmd.EditorialCmd;
import com.mallease.content.model.client.vo.EditorialVO;
import com.mallease.content.model.data.entity.Editorial;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * 编辑精选转换器
 *
 * @author: Aulen
 * @create: 2026-01-07
 */
@Mapper(componentModel = "spring")
public interface EditorialConverter {

    /**
     * Cmd 转 Entity
     */
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "publishTime", ignore = true)
    Editorial cmdToEntity(EditorialCmd cmd);

    /**
     * Entity 转 VO
     */
    EditorialVO entityToVo(Editorial entity);

    /**
     * Entity 列表转 VO 列表
     */
    List<EditorialVO> entityListToVoList(List<Editorial> entityList);
}