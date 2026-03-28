package com.mallease.bff.controller.portal.home;

import com.mallease.bff.controller.portal.home.vo.HomePageRespVO;
import com.mallease.bff.service.home.HomeService;
import com.mallease.common.api.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * BFF 首页控制器
 *
 * @author: Aulen
 * @create: 2026-01-08
 */
@Tag(name = "BFF首页", description = "首页聚合数据接口")
@RestController
@RequestMapping("/portal/home")
@RequiredArgsConstructor
public class HomePortalController {

    private final HomeService homeService;

    @Operation(summary = "获取首页数据", description = "聚合返回Banner、分类、秒杀、编辑精选、推荐商品")
    @GetMapping
    public R<HomePageRespVO> getHomeData() {
        return R.success(homeService.getHomePage());
    }
}
