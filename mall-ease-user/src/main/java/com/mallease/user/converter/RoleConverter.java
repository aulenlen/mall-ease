package com.mallease.user.converter;

import com.mallease.user.model.client.cmd.RoleCmd;
import com.mallease.user.model.client.vo.RoleDetailVO;
import com.mallease.user.model.client.vo.RoleVO;
import com.mallease.user.model.data.Role;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * 角色转换器
 *
 * @author: Aulen
 * @create: 2026-01-23
 */
@Mapper(componentModel = "spring")
public interface RoleConverter {

    /**
     * Entity → VO
     */
    RoleVO entityToVo(Role entity);

    /**
     * Entity List → VO List
     */
    List<RoleVO> entityListToVoList(List<Role> entities);

    /**
     * Entity → DetailVO
     */
    RoleDetailVO entityToDetailVo(Role entity);

    /**
     * Cmd → Entity
     */
    Role cmdToEntity(RoleCmd cmd);

    /**
     * Cmd → Entity (更新)
     */
    void updateEntityFromCmd(@MappingTarget Role entity, RoleCmd cmd);
}