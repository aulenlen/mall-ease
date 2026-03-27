package com.mallease.user.convert;

import com.mallease.user.controller.admin.memberlevel.vo.MemberLevelReqVO;
import com.mallease.user.controller.admin.memberlevel.vo.MemberLevelRespVO;
import com.mallease.user.dal.entity.MemberLevel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * 会员等级转换器
 *
 * @author: Aulen
 * @create: 2026-03-28
 */
@Mapper(componentModel = "spring")
public interface MemberLevelConvert {

    /**
     * ReqVO -> Entity
     */
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    MemberLevel reqVOToEntity(MemberLevelReqVO reqVO);

    /**
     * ReqVO -> Entity（更新）
     */
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    void updateEntityFromReqVO(@MappingTarget MemberLevel entity, MemberLevelReqVO reqVO);

    /**
     * Entity -> RespVO
     */
    MemberLevelRespVO entityToRespVO(MemberLevel entity);

    /**
     * Entity List -> RespVO List
     */
    List<MemberLevelRespVO> entityListToRespVOList(List<MemberLevel> entities);
}