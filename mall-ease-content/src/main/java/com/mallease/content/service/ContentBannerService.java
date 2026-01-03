package com.mallease.content.service;

import com.mallease.content.pojo.ContentBanner;

import java.util.List;

public interface ContentBannerService {
    /**
     * 创建轮播图
     *
     * @param banner 轮播图
     * @return 影响行数
     */
    int create(ContentBanner banner);

    /**
     * 更新轮播图
     *
     * @param banner 轮播图
     * @return 影响行数
     */
    int update(ContentBanner banner);

    /**
     * 删除 banner
     *
     * @param id banner ID
     * @return 影响行数
     */
    int delete(Long id);

    /**
     * 根据ID获取 banner
     *
     * @param id banner ID
     * @return banner 信息
     */
    ContentBanner getById(Long id);

    /**
     * 根据关键字分页查询轮播图列表
     *
     * @param keyword 关键字（名称模糊匹配）
     * @return 轮播图列表
     */
    List<ContentBanner> listByKeyword(String keyword);

    /**
     * 批量更新启用状态
     * @param ids banner id列表
     * @param status 0-禁用 1-启用
     * @return 更新的记录数
     */
    int updateStatusBatch(List<Long> ids, Integer status);
}
