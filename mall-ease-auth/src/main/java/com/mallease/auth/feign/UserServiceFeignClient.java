package com.mallease.auth.feign;

import com.mallease.auth.model.AdminDTO;
import com.mallease.auth.model.ResourceDTO;
import com.mallease.common.api.R;
import com.mallease.common.dto.remote.MemberDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 用户服务 Feign 客户端
 *
 * @author: Aulen
 * @create: 2025-11-09
 */
@FeignClient(name = "mall-ease-user")
public interface UserServiceFeignClient {

    // ============ 管理员相关方法 ============

    @GetMapping("/user/admin/username/{username}")
    R<AdminDTO> getAdminByUsername(@PathVariable("username") String username);

    @GetMapping("/user/admin/{id}")
    R<AdminDTO> getAdminById(@PathVariable("id") Long id);

    @GetMapping("/user/admin/resource/{adminId}")
    R<List<ResourceDTO>> getResourceList(@PathVariable Long adminId);

    // ============ 会员相关方法 ============

    @GetMapping("/user/member/username/{username}")
    R<MemberDTO> getMemberByUsername(@PathVariable("username") String username);

    @GetMapping("/user/member/{id}")
    R<MemberDTO> getMemberById(@PathVariable("id") Long id);

    @GetMapping("/user/member/internal/phone/{phone}")
    R<MemberDTO> getMemberByPhone(@PathVariable("phone") String phone);

    @PostMapping("/user/member/internal/register")
    R<Long> registerMember(@RequestBody MemberDTO member);
}
