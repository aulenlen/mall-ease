package com.mallease.user.controller;

import com.mallease.common.api.Page;
import com.mallease.common.api.PageUtils;
import com.mallease.common.api.R;
import com.mallease.user.converter.RoleConverter;
import com.mallease.user.model.client.cmd.AllocMenuCmd;
import com.mallease.user.model.client.cmd.AllocResourceCmd;
import com.mallease.user.model.client.cmd.RoleCmd;
import com.mallease.user.model.client.vo.RoleDetailVO;
import com.mallease.user.model.client.vo.RoleVO;
import com.mallease.user.model.data.Role;
import com.mallease.user.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

/**
 * 后台角色管理控制器
 *
 * @author: Aulen
 * @create: 2026-01-25
 */
@Tag(name = "后台角色管理", description = "后台角色管理")
@RestController
@RequestMapping("/user/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;
    private final RoleConverter roleConverter;

    @Operation(summary = "添加角色")
    @PostMapping("/create")
    public R<Long> create(@Validated(RoleCmd.Create.class) @RequestBody RoleCmd cmd) {
        Role role = roleConverter.cmdToEntity(cmd);
        Long id = roleService.create(role);
        return R.success(id);
    }

    @Operation(summary = "修改角色")
    @PostMapping("/update")
    public R<Integer> update(@Validated(RoleCmd.Update.class) @RequestBody RoleCmd cmd) {
        Role role = roleConverter.cmdToEntity(cmd);
        int count = roleService.update(role);
        return R.success(count);
    }

    @Operation(summary = "根据ID删除角色")
    @PostMapping("/delete/{id}")
    public R<Integer> delete(@Parameter(description = "角色ID") @PathVariable Long id) {
        int count = roleService.delete(id);
        return R.success(count);
    }

    @Operation(summary = "批量删除角色")
    @PostMapping("/delete/batch")
    public R<Integer> deleteBatch(@RequestBody List<Long> ids) {
        int count = roleService.batchDelete(ids);
        return R.success(count);
    }

    @Operation(summary = "获取角色详情")
    @GetMapping("/{id}")
    public R<RoleDetailVO> getItem(@Parameter(description = "角色ID") @PathVariable Long id) {
        Role role = roleService.getById(id);
        if (role == null) {
            return R.failed("角色不存在");
        }
        RoleDetailVO detailVO = roleConverter.entityToDetailVo(role);

        List<Long> roleIds = Collections.singletonList(id);
        List<Long> menuIds = roleService.getMenuIdsByRoleIds(roleIds);
        List<Long> resourceIds = roleService.getResourceIdsByRoleIds(roleIds);

        detailVO.setMenuIds(menuIds);
        detailVO.setResourceIds(resourceIds);

        return R.success(detailVO);
    }

    @Operation(summary = "分页查询角色列表")
    @GetMapping("/list")
    public R<Page<RoleVO>> list(@Parameter(description = "角色名称关键字") @RequestParam(required = false) String keyword,
                                @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
                                @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Integer pageNum) {
        List<Role> roleList = roleService.list(keyword, pageNum, pageSize);
        List<RoleVO> roleVOList = roleConverter.entityListToVoList(roleList);
        return R.success(PageUtils.buildPage(roleList, roleVOList));
    }

    @Operation(summary = "查询所有角色")
    @GetMapping("/listAll")
    public R<List<RoleVO>> listAll() {
        List<Role> roleList = roleService.listAll();
        return R.success(roleConverter.entityListToVoList(roleList));
    }

    @Operation(summary = "修改角色状态")
    @PostMapping("/status/{id}")
    public R<Integer> updateStatus(@Parameter(description = "角色ID") @PathVariable Long id,
                                   @Parameter(description = "状态：0->禁用；1->启用") @RequestParam Integer status) {
        int count = roleService.updateStatus(id, status);
        return R.success(count);
    }

    @Operation(summary = "给角色分配菜单")
    @PostMapping("/allocMenu")
    public R<Integer> allocMenu(@Validated @RequestBody AllocMenuCmd cmd) {
        int count = roleService.allocMenu(cmd.getRoleId(), cmd.getMenuIds());
        return R.success(count);
    }

    @Operation(summary = "给角色分配资源")
    @PostMapping("/allocResource")
    public R<Integer> allocResource(@Validated @RequestBody AllocResourceCmd cmd) {
        int count = roleService.allocResource(cmd.getRoleId(), cmd.getResourceIds());
        return R.success(count);
    }
}