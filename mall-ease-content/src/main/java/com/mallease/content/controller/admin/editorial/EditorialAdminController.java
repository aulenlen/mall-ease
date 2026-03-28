package com.mallease.content.controller.admin.editorial;

import com.github.pagehelper.PageHelper;
import com.mallease.common.api.Page;
import com.mallease.common.api.PageUtils;
import com.mallease.common.api.R;
import com.mallease.common.api.ResultCode;
import com.mallease.content.controller.admin.editorial.vo.EditorialPageReqVO;
import com.mallease.content.controller.admin.editorial.vo.EditorialReqVO;
import com.mallease.content.controller.admin.editorial.vo.EditorialRespVO;
import com.mallease.content.convert.editorial.EditorialConvert;
import com.mallease.content.dal.entity.Editorial;
import com.mallease.content.service.editorial.EditorialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "编辑精选管理", description = "品牌期刊增删改查")
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/content/editorials")
public class EditorialAdminController {

    private final EditorialConvert editorialConvert;
    private final EditorialService editorialService;

    @Operation(summary = "创建编辑精选")
    @PostMapping
    public R<Long> create(@Validated(EditorialReqVO.Create.class) @RequestBody EditorialReqVO reqVO) {
        Editorial editorial = editorialConvert.toEditorial(reqVO);
        return R.success(editorialService.create(editorial, reqVO.getSpuIds()));
    }

    @Operation(summary = "更新编辑精选")
    @PutMapping
    public R<Integer> update(@Validated(EditorialReqVO.Update.class) @RequestBody EditorialReqVO reqVO) {
        Editorial editorial = editorialConvert.toEditorial(reqVO);
        int count = editorialService.update(editorial, reqVO.getSpuIds());
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "删除编辑精选")
    @DeleteMapping("/{id}")
    public R<Integer> delete(@PathVariable Long id) {
        int count = editorialService.delete(id);
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "批量删除编辑精选")
    @DeleteMapping("/batch")
    public R<Integer> deleteBatch(@Parameter(description = "编辑精选ID列表") @RequestParam("ids") List<Long> ids) {
        int count = editorialService.deleteBatch(ids);
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "查询编辑精选详情")
    @GetMapping("/{id}")
    public R<EditorialRespVO> get(@PathVariable Long id) {
        Editorial editorial = editorialService.get(id);
        if (editorial == null) {
            return R.failed("编辑精选不存在");
        }
        EditorialRespVO respVO = editorialConvert.toEditorialResp(editorial);
        respVO.setSpuIds(editorialService.listSpuIds(id));
        return R.success(respVO);
    }

    @Operation(summary = "分页查询编辑精选")
    @GetMapping
    public R<Page<EditorialRespVO>> page(@ParameterObject EditorialPageReqVO reqVO) {
        PageHelper.startPage(reqVO.getPageNum(), reqVO.getPageSize());
        List<Editorial> editorialList = editorialService.page(reqVO);
        return R.success(PageUtils.convertPage(editorialList, editorialConvert::toEditorialRespList));
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
    public R<Integer> bindSpus(@PathVariable("id") Long editorialId, @RequestBody List<Long> spuIds) {
        return R.success(editorialService.bindSpuIds(editorialId, spuIds));
    }

    @Operation(summary = "解绑商品")
    @DeleteMapping("/{id}/spus")
    public R<Integer> unbindSpus(@PathVariable("id") Long editorialId, @RequestParam("spuIds") List<Long> spuIds) {
        return R.success(editorialService.unbindSpuIds(editorialId, spuIds));
    }
}
