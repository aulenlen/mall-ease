package com.mallease.user.convert;

import com.mallease.common.dto.remote.AdminDTO;
import com.mallease.user.controller.admin.user.vo.AdminReqVO;
import com.mallease.user.controller.admin.user.vo.AdminRespVO;
import com.mallease.user.dal.entity.Admin;
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
public interface AdminConvert {

    /**
     * Entity -> RespVO
     */
    AdminRespVO entityToRespVO(Admin entity);

    /**
     * Entity List -> RespVO List
     */
    List<AdminRespVO> entityListToRespVOList(List<Admin> entities);

    /**
     * Entity -> DTO
     */
    AdminDTO entityToDTO(Admin entity);

    /**
     * ReqVO -> Entity
     */
    Admin reqVOToEntity(AdminReqVO cmd);

    /**
     * ReqVO -> Entity (更新)
     */
    void updateEntityFromReqVO(@MappingTarget Admin entity, AdminReqVO cmd);
}