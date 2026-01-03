package com.mallease.auth.feign;

import com.mallease.auth.dto.UserAdminDto;
import com.mallease.auth.dto.UserMemberDto;
import com.mallease.auth.dto.UserResourceDto;
import com.mallease.common.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author: Aulen
 * @description:
 * @create: 2025-11-09 21:54
 **/

@FeignClient(name = "mall-ease-user")
public interface UserServiceFeignClient {
    // 管理员相关方法
    @GetMapping("/user/admin/username/{username}")
    R<UserAdminDto> getAdminByUsername(@PathVariable("username") String username);

    @GetMapping("/user/admin/{id}")
    R<UserAdminDto> getAdminById(@PathVariable("id") Long id);

    // 会员相关方法
    @GetMapping("/user/member/username/{username}")
    R<UserMemberDto> getMemberByUsername(@PathVariable("username") String username);

    @GetMapping("/user/member/{id}")
    R<UserMemberDto> getMemberById(@PathVariable("id") Long id);

    @GetMapping("/user/admin/resource/{adminId}")
    R<List<UserResourceDto>> getResourceList(@PathVariable Long adminId);
}
