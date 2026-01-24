package com.mallease.user.controller;

import com.mallease.common.api.R;
import com.mallease.common.api.ResultCode;
import com.mallease.user.converter.MenuConverter;
import com.mallease.user.model.client.cmd.MenuCmd;
import com.mallease.user.model.client.vo.MenuTreeVO;
import com.mallease.user.model.client.vo.MenuVO;
import com.mallease.user.model.data.Menu;
import com.mallease.user.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "菜单管理", description = "菜单的增删改查")
@RestController
@RequestMapping("/user/menu")
@RequiredArgsConstructor
public class MenuController {
    private final MenuService menuService;
    private final MenuConverter menuConverter;

    @Operation(summary = "创建菜单")
    @PostMapping("/create")
    public R<Integer> create(@Validated(MenuCmd.Create.class) @RequestBody MenuCmd cmd) {
        Menu menu = menuConverter.cmdToEntity(cmd);
        return R.success(menuService.create(menu));
    }

    @Operation(summary = "更新菜单")
    @PostMapping("/update")
    public R<Integer> update(@Validated(MenuCmd.Update.class) @RequestBody MenuCmd cmd) {
        Menu menu = menuConverter.cmdToEntity(cmd);
        return R.success(menuService.update(menu));
    }

    @Operation(summary = "删除菜单")
    @DeleteMapping("/delete/{id}")
    public R<Integer> delete(@Parameter(description = "菜单ID") @PathVariable Long id) {
        return R.success(menuService.delete(id));
    }

    @Operation(summary = "获取菜单详情")
    @GetMapping("/{id}")
    public R<MenuVO> getById(@Parameter(description = "菜单ID") @PathVariable Long id) {
        return R.success(menuConverter.entityToVo(menuService.getById(id)));
    }

    @Operation(summary = "批量删除菜单")
    @PostMapping("/delete/batch")
    public R<Integer> deleteBatch(@RequestBody List<Long> ids) {
        int count = menuService.deleteBatch(ids);
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "获取菜单树形结构")
    @GetMapping("/tree")
    public R<List<MenuTreeVO>> treeMenu() {
        List<Menu> menus = menuService.listAll();
        List<MenuTreeVO> menuTreeVOS = menuConverter.buildTree(menus);
        return R.success(menuTreeVOS);
    }
}
