package com.mallease.admin.feign;

import com.mallease.admin.dto.request.LoginRequestDto;
import com.mallease.common.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * @author: Aulen
 * @description:
 * @create: 2025-11-10 00:35
 **/
@FeignClient(name = "mall-ease-auth")
public interface AuthServiceFeignClient {
    @PostMapping("/auth/login")
    R<Map<String, String>> login(@Validated @RequestBody LoginRequestDto request);
}
