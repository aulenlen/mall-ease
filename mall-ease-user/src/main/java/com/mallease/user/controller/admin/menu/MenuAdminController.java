package com.mallease.user.controller.admin.menu;

import com.mallease.common.api.R;
import com.mallease.common.api.ResultCode;
import com.mallease.user.convert.MenuConvert;
import com.mallease.user.controller.admin.menu.vo.MenuReqVO;
import com.mallease.user.controller.admin.menu.vo.MenuTreeRespVO;
import com.mallease.user.controller.admin.menu.vo.MenuRespVO;
import com.mallease.user.dal.entity.Menu;
import com.mallease.user.service.menu.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "菜单管理", description = "菜单的增删改查")
@RestController
@RequestMapping("/admin/menus")
@RequiredArgsConstructor
public class MenuAdminController {
    private final MenuService menuService;
    private final MenuConvert menuConvert;

    @Operation(summary = "创建菜单")
    @PostMapping
    public R<Integer> create(@Validated(MenuReqVO.Create.class) @RequestBody MenuReqVO reqVO) {
        Menu menu = menuConvert.toMenu(reqVO);
        return R.success(menuService.create(menu));
    }

    @Operation(summary = "更新菜单")
    @PutMapping
    public R<Integer> update(@Validated(MenuReqVO.Update.class) @RequestBody MenuReqVO reqVO) {
        Menu menu = menuConvert.toMenu(reqVO);
        return R.success(menuService.update(menu));
    }

    @Operation(summary = "删除菜单")
    @DeleteMapping("/{id}")
    public R<Integer> delete(@Parameter(description = "菜单ID") @PathVariable Long id) {
        return R.success(menuService.delete(id));
    }

    @Operation(summary = "获取菜单详情")
    @GetMapping("/{id}")
    public R<MenuRespVO> getById(@Parameter(description = "菜单ID") @PathVariable Long id) {
        return R.success(menuConvert.toMenuResp(menuService.getById(id)));
    }

    @Operation(summary = "批量删除菜单")
    @DeleteMapping("/batch")
    public R<Integer> deleteBatch(@RequestBody List<Long> ids) {
        int count = menuService.deleteBatch(ids);
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "获取菜单树形结构")
    @GetMapping("/tree")
    public R<List<MenuTreeRespVO>> treeMenu() {
        List<Menu> menus = menuService.listAll();
        List<MenuTreeRespVO> menuTreeVOS = menuConvert.buildMenuTree(menus);
        return R.success(menuTreeVOS);
    }
}
