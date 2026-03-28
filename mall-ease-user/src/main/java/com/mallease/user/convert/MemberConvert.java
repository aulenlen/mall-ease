package com.mallease.user.convert;

import com.mallease.common.dto.remote.MemberDTO;
import com.mallease.user.controller.portal.member.vo.MemberRespVO;
import com.mallease.user.dal.entity.Member;
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
public interface MemberConvert {

    /**
     * Entity → DTO（内部调用）
     */
    MemberDTO toMemberRemote(Member entity);

    /**
     * Entity List → DTO List（内部调用）
     */
    List<MemberDTO> toMemberRemoteList(List<Member> entities);

    /**
     * DTO → Entity（注册时使用）
     */
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    Member toMember(MemberDTO dto);

    /**
     * Entity → VO（对外响应）
     */
    MemberRespVO toMemberResp(Member entity);
}
