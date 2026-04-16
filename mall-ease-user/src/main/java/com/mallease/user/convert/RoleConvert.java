package com.mallease.user.convert;

import com.mallease.user.controller.admin.role.vo.RoleReqVO;
import com.mallease.user.controller.admin.role.vo.RoleDetailRespVO;
import com.mallease.user.controller.admin.role.vo.RoleRespVO;
import com.mallease.user.dal.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * 角色转换器
 *
 * @author: Aulen
 * @create: 2026-01-23
 */
@Mapper(componentModel = "spring")
public interface RoleConvert {

    /**
     * Entity → VO
     */
    RoleRespVO toRoleResp(Role entity);

    /**
     * Entity List → VO List
     */
    List<RoleRespVO> toRoleRespList(List<Role> entities);

    /**
     * Entity → DetailVO
     */
    @Mapping(target = "resourceIds", ignore = true)
    @Mapping(target = "menuIds", ignore = true)
    RoleDetailRespVO toRoleDetailResp(Role entity);

    /**
     * Cmd → Entity
     */
    @Mapping(target = "adminCount", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    Role toRole(RoleReqVO cmd);

    /**
     * Cmd → Entity (更新)
     */
    @Mapping(target = "adminCount", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    void copyToRole(@MappingTarget Role entity, RoleReqVO cmd);
}
