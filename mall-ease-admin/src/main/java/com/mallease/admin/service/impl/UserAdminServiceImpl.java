package com.mallease.admin.service.impl;

import com.mallease.admin.dto.request.LoginRequestDto;
import com.mallease.admin.feign.AuthServiceFeignClient;
import com.mallease.admin.feign.UmsServiceFeignClient;
import com.mallease.admin.service.UserAdminService;
import com.mallease.common.api.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author: Aulen
 * @description: 管理员服务实现类
 * @create: 2025-11-10
 **/
@Service
public class UserAdminServiceImpl implements UserAdminService {

    @Autowired
    private AuthServiceFeignClient authServiceFeignClient;
    @Autowired
    private UmsServiceFeignClient umsServiceFeignClient;

    @Override
    public Map<String, String> login(LoginRequestDto loginRequestDto) {
        R<Map<String, String>> result = authServiceFeignClient.login(loginRequestDto);
        if (result == null || result.getData() == null) {
            throw new RuntimeException("登录失败");
        }
        return result.getData();
    }

    @Override
    public Map<String, Object> getCurrentAdminInfo() {
        Map<String, Object> info = umsServiceFeignClient.getCurrentAdmin().getData();
        return info;
    }
}
