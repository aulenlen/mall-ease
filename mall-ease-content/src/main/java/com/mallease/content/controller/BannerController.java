package com.mallease.content.controller;

import com.github.pagehelper.PageHelper;
import com.mallease.content.converter.BannerConverter;
import com.mallease.content.model.client.cmd.ContentBannerCmd;
import com.mallease.content.model.client.cmd.UpdateContentBannerCmd;
import com.mallease.content.model.client.query.BannerQuery;
import com.mallease.content.model.client.vo.BannerVO;
import com.mallease.content.model.data.entity.Banner;
import com.mallease.content.service.BannerService;
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
@RequestMapping("/content/banner")
public class BannerController {
    @Autowired
    private BannerService bannerService;
    @Autowired
    private BannerConverter bannerConverter;

    @Operation(summary = "创建轮播图")
    @PostMapping("/create")
    public R<Integer> create(@Validated @RequestBody ContentBannerCmd cmd) {
        Banner banner = bannerConverter.createCmdToEntity(cmd);
        int count = bannerService.create(banner);
        if (count > 0) {
            return R.success(count);
        }
        return R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "更新轮播图")
    @PutMapping("/update")
    public R<Integer> update(@Validated @RequestBody UpdateContentBannerCmd cmd) {
        Banner banner = bannerConverter.updateCmdToEntity(cmd);
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
    public R<BannerVO> getById(@PathVariable Long id) {
        Banner banner = bannerService.getById(id);
        if (banner == null) {
            return R.failed(ResultCode.FAILED);
        }
        return R.success(bannerConverter.entityToVo(banner));
    }

    @Operation(summary = "分页查询轮播图")
    @GetMapping
    public R<Page<BannerVO>> list(@ParameterObject BannerQuery query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<Banner> list = bannerService.listByKeyword(
                query.getKeyword());
        Page<BannerVO> result = PageUtils.convertPage(list, bannerConverter::entityListToVoList);
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
