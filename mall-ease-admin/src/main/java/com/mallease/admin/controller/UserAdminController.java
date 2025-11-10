package com.mallease.admin.controller;

import com.mallease.admin.dto.request.LoginRequestDto;
import com.mallease.admin.dto.request.UmsAdminLoginRequest;
import com.mallease.admin.service.UserAdminService;
import com.mallease.common.api.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author: Aulen
 * @description:
 * @create: 2025-11-07 14:53
 * 后台管理端
 **/
@RestController
@RequestMapping("/admin")
@Slf4j
public class UserAdminController {
    @Autowired
    private UserAdminService userAdminService;

    /**
     * 管理端后台登录
     *
     * @param request
     * @return
     */
    @PostMapping("/login")
    public R login(@Validated @RequestBody UmsAdminLoginRequest request) {
        LoginRequestDto adminDto = LoginRequestDto.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .userType("admin").build();
        Map<String, String> token = userAdminService.login(adminDto);  // 调用Service
        return R.success(token);
    }

    /**
     * 获取当前登录用户信息
     *
     * @return
     */
    @GetMapping("/info")
    public R info() {
        Map<String, Object> currentAdminInfo = userAdminService.getCurrentAdminInfo();
        return R.success(currentAdminInfo);
    }
}
