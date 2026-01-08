package com.mallease.content.controller;

import com.github.pagehelper.PageHelper;
import com.mallease.common.api.Page;
import com.mallease.common.api.PageUtils;
import com.mallease.common.api.R;
import com.mallease.common.api.ResultCode;
import com.mallease.common.dto.remote.EditorialDTO;
import com.mallease.content.converter.EditorialConverter;
import com.mallease.content.model.client.cmd.EditorialCmd;
import com.mallease.content.model.client.query.EditorialQuery;
import com.mallease.content.model.client.vo.EditorialVO;
import com.mallease.content.model.data.entity.Editorial;
import com.mallease.content.model.data.entity.EditorialSpuRelation;
import com.mallease.content.service.EditorialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 编辑精选管理
 *
 * @author: Aulen
 * @create: 2026-01-07
 */
@Tag(name = "编辑精选管理", description = "品牌期刊增删改查")
@RestController
@RequestMapping("/content/editorial")
@RequiredArgsConstructor
public class EditorialController {

    private final EditorialConverter editorialConverter;
    private final EditorialService editorialService;

    @Operation(summary = "创建编辑精选")
    @PostMapping("/create")
    public R<Long> create(@Validated(EditorialCmd.Create.class) @RequestBody EditorialCmd cmd) {
        Editorial editorial = editorialConverter.cmdToEntity(cmd);
        Long id = editorialService.create(editorial, cmd.getSpuIds());
        return R.success(id);
    }

    @Operation(summary = "更新编辑精选")
    @PutMapping("/update")
    public R<Integer> update(@Validated(EditorialCmd.Update.class) @RequestBody EditorialCmd cmd) {
        Editorial editorial = editorialConverter.cmdToEntity(cmd);
        int count = editorialService.update(editorial, cmd.getSpuIds());
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "删除编辑精选")
    @DeleteMapping("/delete/{id}")
    public R<Integer> delete(@PathVariable Long id) {
        int count = editorialService.delete(id);
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "批量删除编辑精选")
    @DeleteMapping("/delete/batch")
    public R<Integer> deleteBatch(
            @Parameter(description = "编辑精选ID列表") @RequestParam("ids") List<Long> ids) {
        int count = editorialService.deleteBatch(ids);
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "查询编辑精选详情")
    @GetMapping("/{id}")
    public R<EditorialVO> getById(@PathVariable Long id) {
        Editorial editorial = editorialService.getById(id);
        if (editorial == null) {
            return R.failed("编辑精选不存在");
        }
        EditorialVO vo = editorialConverter.entityToVo(editorial);
        vo.setSpuIds(editorialService.getSpuIdsByEditorialId(id));
        return R.success(vo);
    }

    @Operation(summary = "分页查询编辑精选")
    @GetMapping
    public R<Page<EditorialVO>> list(@ParameterObject EditorialQuery query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<Editorial> list = editorialService.list(query);
        Page<EditorialVO> result = PageUtils.convertPage(list, editorialConverter::entityListToVoList);
        return R.success(result);
    }

    @Operation(summary = "批量更新状态")
    @PutMapping("/status")
    public R<Integer> updateStatus(
            @Parameter(description = "编辑精选ID列表") @RequestParam("ids") List<Long> ids,
            @Parameter(description = "状态：0-草稿 1-已发布 2-已下架") @RequestParam("status") Integer status) {
        if (status < 0 || status > 2) {
            return R.failed(ResultCode.VALIDATE_FAILED, "状态值只能是 0、1 或 2");
        }
        int count = editorialService.updateStatusBatch(ids, status);
        return count > 0 ? R.success(count) : R.failed();
    }

    @Operation(summary = "绑定商品（追加关联）")
    @PostMapping("/{id}/spus")
    public R<Integer> bindSpus(
            @Parameter(description = "编辑精选ID") @PathVariable("id") Long editorialId,
            @Parameter(description = "商品ID列表") @RequestBody List<Long> spuIds) {
        int count = editorialService.bindSpuIds(editorialId, spuIds);
        return R.success(count);
    }

    @Operation(summary = "解绑商品")
    @DeleteMapping("/{id}/spus")
    public R<Integer> unbindSpus(
            @Parameter(description = "编辑精选ID") @PathVariable("id") Long editorialId,
            @Parameter(description = "商品ID列表") @RequestParam("spuIds") List<Long> spuIds) {
        int count = editorialService.unbindSpuIds(editorialId, spuIds);
        return R.success(count);
    }

    @Operation(summary = "获取已发布编辑精选", description = "内部调用，返回已发布状态的精选列表")
    @GetMapping("/internal/published")
    public R<List<EditorialDTO>> listPublished(
            @Parameter(description = "返回数量，默认10") @RequestParam(defaultValue = "10") Integer limit) {
        List<Editorial> editorials = editorialService.listPublished(limit);
        if (editorials.isEmpty()) {
            return R.success(Collections.emptyList());
        }
        List<EditorialDTO> dtoList = editorialConverter.entityListToDTOList(editorials);

        List<Long> editorialIds = editorials.stream().map(Editorial::getId).toList();
        Map<Long, List<Long>> spuIdsMap = editorialService.getSpuRelationsByEditorialIds(editorialIds)
                .stream()
                .collect(Collectors.groupingBy(
                        EditorialSpuRelation::getEditorialId,
                        Collectors.mapping(EditorialSpuRelation::getSpuId, Collectors.toList())
                ));
        dtoList.forEach(dto -> dto.setSpuIds(spuIdsMap.getOrDefault(dto.getId(), Collections.emptyList())));
        return R.success(dtoList);
    }
}