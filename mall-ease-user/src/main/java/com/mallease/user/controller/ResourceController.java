package com.mallease.user.controller;

import com.github.pagehelper.PageHelper;
import com.mallease.common.api.Page;
import com.mallease.common.api.PageUtils;
import com.mallease.common.api.R;
import com.mallease.user.converter.ResourceConverter;
import com.mallease.user.model.client.cmd.ResourceCmd;
import com.mallease.user.model.client.vo.ResourceVO;
import com.mallease.user.model.data.Resource;
import com.mallease.user.service.ResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台资源管理控制器
 *
 * @author: Aulen
 * @create: 2026-01-25
 */
@Tag(name = "后台资源管理", description = "后台资源管理")
@RestController
@RequestMapping("/user/resource")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;
    private final ResourceConverter resourceConverter;

    @Operation(summary = "添加后台资源")
    @PostMapping("/create")
    public R<Integer> create(@Validated(ResourceCmd.Create.class) @RequestBody ResourceCmd cmd) {
        Resource resource = resourceConverter.cmdToEntity(cmd);
        int count = resourceService.create(resource);
        return R.success(count);
    }

    @Operation(summary = "修改后台资源")
    @PostMapping("/update")
    public R<Integer> update(@Validated(ResourceCmd.Update.class) @RequestBody ResourceCmd cmd) {
        Resource resource = resourceConverter.cmdToEntity(cmd);
        int count = resourceService.update(cmd.getId(), resource);
        return R.success(count);
    }

    @Operation(summary = "根据ID获取资源详情")
    @GetMapping("/{id}")
    public R<ResourceVO> getItem(@Parameter(description = "资源ID") @PathVariable Long id) {
        Resource resource = resourceService.getItem(id);
        return R.success(resourceConverter.entityToVo(resource));
    }

    @Operation(summary = "根据ID删除后台资源")
    @PostMapping("/delete/{id}")
    public R<Integer> delete(@Parameter(description = "资源ID") @PathVariable Long id) {
        int count = resourceService.delete(id);
        return count > 0 ? R.success(count) : R.failed("删除失败");
    }

    @Operation(summary = "分页模糊查询后台资源")
    @GetMapping("/list")
    public R<Page<ResourceVO>> list(@Parameter(description = "资源名称") @RequestParam(required = false) String name,
                                    @Parameter(description = "资源URL") @RequestParam(required = false) String url,
                                    @Parameter(description = "分类ID") @RequestParam(required = false) Long categoryId,
                                    @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
                                    @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        List<Resource> resourceList = resourceService.list(name, url, categoryId);
        List<ResourceVO> voList = resourceConverter.entityListToVoList(resourceList);
        return R.success(PageUtils.buildPage(resourceList, voList));
    }

    @Operation(summary = "查询所有后台资源")
    @GetMapping("/listAll")
    public R<List<ResourceVO>> listAll() {
        List<Resource> resourceList = resourceService.listAll();
        return R.success(resourceConverter.entityListToVoList(resourceList));
    }
}