package com.mallease.content.controller.admin.banner;

import com.github.pagehelper.PageHelper;
import com.mallease.common.api.Page;
import com.mallease.common.api.PageUtils;
import com.mallease.common.api.R;
import com.mallease.common.api.ResultCode;
import com.mallease.content.controller.admin.banner.vo.BannerPageReqVO;
import com.mallease.content.controller.admin.banner.vo.BannerReqVO;
import com.mallease.content.controller.admin.banner.vo.BannerRespVO;
import com.mallease.content.convert.banner.BannerConvert;
import com.mallease.content.dal.entity.Banner;
import com.mallease.content.service.banner.BannerService;
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

@Tag(name = "Banner管理", description = "轮播图增删改查")
@RestController
@RequiredArgsConstructor
@RequestMapping("/content/banner")
public class BannerAdminController {

    private final BannerService bannerService;
    private final BannerConvert bannerConvert;

    @Operation(summary = "创建轮播图")
    @PostMapping("/create")
    public R<Integer> create(@Validated(BannerReqVO.Create.class) @RequestBody BannerReqVO reqVO) {
        int count = bannerService.create(bannerConvert.toBanner(reqVO));
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "更新轮播图")
    @PutMapping("/update")
    public R<Integer> update(@Validated(BannerReqVO.Update.class) @RequestBody BannerReqVO reqVO) {
        int count = bannerService.update(bannerConvert.toBanner(reqVO));
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "删除轮播图")
    @DeleteMapping("/delete/{id}")
    public R<Integer> delete(@PathVariable Long id) {
        int count = bannerService.delete(id);
        return count > 0 ? R.success(count) : R.failed(ResultCode.FAILED);
    }

    @Operation(summary = "查询轮播图")
    @GetMapping("/{id}")
    public R<BannerRespVO> get(@PathVariable Long id) {
        Banner banner = bannerService.get(id);
        if (banner == null) {
            return R.failed(ResultCode.FAILED);
        }
        return R.success(bannerConvert.toBannerResp(banner));
    }

    @Operation(summary = "分页查询轮播图")
    @GetMapping
    public R<Page<BannerRespVO>> page(@ParameterObject BannerPageReqVO reqVO) {
        PageHelper.startPage(reqVO.getPageNum(), reqVO.getPageSize());
        List<Banner> bannerList = bannerService.page(reqVO);
        return R.success(PageUtils.convertPage(bannerList, bannerConvert::toBannerRespList));
    }

    @Operation(summary = "批量更新轮播图状态")
    @PutMapping("/status")
    public R<Integer> updateStatus(
            @Parameter(description = "轮播图ID列表") @RequestParam("ids") List<Long> ids,
            @Parameter(description = "状态：0-禁用 1-启用") @RequestParam("status") Integer status) {
        if (status != 0 && status != 1) {
            return R.failed(ResultCode.VALIDATE_FAILED, "状态值只能是 0 或 1");
        }
        int count = bannerService.updateStatusBatch(ids, status);
        return count > 0 ? R.success(count) : R.failed();
    }
}
