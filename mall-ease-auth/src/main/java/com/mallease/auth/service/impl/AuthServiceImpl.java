package com.mallease.auth.service.impl;

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.mallease.auth.dto.request.LoginRequest;
import com.mallease.auth.dto.UserAdminDto;
import com.mallease.auth.dto.UserMemberDto;
import com.mallease.auth.dto.UserResourceDto;
import com.mallease.auth.feign.UserServiceFeignClient;
import com.mallease.auth.service.AuthService;
import com.mallease.common.api.R;
import com.mallease.common.constant.AuthConstant;
import com.mallease.common.dto.UserDto;
import com.mallease.common.enums.UserType;
import com.mallease.common.exception.Asserts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Aulen
 * @description:
 * @create: 2025-11-09 19:09
 **/
@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private UserServiceFeignClient umsServiceFeignClient;

    @Override
    public SaTokenInfo login(LoginRequest request) {
        // 1. 参数校验
        if (StrUtil.isEmpty(request.getUsername()) || StrUtil.isEmpty(request.getPassword())) {
            Asserts.fail("用户名或密码不能为空！");
        }

        // 2. 确定用户类型
        UserType userType = determineUserType(request.getUserType());

        // 3. 根据用户类型获取用户信息并验证
        if (userType == UserType.ADMIN) {
            return loginAdmin(request);
        } else {
            return loginMember(request);
        }
    }

    /**
     * 管理员登录
     */
    private SaTokenInfo loginAdmin(LoginRequest request) {
        R<UserAdminDto> result = umsServiceFeignClient.getAdminByUsername(request.getUsername());
        if (result == null || result.getData() == null) {
            Asserts.fail("用户不存在！");
        }

        UserAdminDto admin = result.getData();
        validateUser(admin.getPassword(), admin.getStatus(), request.getPassword());

        StpUtil.login(admin.getId());
        UserDto userDto = buildUserDto(admin.getId(), admin.getUsername(), UserType.ADMIN);
        StpUtil.getSession().set(AuthConstant.STP_ADMIN_INFO, userDto);

        return StpUtil.getTokenInfo();
    }

    /**
     * 会员登录
     */
    private SaTokenInfo loginMember(LoginRequest request) {
        R<UserMemberDto> result = umsServiceFeignClient.getMemberByUsername(request.getUsername());
        if (result == null || result.getData() == null) {
            Asserts.fail("用户不存在！");
        }

        UserMemberDto member = result.getData();
        validateUser(member.getPassword(), member.getStatus(), request.getPassword());

        StpUtil.login(member.getId());
        UserDto userDto = buildUserDto(member.getId(), member.getUsername(), UserType.MEMBER);
        StpUtil.getSession().set(AuthConstant.STP_MEMBER_INFO, userDto);

        return StpUtil.getTokenInfo();
    }

    /**
     * 确定用户类型
     */
    private UserType determineUserType(String userTypeStr) {
        if (StrUtil.isEmpty(userTypeStr)) {
            return UserType.ADMIN; // 默认管理员
        }
        return "member".equalsIgnoreCase(userTypeStr) ? UserType.MEMBER : UserType.ADMIN;
    }

    /**
     * 统一的用户验证逻辑
     */
    private void validateUser(String dbPassword, Integer status, String password) {
        if (!BCrypt.checkpw(password, dbPassword)) {
            Asserts.fail("密码错误！");
        }
        if (status == null || status != 1) {
            Asserts.fail("账户被禁用！");
        }
    }

    /**
     * 构建UserDto
     */
    private UserDto buildUserDto(Long id, String username, UserType userType) {
        List<String> permissionList = new ArrayList<>();
        // 如果是管理员，可以获取权限列表（如果需要）
        if (userType == UserType.ADMIN) {
            List<UserResourceDto> resourceDtoList = umsServiceFeignClient.getResourceList(id).getData();
            permissionList = resourceDtoList.stream().map(res -> res.getId() + ":" + res.getName()).toList();
        }

        return UserDto.builder()
                .id(id)
                .username(username)
                .clientId(userType.getClientId())
                .permissionList(permissionList)
                .build();
    }
}
