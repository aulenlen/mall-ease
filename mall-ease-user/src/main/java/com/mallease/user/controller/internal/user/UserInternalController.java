package com.mallease.user.controller.internal.user;

import com.mallease.common.api.R;
import com.mallease.common.dto.remote.AdminDTO;
import com.mallease.common.dto.remote.MemberDTO;
import com.mallease.common.dto.remote.ResourceDTO;
import com.mallease.user.convert.AdminConvert;
import com.mallease.user.convert.MemberConvert;
import com.mallease.user.convert.ResourceConvert;
import com.mallease.user.dal.entity.Admin;
import com.mallease.user.dal.entity.Member;
import com.mallease.user.dal.entity.Resource;
import com.mallease.user.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 内部用户控制器。
 *
 * @author: Aulen
 * @create: 2026-03-27
 */
@Tag(name = "内部-用户接口", description = "供网关、认证和其他服务内部调用")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserInternalController {

    private final UserService userService;
    private final AdminConvert adminConvert;
    private final MemberConvert memberConvert;
    private final ResourceConvert resourceConvert;

    @Operation(summary = "根据用户名查询管理员", description = "内部调用")
    @GetMapping("/admin/username/{username}")
    public R<AdminDTO> getAdminByUsername(@Parameter(description = "用户名") @PathVariable String username) {
        Admin admin = userService.getAdminByUsername(username);
        return R.success(adminConvert.toAdminRemote(admin));
    }

    @Operation(summary = "根据ID查询管理员", description = "内部调用")
    @GetMapping("/admin/{id}")
    public R<AdminDTO> getAdminById(@Parameter(description = "管理员ID") @PathVariable Long id) {
        Admin admin = userService.getAdminById(id);
        return R.success(adminConvert.toAdminRemote(admin));
    }

    @Operation(summary = "根据用户名查询会员", description = "内部调用")
    @GetMapping("/member/username/{username}")
    public R<MemberDTO> getMemberByUsername(@Parameter(description = "用户名") @PathVariable String username) {
        Member member = userService.getMemberByUsername(username);
        return R.success(memberConvert.toMemberRemote(member));
    }

    @Operation(summary = "根据ID查询会员", description = "内部调用")
    @GetMapping("/member/{id}")
    public R<MemberDTO> getMemberById(@Parameter(description = "会员ID") @PathVariable Long id) {
        Member member = userService.getMemberById(id);
        return R.success(memberConvert.toMemberRemote(member));
    }

    @Operation(summary = "获取管理员资源列表", description = "内部调用，用于权限校验")
    @GetMapping("/admin/resource/{adminId}")
    public R<List<ResourceDTO>> getResourceList(@Parameter(description = "管理员ID") @PathVariable Long adminId) {
        List<Resource> resourceList = userService.getResourceList(adminId);
        return R.success(resourceConvert.toResourceRemoteList(resourceList));
    }

    @Operation(summary = "会员注册", description = "内部调用")
    @PostMapping("/member/internal/register")
    public R<Long> registerMember(@RequestBody MemberDTO memberDTO) {
        Member member = memberConvert.toMember(memberDTO);
        return R.success(userService.registerMember(member));
    }

    @Operation(summary = "根据手机号查询会员", description = "内部调用")
    @GetMapping("/member/internal/phone/{phone}")
    public R<MemberDTO> getMemberByPhone(@Parameter(description = "手机号") @PathVariable String phone) {
        Member member = userService.getMemberByPhone(phone);
        return R.success(memberConvert.toMemberRemote(member));
    }
}