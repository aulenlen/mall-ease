package com.mallease.user.converter;

import com.mallease.common.dto.remote.MemberDTO;
import com.mallease.user.model.client.vo.MemberVO;
import com.mallease.user.model.data.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * 会员对象转换器
 *
 * @author: Aulen
 * @create: 2026-01-28
 */
@Mapper(componentModel = "spring")
public interface MemberConverter {

    /**
     * Entity → DTO（内部调用）
     */
    MemberDTO entityToDTO(Member entity);

    /**
     * Entity List → DTO List（内部调用）
     */
    List<MemberDTO> entityListToDTOList(List<Member> entities);

    /**
     * DTO → Entity（注册时使用）
     */
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    Member dtoToEntity(MemberDTO dto);

    /**
     * Entity → VO（对外响应）
     */
    MemberVO entityToVO(Member entity);
}
