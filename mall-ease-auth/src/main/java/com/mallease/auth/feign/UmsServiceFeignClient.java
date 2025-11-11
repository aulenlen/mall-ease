package com.mallease.auth.feign;

import com.mallease.auth.dto.UmsAdminDto;
import com.mallease.auth.dto.UmsMemberDto;
import com.mallease.auth.dto.UmsResourceDto;
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

@FeignClient(name = "mall-ease-ums")
public interface UmsServiceFeignClient {
    // 管理员相关方法
    @GetMapping("/ums/admin/username/{username}")
    R<UmsAdminDto> getAdminByUsername(@PathVariable("username") String username);

    @GetMapping("/ums/admin/{id}")
    R<UmsAdminDto> getAdminById(@PathVariable("id") Long id);

    // 会员相关方法
    @GetMapping("/ums/member/username/{username}")
    R<UmsMemberDto> getMemberByUsername(@PathVariable("username") String username);

    @GetMapping("/ums/member/{id}")
    R<UmsMemberDto> getMemberById(@PathVariable("id") Long id);

    @GetMapping("/ums/admin/resource/{adminId}")
    R<List<UmsResourceDto>> getResourceList(@PathVariable Long adminId);
}
