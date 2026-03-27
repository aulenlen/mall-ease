package com.mallease.bff.service.home;

import com.mallease.bff.controller.portal.home.vo.HomePageRespVO;

/**
 * 首页聚合服务。
 *
 * @author: Aulen
 * @create: 2026-03-28
 */
public interface HomeService {

    /**
     * 获取首页聚合数据
     */
    HomePageRespVO getHomePage();
}
