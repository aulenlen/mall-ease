package com.mallease.cms.controller;

import com.github.pagehelper.PageHelper;
import com.mallease.cms.converter.CmsBannerConverter;
import com.mallease.cms.dto.cmd.CreateCmsBannerCmd;
import com.mallease.cms.dto.cmd.UpdateCmsBannerCmd;
import com.mallease.cms.dto.query.CmsBannerQuery;
import com.mallease.cms.dto.vo.CmsBannerVO;
import com.mallease.cms.pojo.CmsBanner;
import com.mallease.cms.service.CmsBannerService;
import com.mallease.common.api.Page;
import com.mallease.common.api.PageUtils;
import com.mallease.common.api.R;
import com.mallease.common.api.ResultCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Banner轮播图管理
 *
 * @author: Aulen
 * @create: 2025-01-01
 */
@Tag(name = "Banner管理", description = "轮播图增删改查")
@RestController
@RequestMapping("/cms/banner")
public class CmsBannerController {
    @Autowired
    private CmsBannerService bannerService;
    @Autowired
    private CmsBannerConverter bannerConverter;

    @Operation(summary = "创建轮播图")
    @PostMapping("/create")
    public R<Integer> create(@Validated @RequestBody CreateCmsBannerCmd cmd) {
        CmsBanner banner = bannerConverter.createCmdToEntity(cmd);
        int count = bannerService.create(banner);
        if (count > 0) {
            return R.success(count);
        }
        return R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "更新轮播图")
    @PutMapping("/update")
    public R<Integer> update(@Validated @RequestBody UpdateCmsBannerCmd cmd) {
        CmsBanner banner = bannerConverter.updateCmdToEntity(cmd);
        int count = bannerService.update(banner);
        if (count > 0) {
            return R.success(count);
        }
        return R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "删除轮播图")
    @DeleteMapping("/delete/{id}")
    public R<Integer> delete(@PathVariable Long id) {
        int count = bannerService.delete(id);
        if (count > 0) {
            return R.success(count);
        }
        return R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "查询轮播图")
    @GetMapping("/{id}")
    public R<CmsBannerVO> getById(@PathVariable Long id) {
        CmsBanner banner = bannerService.getById(id);
        if (banner == null) {
            return R.failed(ResultCode.FAILED);
        }
        return R.success(bannerConverter.entityToVo(banner));
    }

    @Operation(summary = "分页查询轮播图")
    @GetMapping
    public R<Page<CmsBannerVO>> list(@ParameterObject CmsBannerQuery query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<CmsBanner> list = bannerService.listByKeyword(
                query.getKeyword());
        Page<CmsBannerVO> result = PageUtils.convertPage(list, bannerConverter::entityListToVoList);
        return R.success(result);
    }

    @Operation(summary = "批量更新轮播图状态")
    @PutMapping("/status")
    public R<Integer> updateStatus(
            @Parameter(description = "轮播图ID列表") @RequestParam(value = "ids") List<Long> ids,
            @Parameter(description = "状态：0-禁用 1-启用") @RequestParam(value = "status") Integer status
    ) {
        if (status != 0 && status != 1) {
            return R.failed(ResultCode.VALIDATE_FAILED, "状态值只能是 0 或 1");
        }
        int count = bannerService.updateStatusBatch(ids, status);
        return count > 0 ? R.success(count) : R.failed();
    }
}
