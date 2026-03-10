package com.mallease.user.converter;

import com.mallease.common.dto.remote.AddressDTO;
import com.mallease.user.model.client.cmd.AddressCmd;
import com.mallease.user.model.client.vo.AddressVO;
import com.mallease.user.model.data.MemberAddress;
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
public interface AddressConverter {

    /**
     * Cmd → Entity（新增时使用）
     */
    @Mapping(target = "memberId", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updater", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    MemberAddress cmdToEntity(AddressCmd cmd);

    /**
     * Cmd 更新到 Entity（更新时使用）
     */
    @Mapping(target = "memberId", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updater", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    void updateEntityFromCmd(@MappingTarget MemberAddress entity, AddressCmd cmd);

    /**
     * Entity → VO（对外响应）
     */
    AddressVO entityToVo(MemberAddress entity);

    /**
     * Entity List → VO List
     */
    List<AddressVO> entityListToVoList(List<MemberAddress> entities);

    /**
     * Entity → DTO（服务间调用）
     */
    AddressDTO entityToDTO(MemberAddress entity);

    /**
     * Entity List → DTO List
     */
    List<AddressDTO> entityListToDTOList(List<MemberAddress> entities);
}
