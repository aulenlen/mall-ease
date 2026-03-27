package com.mallease.user.controller.admin.role;

import com.mallease.common.api.Page;
import com.mallease.common.api.PageUtils;
import com.mallease.common.api.R;
import com.mallease.user.convert.RoleConvert;
import com.mallease.user.controller.admin.role.vo.AllocMenuReqVO;
import com.mallease.user.controller.admin.role.vo.AllocResourceReqVO;
import com.mallease.user.controller.admin.role.vo.RoleReqVO;
import com.mallease.user.controller.admin.role.vo.RoleDetailRespVO;
import com.mallease.user.controller.admin.role.vo.RoleRespVO;
import com.mallease.user.dal.entity.Role;
import com.mallease.user.service.role.RoleService;
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
public class RoleAdminController {

    private final RoleService roleService;
    private final RoleConvert roleConvert;

    @Operation(summary = "添加角色")
    @PostMapping("/create")
    public R<Long> create(@Validated(RoleReqVO.Create.class) @RequestBody RoleReqVO reqVO) {
        Role role = roleConvert.reqVOToEntity(reqVO);
        Long id = roleService.create(role);
        return R.success(id);
    }

    @Operation(summary = "修改角色")
    @PostMapping("/update")
    public R<Integer> update(@Validated(RoleReqVO.Update.class) @RequestBody RoleReqVO reqVO) {
        Role role = roleConvert.reqVOToEntity(reqVO);
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
    public R<RoleDetailRespVO> getItem(@Parameter(description = "角色ID") @PathVariable Long id) {
        Role role = roleService.getById(id);
        if (role == null) {
            return R.failed("角色不存在");
        }
        RoleDetailRespVO detailVO = roleConvert.entityToDetailRespVO(role);

        List<Long> roleIds = Collections.singletonList(id);
        List<Long> menuIds = roleService.getMenuIdsByRoleIds(roleIds);
        List<Long> resourceIds = roleService.getResourceIdsByRoleIds(roleIds);

        detailVO.setMenuIds(menuIds);
        detailVO.setResourceIds(resourceIds);

        return R.success(detailVO);
    }

    @Operation(summary = "分页查询角色列表")
    @GetMapping("/list")
    public R<Page<RoleRespVO>> list(@Parameter(description = "角色名称关键字") @RequestParam(required = false) String keyword,
                                @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
                                @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Integer pageNum) {
        List<Role> roleList = roleService.list(keyword, pageNum, pageSize);
        List<RoleRespVO> roleVOList = roleConvert.entityListToRespVOList(roleList);
        return R.success(PageUtils.buildPage(roleList, roleVOList));
    }

    @Operation(summary = "查询所有角色")
    @GetMapping("/listAll")
    public R<List<RoleRespVO>> listAll() {
        List<Role> roleList = roleService.listAll();
        return R.success(roleConvert.entityListToRespVOList(roleList));
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
    public R<Integer> allocMenu(@Validated @RequestBody AllocMenuReqVO reqVO) {
        int count = roleService.allocMenu(reqVO.getRoleId(), reqVO.getMenuIds());
        return R.success(count);
    }

    @Operation(summary = "给角色分配资源")
    @PostMapping("/allocResource")
    public R<Integer> allocResource(@Validated @RequestBody AllocResourceReqVO reqVO) {
        int count = roleService.allocResource(reqVO.getRoleId(), reqVO.getResourceIds());
        return R.success(count);
    }
}