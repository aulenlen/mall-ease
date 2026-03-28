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
    AdminRespVO toAdminResp(Admin entity);

    /**
     * Entity List -> RespVO List
     */
    List<AdminRespVO> toAdminRespList(List<Admin> entities);

    /**
     * Entity -> DTO
     */
    AdminDTO toAdminRemote(Admin entity);

    /**
     * ReqVO -> Entity
     */
    Admin toAdmin(AdminReqVO cmd);

    /**
     * ReqVO -> Entity (更新)
     */
    void copyToAdmin(@MappingTarget Admin entity, AdminReqVO cmd);
}