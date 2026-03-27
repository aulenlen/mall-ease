package com.mallease.user.service.auth;

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.hutool.core.util.StrUtil;
import com.mallease.common.constant.AuthConstant;
import com.mallease.common.dto.remote.UserDTO;
import com.mallease.common.enums.UserType;
import com.mallease.common.exception.Asserts;
import com.mallease.user.config.StpAdminUtil;
import com.mallease.user.config.StpMemberUtil;
import com.mallease.user.controller.admin.auth.vo.AdminLoginReqVO;
import com.mallease.user.controller.portal.auth.vo.MemberLoginReqVO;
import com.mallease.user.controller.portal.auth.vo.MemberRegisterReqVO;
import com.mallease.user.dal.entity.Admin;
import com.mallease.user.dal.entity.Member;
import com.mallease.user.dal.entity.Resource;
import com.mallease.user.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 认证服务实现。
 *
 * @author: Aulen
 * @create: 2025-11-09
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;

    @Override
    public SaTokenInfo loginAdmin(AdminLoginReqVO reqVO) {
        validateLoginParams(reqVO.getUsername(), reqVO.getPassword());

        Admin admin = userService.getAdminByUsername(reqVO.getUsername());
        if (admin == null) {
            Asserts.fail("用户不存在！");
        }

        validateUser(admin.getPassword(), admin.getStatus(), reqVO.getPassword());

        StpAdminUtil.login(admin.getId());
        UserDTO userDTO = buildAdminUserDto(admin.getId(), admin.getUsername());
        StpAdminUtil.getSession().set(AuthConstant.STP_ADMIN_INFO, userDTO);
        return StpAdminUtil.getTokenInfo();
    }

    @Override
    public SaTokenInfo loginMember(MemberLoginReqVO reqVO) {
        validateLoginParams(reqVO.getUsername(), reqVO.getPassword());

        Member member = userService.getMemberByUsername(reqVO.getUsername());
        if (member == null) {
            Asserts.fail("用户不存在！");
        }

        validateUser(member.getPassword(), member.getStatus(), reqVO.getPassword());

        StpMemberUtil.login(member.getId());
        UserDTO userDTO = buildMemberUserDto(member.getId(), member.getUsername());
        StpMemberUtil.getSession().set(AuthConstant.STP_MEMBER_INFO, userDTO);
        return StpMemberUtil.getTokenInfo();
    }

    @Override
    public SaTokenInfo registerMember(MemberRegisterReqVO reqVO) {
        validateRegisterParams(reqVO);

        Member member = new Member();
        member.setUsername(reqVO.getUsername());
        member.setPassword(reqVO.getPassword());
        member.setPhone(reqVO.getPhone());
        member.setNickname(StrUtil.isNotBlank(reqVO.getNickname()) ? reqVO.getNickname() : reqVO.getUsername());

        Long memberId = userService.registerMember(member);

        StpMemberUtil.login(memberId);
        UserDTO userDTO = buildMemberUserDto(memberId, reqVO.getUsername());
        StpMemberUtil.getSession().set(AuthConstant.STP_MEMBER_INFO, userDTO);
        return StpMemberUtil.getTokenInfo();
    }

    private void validateLoginParams(String username, String password) {
        if (StrUtil.isEmpty(username) || StrUtil.isEmpty(password)) {
            Asserts.fail("用户名或密码不能为空！");
        }
    }

    private void validateRegisterParams(MemberRegisterReqVO reqVO) {
        if (StrUtil.isEmpty(reqVO.getUsername())) {
            Asserts.fail("用户名不能为空！");
        }
        if (StrUtil.isEmpty(reqVO.getPassword())) {
            Asserts.fail("密码不能为空！");
        }
        if (!reqVO.getPassword().equals(reqVO.getConfirmPassword())) {
            Asserts.fail("两次输入的密码不一致！");
        }
    }

    private void validateUser(String dbPassword, Integer status, String password) {
        if (!BCrypt.checkpw(password, dbPassword)) {
            Asserts.fail("密码错误！");
        }
        if (status == null || status != 1) {
            Asserts.fail("账户被禁用！");
        }
    }

    private UserDTO buildAdminUserDto(Long id, String username) {
        List<Resource> resourceList = userService.getResourceList(id);
        List<String> permissionList = resourceList == null ? new ArrayList<>() : resourceList.stream()
                .map(resource -> resource.getId() + ":" + resource.getName())
                .toList();

        return UserDTO.builder()
                .id(id)
                .username(username)
                .clientId(UserType.ADMIN.getClientId())
                .permissionList(permissionList)
                .build();
    }

    private UserDTO buildMemberUserDto(Long id, String username) {
        return UserDTO.builder()
                .id(id)
                .username(username)
                .clientId(UserType.MEMBER.getClientId())
                .permissionList(new ArrayList<>())
                .build();
    }
}
