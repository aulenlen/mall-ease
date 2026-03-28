package com.mallease.user.convert;

import com.mallease.common.dto.remote.AddressDTO;
import com.mallease.user.controller.portal.address.vo.AddressReqVO;
import com.mallease.user.controller.portal.address.vo.AddressRespVO;
import com.mallease.user.dal.entity.MemberAddress;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * 收货地址对象转换器
 *
 * @author: Aulen
 * @create: 2026-03-11
 */
@Mapper(componentModel = "spring")
public interface AddressConvert {

    /**
     * Cmd → Entity（新增时使用）
     */
    @Mapping(target = "memberId", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updater", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    MemberAddress toAddress(AddressReqVO cmd);

    /**
     * Cmd 更新到 Entity（更新时使用）
     */
    @Mapping(target = "memberId", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updater", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    void copyToAddress(@MappingTarget MemberAddress entity, AddressReqVO cmd);

    /**
     * Entity → VO（对外响应）
     */
    AddressRespVO toAddressResp(MemberAddress entity);

    /**
     * Entity List → VO List
     */
    List<AddressRespVO> toAddressRespList(List<MemberAddress> entities);

    /**
     * Entity → DTO（服务间调用）
     */
    AddressDTO toAddressRemote(MemberAddress entity);

    /**
     * Entity List → DTO List
     */
    List<AddressDTO> toAddressRemoteList(List<MemberAddress> entities);
}
