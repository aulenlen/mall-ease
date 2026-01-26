package com.mallease.auth.service.impl;

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.hutool.core.util.StrUtil;
import com.mallease.auth.config.StpAdminUtil;
import com.mallease.auth.config.StpMemberUtil;
import com.mallease.auth.model.query.LoginCmd;
import com.mallease.auth.model.AdminDTO;
import com.mallease.auth.model.MemberDTO;
import com.mallease.auth.model.ResourceDTO;
import com.mallease.auth.feign.UserServiceFeignClient;
import com.mallease.auth.service.AuthService;
import com.mallease.common.api.R;
import com.mallease.common.constant.AuthConstant;
import com.mallease.common.dto.UserDTO;
import com.mallease.common.enums.UserType;
import com.mallease.common.exception.Asserts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 认证服务实现
 *
 * @author: Aulen
 * @create: 2025-11-09
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserServiceFeignClient umsServiceFeignClient;

    @Override
    public SaTokenInfo loginAdmin(LoginCmd request) {
        validateLoginParams(request);

        R<AdminDTO> result = umsServiceFeignClient.getAdminByUsername(request.getUsername());
        if (result == null || result.getData() == null) {
            Asserts.fail("用户不存在！");
        }

        AdminDTO admin = result.getData();
        validateUser(admin.getPassword(), admin.getStatus(), request.getPassword());

        StpAdminUtil.login(admin.getId());
        UserDTO userDTO = buildAdminUserDto(admin.getId(), admin.getUsername());
        StpAdminUtil.getSession().set(AuthConstant.STP_ADMIN_INFO, userDTO);

        return StpAdminUtil.getTokenInfo();
    }

    @Override
    public SaTokenInfo loginMember(LoginCmd request) {
        validateLoginParams(request);

        R<MemberDTO> result = umsServiceFeignClient.getMemberByUsername(request.getUsername());
        if (result == null || result.getData() == null) {
            Asserts.fail("用户不存在！");
        }

        MemberDTO member = result.getData();
        validateUser(member.getPassword(), member.getStatus(), request.getPassword());

        StpMemberUtil.login(member.getId());
        UserDTO userDto = buildMemberUserDto(member.getId(), member.getUsername());
        StpMemberUtil.getSession().set(AuthConstant.STP_MEMBER_INFO, userDto);

        return StpMemberUtil.getTokenInfo();
    }

    private void validateLoginParams(LoginCmd request) {
        if (StrUtil.isEmpty(request.getUsername()) || StrUtil.isEmpty(request.getPassword())) {
            Asserts.fail("用户名或密码不能为空！");
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
        List<String> permissionList = new ArrayList<>();
        List<ResourceDTO> resourceDtoList = umsServiceFeignClient.getResourceList(id).getData();
        if (resourceDtoList != null) {
            permissionList = resourceDtoList.stream()
                    .map(res -> res.getId() + ":" + res.getName())
                    .toList();
        }

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