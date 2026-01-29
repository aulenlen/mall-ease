package com.mallease.user.controller;

import com.mallease.common.api.Page;
import com.mallease.common.api.PageUtils;
import com.mallease.common.api.R;
import com.mallease.common.dto.remote.MemberDTO;
import com.mallease.user.config.StpMemberUtil;
import com.mallease.user.converter.AdminConverter;
import com.mallease.user.converter.MemberConverter;
import com.mallease.user.converter.RoleConverter;
import com.mallease.user.model.client.cmd.AdminCmd;
import com.mallease.user.model.client.cmd.AllocRoleCmd;
import com.mallease.user.model.client.vo.AdminVO;
import com.mallease.user.model.client.vo.MemberVO;
import com.mallease.user.model.client.vo.RoleVO;
import com.mallease.user.model.data.*;
import com.mallease.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户管理控制器
 *
 * @author: Aulen
 * @create: 2025-11-09
 */
@Tag(name = "用户管理", description = "管理员和会员信息管理")
@RestController
@RequestMapping("/user")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AdminConverter adminConverter;
    private final MemberConverter memberConverter;
    private final RoleConverter roleConverter;

    @Operation(summary = "获取当前登录管理员信息", description = "返回用户名、头像、角色列表、菜单列表")
    @GetMapping("/admin/info")
    public R<Map<String, Object>> getCurrentAdmin() {
        try {
            Admin admin = userService.getCurrentAdmin();
            List<Role> roleList = userService.getCurrentRoles(admin.getId());
            List<String> roles = roleList.stream().map(Role::getName).toList();
            Map<String, Object> data = new HashMap<>();
            List<Menu> menus = userService.getCurrentMenus(admin.getId());
            data.put("username", admin.getUsername());
            data.put("icon", admin.getIcon());
            data.put("roles", roles);
            data.put("menus", menus);
            return R.success(data);
        } catch (Exception e) {
            log.error("获取当前用户信息失败", e);
            return R.failed("获取用户信息失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取当前登录会员信息", description = "前台接口，返回会员基本信息")
    @GetMapping("/member/portal/me")
    public R<MemberVO> getCurrentMember() {
        Long memberId = StpMemberUtil.getLoginIdAsLong();
        Member member = userService.getMemberById(memberId);
        return R.success(memberConverter.entityToVO(member));
    }

    @Operation(summary = "管理员注册")
    @PostMapping("/admin/register")
    public R<Integer> register(@Validated(AdminCmd.Create.class) @RequestBody AdminCmd cmd) {
        Admin admin = adminConverter.cmdToEntity(cmd);
        int count = userService.create(admin);
        return R.success(count);
    }

    @Operation(summary = "修改指定管理员信息")
    @PostMapping("/admin/update/{id}")
    public R<Integer> update(@PathVariable Long id, @RequestBody AdminCmd cmd) {
        Admin admin = adminConverter.cmdToEntity(cmd);
        int count = userService.update(id, admin);
        return R.success(count);
    }

    @Operation(summary = "删除指定管理员")
    @PostMapping("/admin/delete/{id}")
    public R<Integer> delete(@PathVariable Long id) {
        int count = userService.delete(id);
        return R.success(count);
    }

    @Operation(summary = "根据用户名或姓名分页查询管理员")
    @GetMapping("/admin/list")
    public R<Page<AdminVO>> list(@RequestParam(value = "keyword", required = false) String keyword,
                                 @RequestParam(value = "status", required = false) Integer status,
                                 @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
                                 @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        List<Admin> adminList = userService.list(keyword, status, pageSize, pageNum);
        List<AdminVO> voList = adminConverter.entityListToVoList(adminList);
        return R.success(PageUtils.buildPage(adminList, voList));
    }

    @Operation(summary = "修改帐号状态")
    @PostMapping("/admin/updateStatus/{id}")
    public R<Integer> updateStatus(@PathVariable Long id, @RequestParam(value = "status") Integer status) {
        int count = userService.updateStatus(id, status);
        return R.success(count);
    }

    @Operation(summary = "给用户分配角色")
    @PostMapping("/admin/role/update")
    public R<Integer> updateRole(@Validated @RequestBody AllocRoleCmd cmd) {
        int count = userService.updateRole(cmd.getAdminId(), cmd.getRoleIds());
        return R.success(count);
    }

    @Operation(summary = "获取指定用户的角色")
    @GetMapping("/admin/role/{adminId}")
    public R<List<RoleVO>> getRoleList(@PathVariable Long adminId) {
        List<Role> roleList = userService.getRoleList(adminId);
        return R.success(roleConverter.entityListToVoList(roleList));
    }

    @Operation(summary = "根据用户名查询管理员", description = "内部调用")
    @GetMapping("/admin/username/{username}")
    public R<Admin> getAdminByUsername(@Parameter(description = "用户名") @PathVariable String username) {
        Admin admin = userService.getAdminByUsername(username);
        return R.success(admin);
    }

    @Operation(summary = "根据ID查询管理员", description = "内部调用")
    @GetMapping("/admin/{id}")
    public R<Admin> getAdminById(@Parameter(description = "管理员ID") @PathVariable Long id) {
        Admin admin = userService.getAdminById(id);
        return R.success(admin);
    }

    @Operation(summary = "根据用户名查询会员", description = "内部调用")
    @GetMapping("/member/username/{username}")
    public R<MemberDTO> getMemberByUsername(@Parameter(description = "用户名") @PathVariable String username) {
        Member member = userService.getMemberByUsername(username);
        return R.success(memberConverter.entityToDTO(member));
    }

    @Operation(summary = "根据ID查询会员", description = "内部调用")
    @GetMapping("/member/{id}")
    public R<MemberDTO> getMemberById(
            @Parameter(description = "会员ID") @PathVariable Long id) {
        Member member = userService.getMemberById(id);
        return R.success(memberConverter.entityToDTO(member));
    }

    @Operation(summary = "获取管理员资源列表", description = "内部调用，用于权限校验")
    @GetMapping("/admin/resource/{adminId}")
    public R<List<Resource>> getResourceList(
            @Parameter(description = "管理员ID") @PathVariable Long adminId) {
        List<Resource> resourceList = userService.getResourceList(adminId);
        return R.success(resourceList);
    }

    @Operation(summary = "会员注册", description = "内部调用")
    @PostMapping("/member/internal/register")
    public R<Long> registerMember(@RequestBody MemberDTO memberDTO) {
        Member member = memberConverter.dtoToEntity(memberDTO);
        Long memberId = userService.registerMember(member);
        return R.success(memberId);
    }

    @Operation(summary = "根据手机号查询会员", description = "内部调用")
    @GetMapping("/member/internal/phone/{phone}")
    public R<MemberDTO> getMemberByPhone(@Parameter(description = "手机号") @PathVariable String phone) {
        Member member = userService.getMemberByPhone(phone);
        return R.success(memberConverter.entityToDTO(member));
    }
}
