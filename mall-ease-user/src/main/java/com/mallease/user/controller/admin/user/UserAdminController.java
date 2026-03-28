package com.mallease.user.controller.admin.user;

import com.mallease.common.api.Page;
import com.mallease.common.api.PageUtils;
import com.mallease.common.api.R;
import com.mallease.user.convert.AdminConvert;
import com.mallease.user.convert.MenuConvert;
import com.mallease.user.convert.RoleConvert;
import com.mallease.user.controller.admin.menu.vo.MenuRespVO;
import com.mallease.user.controller.admin.role.vo.RoleRespVO;
import com.mallease.user.controller.admin.user.vo.AdminReqVO;
import com.mallease.user.controller.admin.user.vo.AdminRespVO;
import com.mallease.user.controller.admin.user.vo.AllocRoleReqVO;
import com.mallease.user.controller.admin.user.vo.CurrentAdminInfoRespVO;
import com.mallease.user.dal.entity.Admin;
import com.mallease.user.dal.entity.Menu;
import com.mallease.user.dal.entity.Role;
import com.mallease.user.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 管理端用户控制器。
 *
 * @author: Aulen
 * @create: 2026-03-27
 */
@Tag(name = "管理端-用户管理", description = "管理员信息、角色分配与当前登录信息")
@RestController
@RequestMapping("/user/admin")
@Slf4j
@RequiredArgsConstructor
public class UserAdminController {

    private final UserService userService;
    private final AdminConvert adminConvert;
    private final RoleConvert roleConvert;
    private final MenuConvert menuConvert;

    @Operation(summary = "获取当前登录管理员信息", description = "返回用户名、头像、角色列表、菜单列表")
    @GetMapping("/info")
    public R<CurrentAdminInfoRespVO> getCurrentAdmin() {
        try {
            Admin admin = userService.getCurrentAdmin();
            List<Role> roleList = userService.getCurrentRoles(admin.getId());
            List<String> roles = roleList.stream().map(Role::getName).toList();
            List<Menu> menus = userService.getCurrentMenus(admin.getId());
            List<MenuRespVO> menuRespVOList = menuConvert.toMenuRespList(menus);

            CurrentAdminInfoRespVO data = CurrentAdminInfoRespVO.builder()
                    .username(admin.getUsername())
                    .icon(admin.getIcon())
                    .roles(roles)
                    .menus(menuRespVOList)
                    .build();
            return R.success(data);
        } catch (Exception e) {
            log.error("获取当前用户信息失败", e);
            return R.failed("获取用户信息失败: " + e.getMessage());
        }
    }

    @Operation(summary = "管理员注册")
    @PostMapping("/register")
    public R<Integer> register(@Validated(AdminReqVO.Create.class) @RequestBody AdminReqVO reqVO) {
        Admin admin = adminConvert.toAdmin(reqVO);
        return R.success(userService.create(admin));
    }

    @Operation(summary = "修改指定管理员信息")
    @PostMapping("/update/{id}")
    public R<Integer> update(@PathVariable Long id, @RequestBody AdminReqVO reqVO) {
        Admin admin = adminConvert.toAdmin(reqVO);
        return R.success(userService.update(id, admin));
    }

    @Operation(summary = "删除指定管理员")
    @PostMapping("/delete/{id}")
    public R<Integer> delete(@PathVariable Long id) {
        return R.success(userService.delete(id));
    }

    @Operation(summary = "根据用户名或姓名分页查询管理员")
    @GetMapping("/list")
    public R<Page<AdminRespVO>> list(@RequestParam(value = "keyword", required = false) String keyword,
                                     @RequestParam(value = "status", required = false) Integer status,
                                     @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
                                     @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        List<Admin> adminList = userService.list(keyword, status, pageSize, pageNum);
        List<AdminRespVO> voList = adminConvert.toAdminRespList(adminList);
        return R.success(PageUtils.buildPage(adminList, voList));
    }

    @Operation(summary = "修改帐号状态")
    @PostMapping("/updateStatus/{id}")
    public R<Integer> updateStatus(@PathVariable Long id, @RequestParam("status") Integer status) {
        return R.success(userService.updateStatus(id, status));
    }

    @Operation(summary = "给用户分配角色")
    @PostMapping("/role/update")
    public R<Integer> updateRole(@Validated @RequestBody AllocRoleReqVO reqVO) {
        return R.success(userService.updateRole(reqVO.getAdminId(), reqVO.getRoleIds()));
    }

    @Operation(summary = "获取指定用户的角色")
    @GetMapping("/role/{adminId}")
    public R<List<RoleRespVO>> getRoleList(@PathVariable Long adminId) {
        List<Role> roleList = userService.getRoleList(adminId);
        return R.success(roleConvert.toRoleRespList(roleList));
    }
}