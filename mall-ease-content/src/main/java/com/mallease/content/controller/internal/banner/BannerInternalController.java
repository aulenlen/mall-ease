package com.mallease.content.controller.internal.banner;

import com.mallease.common.api.R;
import com.mallease.common.dto.remote.BannerDTO;
import com.mallease.content.convert.banner.BannerConvert;
import com.mallease.content.dal.entity.Banner;
import com.mallease.content.service.banner.BannerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "轮播图内部接口")
@RestController
@RequiredArgsConstructor
@RequestMapping("/content/banner/internal")
public class BannerInternalController {

    private final BannerService bannerService;
    private final BannerConvert bannerConvert;

    @Operation(summary = "获取首页轮播图")
    @GetMapping("/published")
    public R<List<BannerDTO>> listPublished(
            @Parameter(description = "投放位置，默认home") @RequestParam(defaultValue = "home") String position) {
        List<Banner> bannerList = bannerService.listPublishedByPosition(position);
        return R.success(bannerConvert.toBannerRemoteList(bannerList));
    }
}
