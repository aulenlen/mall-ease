package com.mallease.user.converter;

import com.mallease.user.model.client.cmd.AdminCmd;
import com.mallease.user.model.client.vo.AdminVO;
import com.mallease.user.model.data.Admin;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * 管理员对象转换器
 *
 * @author: Aulen
 * @create: 2026-01-25
 */
@Mapper(componentModel = "spring")
public interface AdminConverter {

    /**
     * Entity → VO
     */
    AdminVO entityToVo(Admin entity);

    /**
     * Entity List → VO List
     */
    List<AdminVO> entityListToVoList(List<Admin> entities);

    /**
     * Cmd → Entity
     */
    Admin cmdToEntity(AdminCmd cmd);

    /**
     * Cmd → Entity (更新)
     */
    void updateEntityFromCmd(@MappingTarget Admin entity, AdminCmd cmd);
}
